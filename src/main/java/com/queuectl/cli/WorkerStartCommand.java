package com.queuectl.cli;

import com.queuectl.db.Database;
import com.queuectl.model.WorkerInfo;
import com.queuectl.repository.WorkerRepository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.UUID;

/**
 * Subcommand to start worker processing and maintain process registration.
 */
@Command(
    name = "start",
    mixinStandardHelpOptions = true,
    description = "Starts worker process."
)
public class WorkerStartCommand implements Runnable {

    @Option(names = {"--concurrency"}, defaultValue = "1", description = "Worker concurrency level (default: 1)")
    private int concurrency = 1;

    private final WorkerRepository workerRepository;
    private volatile boolean running = true;

    /**
     * Default constructor for Picocli CLI instantiation.
     */
    public WorkerStartCommand() {
        this(new WorkerRepository(new Database()));
    }

    /**
     * Constructor for injecting custom WorkerRepository instance.
     *
     * @param workerRepository the repository for managing WorkerInfo persistence
     */
    public WorkerStartCommand(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Override
    public void run() {
        String workerId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        WorkerInfo worker = new WorkerInfo(workerId, now, now, "RUNNING");

        try {
            workerRepository.registerWorker(worker);
            System.out.println("Worker started.");
            System.out.println("Worker ID: " + workerId);
            System.out.println("Status: RUNNING");

            Thread shutdownHook = new Thread(() -> {
                running = false;
                try {
                    workerRepository.updateStatus(workerId, "STOPPED");
                    System.out.println("Worker stopped.");
                } catch (Exception e) {
                    System.err.println("Failed to update worker status on shutdown: " + e.getMessage());
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to start worker:");
            System.err.println(e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }

    /**
     * Signals the worker loop to stop running.
     */
    public void stopRunning() {
        this.running = false;
    }
}
