package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.Job;
import com.queuectl.model.JobStatus;
import com.queuectl.repository.JobRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to enqueue a new job into the queue system.
 */
@Command(
    name = "enqueue",
    mixinStandardHelpOptions = true,
    description = "Enqueues a job for execution."
)
public class EnqueueCommand implements Runnable {

    @Parameters(index = "0", arity = "0..1", description = "The shell command to run")
    private String command;

    @Option(names = {"--max-attempts"}, defaultValue = "5", description = "Maximum retry attempts (default: 5)")
    private int maxAttempts = 5;

    private final JobRepository jobRepository;

    /**
     * Default constructor for Picocli CLI instantiation.
     */
    public EnqueueCommand() {
        this(new JobRepository(new Database()));
    }

    /**
     * Constructor for injecting custom JobRepository instance.
     *
     * @param jobRepository the repository for managing Job persistence
     */
    public EnqueueCommand(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public void run() {
        if (!validateInput()) {
            return;
        }

        try {
            long now = System.currentTimeMillis();
            Job job = new Job(
                null,
                command.trim(),
                JobStatus.PENDING,
                0,
                maxAttempts,
                now,
                null,
                null,
                null,
                null,
                now,
                now
            );

            Job enqueuedJob = jobRepository.insert(job);

            System.out.println("Job enqueued successfully.");
            System.out.println("Job ID: " + enqueuedJob.getId());
            System.out.println("Command: " + enqueuedJob.getCommand());
            System.out.println("Status: PENDING");
            System.out.println("Max Attempts: " + enqueuedJob.getMaxAttempts());
        } catch (Exception e) {
            System.err.println("Failed to enqueue job:");
            System.err.println(e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }

    private boolean validateInput() {
        if (command == null || command.isBlank()) {
            System.err.println("Error: Command cannot be empty or blank.");
            return false;
        }
        if (maxAttempts <= 0) {
            System.err.println("Error: Max attempts must be greater than 0.");
            return false;
        }
        return true;
    }
}
