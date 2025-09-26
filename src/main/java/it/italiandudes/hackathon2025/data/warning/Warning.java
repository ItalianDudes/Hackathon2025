package it.italiandudes.hackathon2025.data.warning;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@EqualsAndHashCode
public final class Warning {

    // Attributes
    private final long creationTime = System.currentTimeMillis();
    private final WarningOrigin origin;
    private final WarningSeverity severity;
    private final String reason;

    // Constructors
    public Warning(@NotNull final WarningOrigin origin, @Nullable final WarningSeverity severity, @NotNull final String reason) {
        this.origin = origin;
        this.severity = severity;
        this.reason = reason;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return "[" + origin + "][" + severity + "] " + reason;
    }
}
