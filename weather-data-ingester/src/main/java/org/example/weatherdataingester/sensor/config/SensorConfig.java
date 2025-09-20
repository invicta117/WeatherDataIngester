package org.example.weatherdataingester.sensor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties(prefix = "app")
@Data
public class SensorConfig {
    private List<SensorSource> sensors;

    @Data
    public static class SensorSource {
        private Long id;
        private String name;
        private String url;
    }
}
