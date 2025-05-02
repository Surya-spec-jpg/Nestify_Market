package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlacklistDAO {
    private Connection conn;

    public BlacklistDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean blacklistUser(int userId, String reason) throws SQLException {
        String query = "INSERT INTO blacklist (user_id, reason) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, reason);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

}
