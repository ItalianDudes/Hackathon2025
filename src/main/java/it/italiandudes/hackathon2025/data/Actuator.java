package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.interfaces.IDatabaseInteractable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class Actuator implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private final Sector sector;
    @NotNull private String name;
    @NotNull private String ip;
    private int port;
    @NotNull private final DataType inputType;
    private final boolean allowOverride;
    private boolean isOverrideActive;
    @NotNull private final Number minValue;
    @NotNull private final Number maxValue;
    @NotNull private Number value;

    // Constructors
    public Actuator(final long uuid) throws SQLException {
        this.uuid = uuid;
        String query = "SELECT * FROM actuators WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.name = result.getString("name");
            this.sector = new Sector(result.getLong("sector_id"));
            this.ip = result.getString("ip");
            this.port = result.getInt("port");
            this.inputType = DataType.valueOf(result.getString("input_type"));
            String dbValue = result.getString("value");
            String dbMinValue = result.getString("min_value");
            String dbMaxValue = result.getString("max_value");
            if (inputType == DataType.INTEGER) {
                this.value = Integer.parseInt(dbValue);
                this.minValue = Integer.parseInt(dbMinValue);
                this.maxValue = Integer.parseInt(dbMaxValue);
            } else if (inputType == DataType.DOUBLE) {
                this.value = Double.parseDouble(dbValue);
                this.minValue = Double.parseDouble(dbMinValue);
                this.maxValue = Double.parseDouble(dbMaxValue);
            } else throw new RuntimeException("Unexpected input type for actuator #" + uuid);
            this.allowOverride = result.getInt("allow_override") != 0;
            this.isOverrideActive = result.getInt("is_override_active") != 0;
        } else throw new SQLException("Actuator #" + uuid + " not found");
        ps.close();
    }
    public Actuator(@NotNull final Sector sector, @NotNull String name, @NotNull String ip, int port, @NotNull final Number value, @NotNull final Number minValue, @NotNull final Number maxValue, @NotNull final DataType inputType, boolean allowOverride) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.sector = sector;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.inputType = inputType;
        this.allowOverride = allowOverride;
        this.isOverrideActive = false;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        save();
    }

    // Save&Update
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO actuators (id, sector_id, name, ip, port, input_type, allow_override, is_override_active, value, min_value, max_value) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, sector.getUuid());
        ps.setString(3, name);
        ps.setString(4, ip);
        ps.setInt(5, port);
        ps.setString(6, inputType.name());
        ps.setInt(7, allowOverride ? 1 : 0);
        ps.setInt(8, isOverrideActive ? 1 : 0);
        ps.setString(9, value.toString());
        ps.setString(10, minValue.toString());
        ps.setString(11, maxValue.toString());
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void update() throws SQLException {
        String query = "UPDATE actuators SET name=?, ip=?, port=?, is_override_active=?, value=? WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setString(1, name);
        ps.setString(2, ip);
        ps.setInt(3, port);
        ps.setInt(4, isOverrideActive ? 1 : 0);
        ps.setString(5, value.toString());
        ps.setLong(6, uuid);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void delete() throws SQLException {
        String query = "DELETE FROM actuators WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.executeUpdate();
        ps.close();
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
    public void setValue(@NotNull Number value) throws SQLException {
        this.value = value;
        update();
    }
    public void setPort(int port) throws SQLException {
        this.port = port;
        update();
    }
    public void setOverrideStatus(boolean newOverrideStatus) throws SQLException {
        this.isOverrideActive = newOverrideStatus;
        update();
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return name;
    }
}
