package org.example.weatherdataingester.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidDateRange_shouldReturnBadRequestWithMessage() {
        InvalidDateRangeException ex = new InvalidDateRangeException("Start date must be before end date");
        ResponseEntity<Map<String, String>> response = handler.handleInvalidDateRange(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid date range");
        assertThat(response.getBody()).containsEntry("message", "Start date must be before end date");
    }

    @Test
    void handleInvalidSensor_shouldReturnBadRequestWithMessage() {
        InvalidSensorException ex = new InvalidSensorException("Sensor id: 99 not found");
        ResponseEntity<Map<String, String>> response = handler.handleInvalidDateRange(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid sensor id");
        assertThat(response.getBody()).containsEntry("message", "Sensor id: 99 not found");
    }

    @Test
    void handleValidationErrors_shouldReturnAllFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("metricRequest", "metrics", "must not be null")
        ));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("metrics", "must not be null");
    }
}