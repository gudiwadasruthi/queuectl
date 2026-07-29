package com.queuectl.model;

import java.util.Objects;

/**
 * Domain entity representing a single job record in the queue system.
 */
public class Job {

    private Long id;
    private String command;
    private JobStatus status;
    private int attempts;
    private int maxAttempts;
    private long availableAt;
    private Long lockedAt;
    private String lockedBy;
    private Long leaseExpiresAt;
    private String lastError;
    private long createdAt;
    private long updatedAt;

    /**
     * Default constructor.
     */
    public Job() {
    }

    /**
     * Full constructor for creating a Job with all properties.
     *
     * @param id             job primary key
     * @param command        shell command to execute
     * @param status         current status of the job
     * @param attempts       number of execution attempts performed so far
     * @param maxAttempts    maximum allowed execution attempts
     * @param availableAt    epoch timestamp (ms) when job is available for execution
     * @param lockedAt       epoch timestamp (ms) when job was locked by a worker
     * @param lockedBy       identifier of the worker holding the lock
     * @param leaseExpiresAt epoch timestamp (ms) when the lock lease expires
     * @param lastError      error message or stack trace from the last failed attempt
     * @param createdAt      epoch timestamp (ms) when job was created
     * @param updatedAt      epoch timestamp (ms) when job was last updated
     */
    public Job(Long id, String command, JobStatus status, int attempts, int maxAttempts,
               long availableAt, Long lockedAt, String lockedBy, Long leaseExpiresAt,
               String lastError, long createdAt, long updatedAt) {
        this.id = id;
        this.command = command;
        this.status = status;
        this.attempts = attempts;
        this.maxAttempts = maxAttempts;
        this.availableAt = availableAt;
        this.lockedAt = lockedAt;
        this.lockedBy = lockedBy;
        this.leaseExpiresAt = leaseExpiresAt;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(long availableAt) {
        this.availableAt = availableAt;
    }

    public Long getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Long lockedAt) {
        this.lockedAt = lockedAt;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    public Long getLeaseExpiresAt() {
        return leaseExpiresAt;
    }

    public void setLeaseExpiresAt(Long leaseExpiresAt) {
        this.leaseExpiresAt = leaseExpiresAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return attempts == job.attempts &&
                maxAttempts == job.maxAttempts &&
                availableAt == job.availableAt &&
                createdAt == job.createdAt &&
                updatedAt == job.updatedAt &&
                Objects.equals(id, job.id) &&
                Objects.equals(command, job.command) &&
                status == job.status &&
                Objects.equals(lockedAt, job.lockedAt) &&
                Objects.equals(lockedBy, job.lockedBy) &&
                Objects.equals(leaseExpiresAt, job.leaseExpiresAt) &&
                Objects.equals(lastError, job.lastError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, command, status, attempts, maxAttempts, availableAt,
                lockedAt, lockedBy, leaseExpiresAt, lastError, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", command='" + command + '\'' +
                ", status=" + status +
                ", attempts=" + attempts +
                ", maxAttempts=" + maxAttempts +
                ", availableAt=" + availableAt +
                ", lockedAt=" + lockedAt +
                ", lockedBy='" + lockedBy + '\'' +
                ", leaseExpiresAt=" + leaseExpiresAt +
                ", lastError='" + lastError + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
