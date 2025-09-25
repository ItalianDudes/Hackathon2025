package it.italiandudes.hackathon2025.interfaces;

import java.sql.SQLException;

public interface IDatabaseInteractable {
    void save() throws SQLException;
    void update() throws SQLException;
    void delete() throws SQLException;
}
