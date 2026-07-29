package com.queuectl.model;

import java.util.Objects;

/**
 * Domain entity representing a worker process registered in the queue system.
 */
public class WorkerInfo {

    private String id;
    private long startedAt;
    private long lastHeartbeatAt;
    private String status;

    /**
     * Default constructor.
     */
    public WorkerInfo() {
    }

    /**
     * Full constructor for creating a WorkerInfo with all properties.
     *
     * @param id              unique worker identifier
     * @param startedAt       epoch timestamp (ms) when worker was started
     * @param lastHeartbeatAt epoch timestamp (ms) of last worker heartbeat
     * @param status          current status of the worker
     */
    public WorkerInfo(String id, long startedAt, long lastHeartbeatAt, String status) {
        this.id = id;
        this.startedAt = startedAt;
        this.lastHeartbeatAt = lastHeartbeatAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(long lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerInfo that = (WorkerInfo) o;
        return startedAt == that.startedAt &&
                lastHeartbeatAt == that.lastHeartbeatAt &&
                Objects.equals(id, that.id) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startedAt, lastHeartbeatAt, status);
    }

    @Override
    public String toString() {
        return "WorkerInfo{" +
                "id='" + id + '\'' +
                ", startedAt=" + startedAt +
                ", lastHeartbeatAt=" + lastHeartbeatAt +
                ", status='" + status + '\'' +
                '}';
    }
}
