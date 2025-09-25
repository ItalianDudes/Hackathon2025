package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.interfaces.IDatabaseInteractable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class Terrain implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private String name;
    @NotNull private String owner;
    @NotNull private String dimension;
    @NotNull private ArrayList<@NotNull Sector> sectors;

    // Constructors
    public Terrain(final long uuid) throws SQLException {
        this.uuid = uuid;
        String query = "SELECT * FROM terrains WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.name = result.getString("name");
            this.owner = result.getString("owner");
            this.dimension = result.getString("dimension");
        } else throw new SQLException("Terrain #" + uuid + " not found");
        ps.close();
        this.sectors = Sector.getTerrainSectors(this);
    }
    public Terrain(@NotNull String name, @NotNull String owner, @NotNull String dimension) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.name = name;
        this.owner = owner;
        this.dimension = dimension;
        this.sectors = new ArrayList<>();
        save();
    }

    // Save&Update
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO terrains (id, name, owner, dimension) VALUES (?,?,?,?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setString(2, name);
        ps.setString(3, owner);
        ps.setString(4, dimension);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void update() throws SQLException {
        String query = "UPDATE terrains SET name=?, owner=?, dimension=? WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setString(1, name);
        ps.setString(2, owner);
        ps.setString(3, dimension);
        ps.setLong(4, uuid);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void delete() throws SQLException {
        String query = "DELETE FROM terrains  WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.executeUpdate();
        ps.close();
    }

    // Setter
    public void setName(@NotNull String name) throws SQLException {
        this.name = name;
        update();
    }
    public void setOwner(@NotNull String owner) throws SQLException {
        this.owner = owner;
        update();
    }
    public void setDimension(@NotNull String dimension) throws SQLException {
        this.dimension = dimension;
        update();
    }

    // Equals&Hashcode
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Terrain terrain)) return false;

        return getUuid() == terrain.getUuid() && getName().equals(terrain.getName()) && getOwner().equals(terrain.getOwner()) && getDimension().equals(terrain.getDimension()) && getSectors().equals(terrain.getSectors());
    }
    @Override
    public int hashCode() {
        int result = Long.hashCode(getUuid());
        result = 31 * result + getName().hashCode();
        result = 31 * result + getOwner().hashCode();
        result = 31 * result + getDimension().hashCode();
        result = 31 * result + getSectors().hashCode();
        return result;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return name;
    }
}
