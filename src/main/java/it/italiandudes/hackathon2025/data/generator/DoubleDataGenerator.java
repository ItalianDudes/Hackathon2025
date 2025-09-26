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
public final class DoubleDataGenerator extends DataGenerator {

    // Attributes
    private final double minValue;
    private final double maxValue;
    private final double minStep;
    private final double maxStep;
    private final double alarmMinValue;
    private final double alarmMaxValue;
    @NotNull private final FixedSizeQueue<@NotNull Double> dataset;

    // Constructors
    public DoubleDataGenerator(final double minValue, final double maxValue, final double minStep, final double maxStep, final double alarmMinValue, final double alarmMaxValue) {
        this(null, minValue, maxValue, minStep, maxStep, alarmMinValue, alarmMaxValue, DEFAULT_DATASET_SIZE, DEFAULT_GENERATION_INTERVAL);
    }
    public DoubleDataGenerator(@NotNull final Sensor sensor, final double minValue, final double maxValue, final double minStep, final double maxStep, final double alarmMinValue, final double alarmMaxValue) {
        this(sensor, minValue, maxValue, minStep, maxStep, alarmMinValue, alarmMaxValue, DEFAULT_DATASET_SIZE, DEFAULT_GENERATION_INTERVAL);
    }
    public DoubleDataGenerator(@Nullable final Sensor sensor, final double minValue, final double maxValue, final double minStep, final double maxStep, final double alarmMinValue, final double alarmMaxValue, final int datasetSize, @NotNull final Duration generationInterval) {
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
    public double getLastGeneratedValue() {
        return dataset.getLast();
    }
    public List<Double> getCurrentDataset() {
        return dataset.getDataList();
    }

    // Equals&Hashcode
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DoubleDataGenerator that)) return false;
        if (!super.equals(o)) return false;

        return Double.compare(getMinValue(), that.getMinValue()) == 0 && Double.compare(getMaxValue(), that.getMaxValue()) == 0 && Double.compare(getMinStep(), that.getMinStep()) == 0 && Double.compare(getMaxStep(), that.getMaxStep()) == 0 && Double.compare(getAlarmMinValue(), that.getAlarmMinValue()) == 0 && Double.compare(getAlarmMaxValue(), that.getAlarmMaxValue()) == 0 && getDataset().equals(that.getDataset());
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Double.hashCode(getMinValue());
        result = 31 * result + Double.hashCode(getMaxValue());
        result = 31 * result + Double.hashCode(getMinStep());
        result = 31 * result + Double.hashCode(getMaxStep());
        result = 31 * result + Double.hashCode(getAlarmMinValue());
        result = 31 * result + Double.hashCode(getAlarmMaxValue());
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
            Logger.log("DoubleDataGenerator execution blocked: sensor is null!");
        }
        double currentValue = Randomizer.randomBetween(minValue, maxValue);
        dataset.add(currentValue);
        System.out.println(currentValue);

        while (DBManager.isConnectionOpen() && !sensor.isSensorDeleted()) {
            try {
                Thread.sleep(generationInterval);
            } catch (InterruptedException e) {
                Logger.log(e);
                return;
            }
            double step = Randomizer.randomBetween(minStep, maxStep);
            boolean signNegative = Randomizer.randomBetween(0, 100) % 2 == 0;
            if (signNegative) step = -step;
            double nextValue = currentValue + step;
            if (nextValue > maxValue) nextValue = maxValue;
            else if (nextValue < minValue) nextValue = minValue;
            dataset.add(nextValue);
            currentValue = nextValue;
            System.out.println(currentValue);
        }
        Logger.log("DoubleDataGenerator terminated!");
    }
}
