package com.queuectl.cli;

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

    @Parameters(index = "0", description = "The shell command to run")
    private String command;

    @Option(names = {"--max-attempts"}, defaultValue = "5", description = "Maximum retry attempts (default: 5)")
    private int maxAttempts = 5;

    @Override
    public void run() {
        System.out.println("Would enqueue job: " + command + " (max-attempts=" + maxAttempts + ")");
    }
}
