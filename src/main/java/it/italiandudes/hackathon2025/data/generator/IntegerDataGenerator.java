package it.italiandudes.hackathon2025.data.generator;

import it.italiandudes.hackathon2025.data.FixedSizeQueue;
import it.italiandudes.hackathon2025.data.Sensor;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.utils.Randomizer;
import it.italiandudes.idl.logger.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

@Getter
public final class IntegerDataGenerator extends DataGenerator {

    // Attributes
    private final int minValue;
    private final int maxValue;
    private final int minStep;
    private final int maxStep;
    private final int alarmMinValue;
    private final int alarmMaxValue;
    @NotNull private final FixedSizeQueue<@NotNull Integer> dataset;

    // Constructors
    public IntegerDataGenerator(final int minValue, final int maxValue, final int minStep, final int maxStep, final int alarmMinValue, final int alarmMaxValue) {
        this(null, minValue, maxValue, minStep, maxStep, alarmMinValue, alarmMaxValue, DEFAULT_DATASET_SIZE, DEFAULT_GENERATION_INTERVAL);
    }
    public IntegerDataGenerator(@NotNull final Sensor sensor, final int minValue, final int maxValue, final int minStep, final int maxStep, final int alarmMinValue, final int alarmMaxValue) {
        this(sensor, minValue, maxValue, minStep, maxStep, alarmMinValue, alarmMaxValue, DEFAULT_DATASET_SIZE, DEFAULT_GENERATION_INTERVAL);
    }
    public IntegerDataGenerator(@Nullable final Sensor sensor, final int minValue, final int maxValue, final int minStep, final int maxStep, final int alarmMinValue, final int alarmMaxValue, final int datasetSize, @NotNull final Duration generationInterval) {
        super(sensor, generationInterval);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minStep = minStep;
        this.maxStep = maxStep;
        this.alarmMinValue = alarmMinValue;
        this.alarmMaxValue = alarmMaxValue;
        this.dataset = new FixedSizeQueue<>(datasetSize);
    }

    // Methods
    public int getLastGeneratedValue() {
        return dataset.getLast();
    }
    public List<Integer> getCurrentDataset() {
        return dataset.getDataList();
    }

    // Equals&Hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntegerDataGenerator that)) return false;
        if (!super.equals(o)) return false;
        return getMinValue() == that.getMinValue() && getMaxValue() == that.getMaxValue() && getMinStep() == that.getMinStep() && getMaxStep() == that.getMaxStep() && getAlarmMinValue() == that.getAlarmMinValue() && getAlarmMaxValue() == that.getAlarmMaxValue() && getDataset().equals(that.getDataset());
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getMinValue();
        result = 31 * result + getMaxValue();
        result = 31 * result + getMinStep();
        result = 31 * result + getMaxStep();
        result = 31 * result + getAlarmMinValue();
        result = 31 * result + getAlarmMaxValue();
        result = 31 * result + getDataset().hashCode();
        return result;
    }

    // ToString
    @Override
    public String toString() {
        return dataset.toString();
    }

    // Run
    @Override
    public void run() {
        if (sensor == null) {
            Logger.log("IntegerDataGenerator execution blocked: sensor is null!");
            return;
        }

        int currentValue = Randomizer.randomBetween(minValue, maxValue);
        dataset.add(currentValue);
        // System.out.println(currentValue);
        while (DBManager.isConnectionOpen() && !sensor.isSensorDeleted()) {
            try {
                Thread.sleep(generationInterval);
            } catch (InterruptedException e) {
                Logger.log(e);
                return;
            }
            int step = Randomizer.randomBetween(minStep, maxStep);
            boolean signNegative = Randomizer.randomBetween(0, 100) % 2 == 0;
            if (signNegative) step = -step;
            int nextValue = currentValue + step;
            if (nextValue > maxValue) nextValue = maxValue;
            else if (nextValue < minValue) nextValue = minValue;
            dataset.add(nextValue);
            currentValue = nextValue;
            // System.out.println(currentValue);
        }
        // Logger.log("IntegerDataGenerator terminated!");
    }
}
