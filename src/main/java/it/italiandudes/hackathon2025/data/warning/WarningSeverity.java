package it.italiandudes.hackathon2025.data.warning;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum WarningSeverity {
    INFO("Informazione", "#2196F3"),
    NOTICE("Avviso", "#D3D3D3"),
    MINOR("Minore", "#FFEB3B"),
    MODERATE("Moderato", "#FFC107"),
    MAJOR("Grave", "#FF9800"),
    CRITICAL("Critico", "#F44336"),
    EMERGENCY("Emergenza","#B71C1C");

    // Attributes
    @NotNull public final String displayName;
    @NotNull public final String cellColor;

    // Constructor
    WarningSeverity(@NotNull final String displayName, @NotNull final String cellColor) {
        this.displayName = displayName;
        this.cellColor = cellColor;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return displayName;
    }
}
