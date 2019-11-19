package com.pulsar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// A no-arg constructor is required
@NoArgsConstructor
public class SensorReading {

    @Getter
    @Setter
    private float temperature;

    public SensorReading(float temperature) {
        this.temperature = temperature;
    }
}
