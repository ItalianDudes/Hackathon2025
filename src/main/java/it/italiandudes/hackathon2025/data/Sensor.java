package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.data.generator.DataGenerator;
import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.interfaces.IDatabaseInteractable;
import it.italiandudes.idl.javafx.JFXUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Sensor implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private final Sector sector;
    @NotNull private String name;
    @NotNull private String ip;
    private int port;
    @NotNull private final DataType outputType;
    @NotNull private final String unitOfMeasurement;
    @Nullable private final DataGenerator dataGenerator;
    private boolean sensorDeleted = false;

    // Constructors
    public Sensor(final long uuid) throws SQLException {
        this.uuid = uuid;
        String query = "SELECT * FROM sensors WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.name = result.getString("name");
            this.sector = new Sector(result.getLong("sector_id"));
            this.ip = result.getString("ip");
            this.port = result.getInt("port");
            String outputType = result.getString("output_type");
            String unitOfMeasurement = result.getString("unit_of_measurement");
            String preset = result.getString("preset");
            if (result.wasNull()) {
                this.dataGenerator = null;
                this.unitOfMeasurement = unitOfMeasurement;
                this.outputType = DataType.valueOf(outputType);
            } else {
                SensorPreset sensorPreset = SensorPreset.valueOf(preset);
                this.dataGenerator = sensorPreset.getDataGenerator();
                this.dataGenerator.setSensor(this);
                this.unitOfMeasurement = sensorPreset.getUnitOfMeasurement();
                this.outputType = sensorPreset.getDataType();
                JFXUtils.startVoidServiceTask(dataGenerator);
            }
        } else throw new SQLException("Sensor #" + uuid + " not found");
        ps.close();
    }
    public Sensor(@NotNull final Sector sector, @NotNull String name, @NotNull String ip, int port, @NotNull final SensorPreset preset) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.sector = sector;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.outputType = preset.getDataType();
        this.unitOfMeasurement = preset.getUnitOfMeasurement();
        this.dataGenerator = preset.getDataGenerator();
        this.dataGenerator.setSensor(this);
        save(preset);
        JFXUtils.startVoidServiceTask(dataGenerator);
    }
    public Sensor(@NotNull final Sector sector, @NotNull String name, @NotNull String ip, int port, @NotNull final DataType outputType, @NotNull final String unitOfMeasurement) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.sector = sector;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.outputType = outputType;
        this.unitOfMeasurement = unitOfMeasurement;
        this.dataGenerator = null;
        save();
    }

    // Save&Update
    public void save(@NotNull final SensorPreset preset) throws SQLException {
        String query = "INSERT INTO sensors (id, sector_id, name, ip, port, output_type, unit_of_measurement, preset) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, sector.getUuid());
        ps.setString(3, name);
        ps.setString(4, ip);
        ps.setInt(5, port);
        ps.setString(6, outputType.name());
        ps.setString(7, unitOfMeasurement);
        ps.setString(8, preset.name());
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO sensors (id, sector_id, name, ip, port, output_type, unit_of_measurement, preset) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, sector.getUuid());
        ps.setString(3, name);
        ps.setString(4, ip);
        ps.setInt(5, port);
        ps.setString(6, outputType.name());
        ps.setString(7, unitOfMeasurement);
        ps.setNull(8, Types.VARCHAR);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void update() throws SQLException {
        String query = "UPDATE sensors SET name=?, ip=?, port=? WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setString(1, name);
        ps.setString(2, ip);
        ps.setInt(3, port);
        ps.setLong(4, uuid);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void delete() throws SQLException {
        String query = "DELETE FROM sensors WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.executeUpdate();
        ps.close();
        sensorDeleted = true;
    }

    // Methods
    public void setName(@NotNull String name) throws SQLException {
        this.name = name;
        update();
    }
    public void setIp(@NotNull String ip) throws SQLException {
        this.ip = ip;
        update();
    }
    public void setPort(int port) throws SQLException {
        this.port = port;
        update();
    }

    // Equals&Hashcode
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Sensor sensor)) return false;

        return getUuid() == sensor.getUuid() && getPort() == sensor.getPort() && getSector().equals(sensor.getSector()) && getName().equals(sensor.getName()) && getIp().equals(sensor.getIp()) && getOutputType() == sensor.getOutputType() && getUnitOfMeasurement().equals(sensor.getUnitOfMeasurement()) && Objects.equals(getDataGenerator(), sensor.getDataGenerator());
    }
    @Override
    public int hashCode() {
        int result = Long.hashCode(getUuid());
        result = 31 * result + getSector().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getIp().hashCode();
        result = 31 * result + getPort();
        result = 31 * result + getOutputType().hashCode();
        result = 31 * result + getUnitOfMeasurement().hashCode();
        result = 31 * result + Objects.hashCode(getDataGenerator());
        return result;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return name;
    }
}
