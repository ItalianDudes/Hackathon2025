package it.italiandudes.hackathon2025.data;

import it.italiandudes.hackathon2025.db.DBManager;
import it.italiandudes.hackathon2025.interfaces.IDatabaseInteractable;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Sector implements IDatabaseInteractable {

    // Attributes
    private final long uuid;
    @NotNull private final Terrain terrain;
    @NotNull private String name;
    @Nullable private String dimension;

    // Constructors
    public Sector(final long uuid) throws SQLException {
        this.uuid = uuid;
        String query = "SELECT * FROM sectors WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.name = result.getString("name");
            this.dimension = result.getString("dimension");
            this.terrain = new Terrain(result.getLong("terrain_id"));
        } else throw new SQLException("Sector #" + uuid + " not found");
        ps.close();
    }
    public Sector(@NotNull final Terrain terrain, @NotNull final String name, @Nullable final String dimension) throws SQLException {
        this.uuid = UUID.randomUUID().getMostSignificantBits();
        this.terrain = terrain;
        this.name = name;
        this.dimension = dimension;
        save();
    }
    public Sector(final long uuid, @NotNull final Terrain terrain) throws SQLException {
        this.uuid = uuid;
        String query = "SELECT * FROM sectors WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.name = result.getString("name");
            this.dimension = result.getString("dimension");
            this.terrain = terrain;
        } else throw new SQLException("Sector #" + uuid + " not found");
        ps.close();
    }

    // Methods
    @NotNull
    public static ArrayList<@NotNull Sector> getTerrainSectors(@NotNull final Terrain terrain) throws SQLException {
        @NotNull final ArrayList<@NotNull Sector> sectors = new ArrayList<>();
        String query = "SELECT id FROM sectors WHERE terrain_id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, terrain.getUuid());
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            sectors.add(new Sector(result.getLong("id"), terrain));
        }
        ps.close();
        return sectors;
    }

    // Save&Update
    @Override
    public void save() throws SQLException {
        String query = "INSERT INTO sectors (id, terrain_id, name, dimension) VALUES (?,?,?,?);";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.setLong(2, terrain.getUuid());
        ps.setString(3, name);
        if (dimension == null) ps.setNull(4, Types.VARCHAR);
        else ps.setString(4, dimension);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void update() throws SQLException {
        String query = "UPDATE sectors SET name=?, dimension=? WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setString(1, name);
        if (dimension == null) ps.setNull(2, Types.VARCHAR);
        else ps.setString(2, dimension);
        ps.setLong(3, uuid);
        ps.executeUpdate();
        ps.close();
    }
    @Override
    public void delete() throws SQLException {
        String query = "DELETE FROM sectors  WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Database connection is null!");
        ps.setLong(1, uuid);
        ps.executeUpdate();
        ps.close();
    }

    // Getter
    @NotNull
    public String getName() {
        return name;
    }
    public String getDimension() {
        if ((dimension == null || dimension.isEmpty()) && terrain.getSectors().size() == 1) {
            return terrain.getDimension();
        } else return dimension;
    }

    // Setter
    public void setName(String name) throws SQLException {
        this.name = name;
        update();
    }
    public void setDimension(String dimension) throws SQLException {
        this.dimension = dimension;
        update();
    }

    // Equals&Hashcode
    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Sector sector)) return false;

        return getUuid() == sector.getUuid() && getTerrain().equals(sector.getTerrain()) && Objects.equals(getName(), sector.getName()) && Objects.equals(getDimension(), sector.getDimension());
    }
    @Override
    public int hashCode() {
        int result = Long.hashCode(getUuid());
        result = 31 * result + getTerrain().hashCode();
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getDimension());
        return result;
    }

    // ToString
    @Override @NotNull
    public String toString() {
        return getName();
    }
}
