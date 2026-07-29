package com.queuectl.repository;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data access repository for managing Job entity persistence.
 */
public class JobRepository {

    private final Database database;

    /**
     * Constructs a JobRepository using constructor injection for Database access.
     *
     * @param database the Database instance to obtain connections from
     */
    public JobRepository(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("Database instance cannot be null");
        }
        this.database = database;
    }

    /**
     * Inserts a new Job record into the database and populates its generated primary key ID.
     *
     * @param job the Job to insert
     * @return the inserted Job entity with generated ID populated
     */
    public Job insert(Job job) {
        String sql = "INSERT INTO jobs (command, status, attempts, max_attempts, available_at, "
                + "locked_at, locked_by, lease_expires_at, last_error, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setJobParameters(ps, job);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    job.setId(rs.getLong(1));
                }
            }
            return job;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert job: " + job.getCommand(), e);
        }
    }

    /**
     * Finds a Job by its unique primary key ID.
     *
     * @param id the job ID to search for
     * @return an Optional containing the matching Job, or empty if not found
     */
    public Optional<Job> findById(long id) {
        String sql = "SELECT id, command, status, attempts, max_attempts, available_at, "
                + "locked_at, locked_by, lease_expires_at, last_error, created_at, updated_at "
                + "FROM jobs WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find job by id: " + id, e);
        }
    }

    /**
     * Retrieves all jobs present in the database ordered by ID ascending.
     *
     * @return a list of all Job records
     */
    public List<Job> findAll() {
        String sql = "SELECT id, command, status, attempts, max_attempts, available_at, "
                + "locked_at, locked_by, lease_expires_at, last_error, created_at, updated_at "
                + "FROM jobs ORDER BY id ASC";

        List<Job> jobs = new ArrayList<>();
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                jobs.add(mapRow(rs));
            }
            return jobs;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all jobs", e);
        }
    }

    /**
     * Finds all jobs matching a specific JobStatus filter.
     *
     * @param status the status to filter jobs by
     * @return a list of matching Job records
     */
    public List<Job> findByStatus(JobStatus status) {
        String sql = "SELECT id, command, status, attempts, max_attempts, available_at, "
                + "locked_at, locked_by, lease_expires_at, last_error, created_at, updated_at "
                + "FROM jobs WHERE status = ? ORDER BY id ASC";

        List<Job> jobs = new ArrayList<>();
        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jobs.add(mapRow(rs));
                }
            }
            return jobs;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find jobs by status: " + status, e);
        }
    }

    /**
     * Computes job counts grouped by JobStatus.
     *
     * @return a Map mapping each JobStatus to its current total job count
     */
    public Map<JobStatus, Long> countByStatus() {
        String sql = "SELECT status, COUNT(*) AS cnt FROM jobs GROUP BY status";

        Map<JobStatus, Long> counts = new EnumMap<>(JobStatus.class);
        for (JobStatus status : JobStatus.values()) {
            counts.put(status, 0L);
        }

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JobStatus status = JobStatus.valueOf(rs.getString("status"));
                long count = rs.getLong("cnt");
                counts.put(status, count);
            }
            return counts;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count jobs by status", e);
        }
    }

    /**
     * Updates an existing Job record in the database.
     *
     * @param job the Job entity containing updated values
     * @return true if a record was updated, false otherwise
     */
    public boolean update(Job job) {
        String sql = "UPDATE jobs SET command = ?, status = ?, attempts = ?, max_attempts = ?, "
                + "available_at = ?, locked_at = ?, locked_by = ?, lease_expires_at = ?, "
                + "last_error = ?, created_at = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setJobParameters(ps, job);
            ps.setLong(12, job.getId());

            int updatedRows = ps.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update job: " + job.getId(), e);
        }
    }

    /**
     * Deletes a Job record by its primary key ID.
     *
     * @param id the job ID to delete
     * @return true if a record was deleted, false otherwise
     */
    public boolean delete(long id) {
        String sql = "DELETE FROM jobs WHERE id = ?";

        try (Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int deletedRows = ps.executeUpdate();
            return deletedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete job by id: " + id, e);
        }
    }

    private void setJobParameters(PreparedStatement ps, Job job) throws SQLException {
        ps.setString(1, job.getCommand());
        ps.setString(2, job.getStatus().name());
        ps.setInt(3, job.getAttempts());
        ps.setInt(4, job.getMaxAttempts());
        ps.setLong(5, job.getAvailableAt());

        if (job.getLockedAt() != null) {
            ps.setLong(6, job.getLockedAt());
        } else {
            ps.setNull(6, Types.BIGINT);
        }

        if (job.getLockedBy() != null) {
            ps.setString(7, job.getLockedBy());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }

        if (job.getLeaseExpiresAt() != null) {
            ps.setLong(8, job.getLeaseExpiresAt());
        } else {
            ps.setNull(8, Types.BIGINT);
        }

        if (job.getLastError() != null) {
            ps.setString(9, job.getLastError());
        } else {
            ps.setNull(9, Types.VARCHAR);
        }

        ps.setLong(10, job.getCreatedAt());
        ps.setLong(11, job.getUpdatedAt());
    }

    private Job mapRow(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getLong("id"));
        job.setCommand(rs.getString("command"));
        job.setStatus(JobStatus.valueOf(rs.getString("status")));
        job.setAttempts(rs.getInt("attempts"));
        job.setMaxAttempts(rs.getInt("max_attempts"));
        job.setAvailableAt(rs.getLong("available_at"));

        long lockedAt = rs.getLong("locked_at");
        job.setLockedAt(rs.wasNull() ? null : lockedAt);

        job.setLockedBy(rs.getString("locked_by"));

        long leaseExpiresAt = rs.getLong("lease_expires_at");
        job.setLeaseExpiresAt(rs.wasNull() ? null : leaseExpiresAt);

        job.setLastError(rs.getString("last_error"));
        job.setCreatedAt(rs.getLong("created_at"));
        job.setUpdatedAt(rs.getLong("updated_at"));
        return job;
    }
}
