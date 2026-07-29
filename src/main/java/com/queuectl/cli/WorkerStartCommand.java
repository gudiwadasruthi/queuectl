package com.queuectl.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Subcommand to start worker processing.
 */
@Command(
    name = "start",
    mixinStandardHelpOptions = true,
    description = "Starts worker process."
)
public class WorkerStartCommand implements Runnable {

    @Option(names = {"--concurrency"}, defaultValue = "1", description = "Worker concurrency level (default: 1)")
    private int concurrency = 1;

    @Override
    public void run() {
        System.out.println("Would start worker with concurrency=" + concurrency);
    }
}
