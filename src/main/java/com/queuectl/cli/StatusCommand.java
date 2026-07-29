package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.JobStatus;
import com.queuectl.repository.JobRepository;
import com.queuectl.repository.WorkerRepository;
import picocli.CommandLine.Command;

import java.util.Map;

/**
 * Command to display current job queue and worker status summary.
 */
@Command(
    name = "status",
    mixinStandardHelpOptions = true,
    description = "Displays status summary."
)
public class StatusCommand implements Runnable {

    private final JobRepository jobRepository;
    private final WorkerRepository workerRepository;

    /**
     * Default constructor for Picocli CLI instantiation.
     */
    public StatusCommand() {
        Database db = new Database();
        this.jobRepository = new JobRepository(db);
        this.workerRepository = new WorkerRepository(db);
    }

    /**
     * Constructor for injecting custom repositories.
     *
     * @param jobRepository    the JobRepository instance
     * @param workerRepository the WorkerRepository instance
     */
    public StatusCommand(JobRepository jobRepository, WorkerRepository workerRepository) {
        this.jobRepository = jobRepository;
        this.workerRepository = workerRepository;
    }

    @Override
    public void run() {
        try {
            Map<JobStatus, Long> jobCounts = jobRepository.countByStatus();
            long totalWorkers = workerRepository.countTotalWorkers();
            Map<String, Long> workerCounts = workerRepository.countWorkersByStatus();

            long runningWorkers = workerCounts.getOrDefault("RUNNING", 0L);
            long stoppedWorkers = workerCounts.getOrDefault("STOPPED", 0L);

            System.out.println("Queue Summary");
            System.out.println("----------------------------------------");
            System.out.printf("Pending:    %d%n", jobCounts.getOrDefault(JobStatus.PENDING, 0L));
            System.out.printf("Running:    %d%n", jobCounts.getOrDefault(JobStatus.RUNNING, 0L));
            System.out.printf("Succeeded:  %d%n", jobCounts.getOrDefault(JobStatus.SUCCEEDED, 0L));
            System.out.printf("Failed:     %d%n", jobCounts.getOrDefault(JobStatus.FAILED, 0L));
            System.out.printf("Dead:       %d%n", jobCounts.getOrDefault(JobStatus.DEAD, 0L));
            System.out.println();

            System.out.println("Worker Summary");
            System.out.println("----------------------------------------");
            System.out.printf("Total Workers:      %d%n", totalWorkers);
            System.out.printf("Running Workers:    %d%n", runningWorkers);
            System.out.printf("Stopped Workers:    %d%n", stoppedWorkers);
        } catch (Exception e) {
            System.err.println("Failed to show status summary:");
            System.err.println(e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }
}
