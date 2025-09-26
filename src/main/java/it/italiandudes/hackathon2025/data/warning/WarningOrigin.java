package it.italiandudes.hackathon2025.data.warning;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum WarningOrigin {
    ACTUATOR_OVERRIDE("Override Attuatore"),
    SENSOR("Sensore");

    // Attributes
    @NotNull public final String displayName;

    // Constructor
    WarningOrigin(@NotNull final String displayName) {
        this.displayName = displayName;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return displayName;
    }
}
