package com.queuectl.repository;

import com.queuectl.db.Database;
import com.queuectl.model.WorkerInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data access repository for managing WorkerInfo entity persistence.
 */
public class WorkerRepository {

    private final Database database;

    /**
     * Constructs a WorkerRepository using constructor injection for Database access.
     *
     * @param database the Database instance to obtain connections from
     */
    public WorkerRepository(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("Database instance cannot be null");
        }
        this.database = database;
    }

    /**
     * Registers a new worker or updates an existing worker record in the database.
     *
     * @param worker the WorkerInfo object to save
     * @return the registered WorkerInfo object
     */
    public WorkerInfo registerWorker(WorkerInfo worker) {
        String sql = "INSERT OR REPLACE INTO workers (id, started_at, last_heartbeat_at, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, worker.getId());
            ps.setLong(2, worker.getStartedAt());
            ps.setLong(3, worker.getLastHeartbeatAt());
            ps.setString(4, worker.getStatus());

            ps.executeUpdate();
            return worker;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register worker: " + worker.getId(), e);
        }
    }

    /**
     * Finds a registered worker by its unique identifier.
     *
     * @param id the worker ID to search for
     * @return an Optional containing the matching WorkerInfo, or empty if not found
     */
    public Optional<WorkerInfo> findWorker(String id) {
        String sql = "SELECT id, started_at, last_heartbeat_at, status FROM workers WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find worker by id: " + id, e);
        }
    }

    /**
     * Retrieves all registered worker records ordered by ID ascending.
     *
     * @return a list of all WorkerInfo records
     */
    public List<WorkerInfo> findAllWorkers() {
        String sql = "SELECT id, started_at, last_heartbeat_at, status FROM workers ORDER BY id ASC";

        List<WorkerInfo> workers = new ArrayList<>();
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                workers.add(mapRow(rs));
            }
            return workers;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all workers", e);
        }
    }

    /**
     * Updates the last heartbeat timestamp for a specific worker.
     *
     * @param id          the worker ID
     * @param heartbeatAt epoch timestamp (ms) of the heartbeat
     * @return true if a worker record was updated, false otherwise
     */
    public boolean updateHeartbeat(String id, long heartbeatAt) {
        String sql = "UPDATE workers SET last_heartbeat_at = ? WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, heartbeatAt);
            ps.setString(2, id);

            int updatedRows = ps.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update heartbeat for worker: " + id, e);
        }
    }

    /**
     * Updates the status string for a specific worker.
     *
     * @param id     the worker ID
     * @param status the new status value
     * @return true if a worker record was updated, false otherwise
     */
    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE workers SET status = ? WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, id);

            int updatedRows = ps.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update status for worker: " + id, e);
        }
    }

    /**
     * Deletes a worker record by its unique identifier.
     *
     * @param id the worker ID to delete
     * @return true if a worker record was deleted, false otherwise
     */
    public boolean deleteWorker(String id) {
        String sql = "DELETE FROM workers WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            int deletedRows = ps.executeUpdate();
            return deletedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete worker: " + id, e);
        }
    }

    /**
     * Computes total registered worker count using database aggregation.
     *
     * @return total count of worker records
     */
    public long countTotalWorkers() {
        String sql = "SELECT COUNT(*) FROM workers";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count total workers", e);
        }
    }

    /**
     * Computes worker counts grouped by status string using database aggregation.
     *
     * @return a Map mapping status string to count
     */
    public java.util.Map<String, Long> countWorkersByStatus() {
        String sql = "SELECT status, COUNT(*) AS cnt FROM workers GROUP BY status";

        java.util.Map<String, Long> counts = new java.util.HashMap<>();
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getLong("cnt"));
            }
            return counts;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count workers by status", e);
        }
    }

    private WorkerInfo mapRow(ResultSet rs) throws SQLException {
        WorkerInfo worker = new WorkerInfo();
        worker.setId(rs.getString("id"));
        worker.setStartedAt(rs.getLong("started_at"));
        worker.setLastHeartbeatAt(rs.getLong("last_heartbeat_at"));
        worker.setStatus(rs.getString("status"));
        return worker;
    }
}
