package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.data.generator.DataGenerator;
import it.italiandudes.hackathon2025.data.generator.DoubleDataGenerator;
import it.italiandudes.hackathon2025.data.generator.IntegerDataGenerator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum SensorPreset {
    EXTERNAL_TEMPERATURE("Temperatura Aria","°C", new DoubleDataGenerator(-20, 50, 0.1, 1, -10, 40)),
    INTERNAL_TEMPERATURE("Temperatura Suolo","°C", new DoubleDataGenerator(0, 35, 0.1, 0.5, 8, 30)),
    EXTERNAL_HUMIDITY("Umidita' Aria","%", new DoubleDataGenerator(0, 100, 0.5, 5, 20, 90)),
    INTERNAL_HUMIDITY("Umidita' Suolo","%", new DoubleDataGenerator(0, 100, 0.1, 2, 10, 80)),
    PH("Acidita'","pH",new DoubleDataGenerator(4.5, 8.5, 0.001, 0.1, 5.5, 7.8)),
    NITROGEN("Concentrazione Azoto","mg/Kg (ppm)", new IntegerDataGenerator(10, 100, 1, 10, 20, 80)),
    PHOSPHORUS("Concentrazione Fosforo","mg/Kg (ppm)", new IntegerDataGenerator(5, 80, 1, 5, 15, 60)),
    POTASSIUM("Concentrazione Potassio","mg/Kg (ppm)", new IntegerDataGenerator(50, 500, 5, 20, 100, 400)),
    CARBON("Concentrazione Carbonio","%", new DoubleDataGenerator(0.5, 4.0, 0.1, 0.5, 1.0, 3.0)),
    CALCIUM("Concentrazione Calcio","mg/Kg (ppm)", new IntegerDataGenerator(500, 6000, 50, 200, 1000, 5000)),
    MAGNESIUM("Concentrazione Magnesio","mg/Kg (ppm)", new IntegerDataGenerator(50, 600, 10, 50, 100, 400)),
    SALTS("Concentrazione Sali Minerali","dS/m", new DoubleDataGenerator(0.1, 3.0, 0.05, 0.2, 0.3, 2.0)),
    IRON("Concentrazione Ferro","mg/Kg (ppm)", new IntegerDataGenerator(5, 200, 1, 10, 10, 150)),
    ZINC("Concentrazione Zinco","mg/Kg (ppm)", new DoubleDataGenerator(0.5, 20.0, 0.1, 2.0, 1.0, 15.0)),
    BORON("Concentrazione Boro","mg/Kg (ppm)", new DoubleDataGenerator(0.1, 5.0, 0.05, 0.5, 0.3, 3.0)),
    ARSENIC("Concentrazione Arsenico","µg/L", new DoubleDataGenerator(0.0, 50.0, 0.1, 5.0, 10.0, 30.0)),
    NITRATES("Concentrazione Nitrati","mg/L", new DoubleDataGenerator(0.0, 100.0, 0.5, 10.0, 25.0, 50.0)),
    LEAD("Concentrazione Piombo","µg/L", new DoubleDataGenerator(0.0, 100.0, 0.1, 10.0, 10.0, 50.0)),
    CADMIUM("Concentrazione Cadmio","µg/L", new DoubleDataGenerator(0.0, 10.0, 0.05, 1.0, 2.0, 5.0)),
    CHROMIUM("Concentrazione Cromo","µg/L", new DoubleDataGenerator(0.0, 100.0, 0.1, 10.0, 25.0, 70.0)),
    MERCURY("Concentrazione Mercurio","µg/L", new DoubleDataGenerator(0.0, 5.0, 0.01, 0.5, 1.0, 3.0));


    // Attributes
    @NotNull public final String displayName;
    @NotNull public final String unitOfMeasurement;
    @NotNull public final DataGenerator dataGenerator;
    @NotNull public final DataType dataType;

    // Constructor
    SensorPreset(@NotNull final String displayName, @NotNull final String unitOfMeasurement, @NotNull final DataGenerator dataGenerator) {
        this.displayName = displayName;
        this.unitOfMeasurement = unitOfMeasurement;
        this.dataGenerator = dataGenerator;
        if (dataGenerator instanceof IntegerDataGenerator) {
            dataType = DataType.INTEGER;
        } else if (dataGenerator instanceof DoubleDataGenerator) {
            dataType = DataType.DOUBLE;
        } else throw new RuntimeException("DataGenerator not instance of Integer or Double DataGenerator");
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return displayName + " [" + unitOfMeasurement + "]";
    }
}
