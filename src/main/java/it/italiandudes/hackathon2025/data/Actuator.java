package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.interfaces.IDatabaseInteractable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
public class Actuator implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private final Sector sector;
    @NotNull private String name;
    @NotNull private String ip;
    private int port;
    @NotNull private final DataType inputType;
    private final boolean allowOverride;

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
            this.allowOverride = result.getInt("allow_override") != 0;
        } else throw new SQLException("Actuator #" + uuid + " not found");
        ps.close();
    }
    public Actuator(@NotNull final Sector sector, @NotNull String name, @NotNull String ip, int port, @NotNull final DataType inputType, boolean allowOverride) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.sector = sector;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.inputType = inputType;
        this.allowOverride = allowOverride;
        save();
    }

    // Save&Update
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO actuators (id, sector_id, name, ip, port, input_type, allow_override) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, sector.getUuid());
        ps.setString(3, name);
        ps.setString(4, ip);
        ps.setInt(5, port);
        ps.setString(6, inputType.name());
        ps.setInt(7, allowOverride ? 1 : 0);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void update() throws SQLException {
        String query = "UPDATE actuators SET name=?, ip=?, port=? WHERE id=?;";
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
    public void setPort(int port) throws SQLException {
        this.port = port;
        update();
    }

    // Equals&Hashcode
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Actuator actuator)) return false;

        return getUuid() == actuator.getUuid() && getPort() == actuator.getPort() && isAllowOverride() == actuator.isAllowOverride() && getSector().equals(actuator.getSector()) && getName().equals(actuator.getName()) && getIp().equals(actuator.getIp()) && getInputType() == actuator.getInputType();
    }
    @Override
    public int hashCode() {
        int result = Long.hashCode(getUuid());
        result = 31 * result + getSector().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getIp().hashCode();
        result = 31 * result + getPort();
        result = 31 * result + getInputType().hashCode();
        result = 31 * result + Boolean.hashCode(isAllowOverride());
        return result;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return name;
    }
}
