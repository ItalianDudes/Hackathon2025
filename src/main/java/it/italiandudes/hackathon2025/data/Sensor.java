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
public class Sensor implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private final Sector sector;
    @NotNull private String name;
    @NotNull private String ip;
    private int port;
    @NotNull private final DataType outputType;

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
            this.outputType = DataType.valueOf(result.getString("output_type"));
        } else throw new SQLException("Sensor #" + uuid + " not found");
        ps.close();
    }
    public Sensor(@NotNull final Sector sector, @NotNull String name, @NotNull String ip, int port, @NotNull final DataType outputType) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.sector = sector;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.outputType = outputType;
        save();
    }

    // Save&Update
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO sensors (id, sector_id, name, ip, port, output_type) VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, sector.getUuid());
        ps.setString(3, name);
        ps.setString(4, ip);
        ps.setInt(5, port);
        ps.setString(6, outputType.name());
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
        return getUuid() == sensor.getUuid() && getPort() == sensor.getPort() && getSector().equals(sensor.getSector()) && getName().equals(sensor.getName()) && getIp().equals(sensor.getIp()) && getOutputType() == sensor.getOutputType();
    }
    @Override
    public int hashCode() {
        int result = Long.hashCode(getUuid());
        result = 31 * result + getSector().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getIp().hashCode();
        result = 31 * result + getPort();
        result = 31 * result + getOutputType().hashCode();
        return result;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return name;
    }
}
