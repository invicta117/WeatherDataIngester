package org.example.weatherdataingester.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSensorException extends RuntimeException {
    public InvalidSensorException(String message) {
        super(message);
    }
}
