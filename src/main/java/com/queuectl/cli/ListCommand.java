package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;
import com.queuectl.repository.JobRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command to list jobs in the queue system with optional status filtering.
 */
@Command(
    name = "list",
    mixinStandardHelpOptions = true,
    description = "Lists jobs in the queue system."
)
public class ListCommand implements Runnable {

    @Option(names = {"--status"}, description = "Filter jobs by status (PENDING, RUNNING, SUCCEEDED, FAILED, DEAD)")
    private String statusFilter;

    private final JobRepository jobRepository;

    /**
     * Default constructor for Picocli CLI instantiation.
     */
    public ListCommand() {
        this(new JobRepository(new Database()));
    }

    /**
     * Constructor for injecting custom JobRepository instance.
     *
     * @param jobRepository the repository for managing Job persistence
     */
    public ListCommand(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void run() {
        JobStatus filterEnum = null;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                filterEnum = JobStatus.valueOf(statusFilter.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                String allowed = Arrays.stream(JobStatus.values())
                        .map(Enum::name)
                        .collect(Collectors.joining(", "));
                System.err.println("Unknown job status: " + statusFilter);
                System.err.println("Allowed statuses: " + allowed);
                return;
            }
        }

        try {
            List<Job> jobs = (filterEnum == null)
                    ? jobRepository.findAll()
                    : jobRepository.findByStatus(filterEnum);

            if (jobs.isEmpty()) {
                System.out.println("No jobs found.");
                return;
            }

            System.out.printf("%-5s %-10s %-10s %-5s %s%n", "ID", "STATUS", "ATTEMPTS", "MAX", "COMMAND");
            System.out.println("----------------------------------------------------------");
            for (Job job : jobs) {
                System.out.printf("%-5d %-10s %-10d %-5d %s%n",
                        job.getId(),
                        job.getStatus(),
                        job.getAttempts(),
                        job.getMaxAttempts(),
                        job.getCommand());
            }
        } catch (Exception e) {
            System.err.println("Failed to list jobs:");
            System.err.println(e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }
}
