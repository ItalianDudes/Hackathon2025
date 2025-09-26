package it.italiandudes.hackathon2025.data.generator;

import it.italiandudes.hackathon2025.data.Sensor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@EqualsAndHashCode
public abstract class DataGenerator implements Runnable {

    // Constants
    protected static final int DEFAULT_DATASET_SIZE = 20;
    protected static final Duration DEFAULT_GENERATION_INTERVAL = Duration.of(3, ChronoUnit.SECONDS);

    // Attributes
    @Nullable protected Sensor sensor;
    @NotNull protected final Duration generationInterval;

    // Constructors
    public DataGenerator(@NotNull final Sensor sensor) {
        this(sensor, DEFAULT_GENERATION_INTERVAL);
    }
    public DataGenerator(@Nullable Sensor sensor, @NotNull final Duration generationInterval) {
        this.sensor = sensor;
        this.generationInterval = generationInterval;
    }
    public DataGenerator() {
        this(DEFAULT_GENERATION_INTERVAL);
    }
    public DataGenerator(@NotNull final Duration generationInterval) {
        this.sensor = null;
        this.generationInterval = generationInterval;
    }

    // Methods
    public void setSensor(@NotNull final Sensor sensor) {
        if (this.sensor == null) this.sensor = sensor;
    }
}
