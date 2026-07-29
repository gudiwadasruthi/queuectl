package com.queuectl.model;

/**
 * Represents the execution state of a job in the queue system.
 */
public enum JobStatus {
    PENDING,
    RUNNING,
    SUCCEEDED,
    FAILED,
    DEAD
}
