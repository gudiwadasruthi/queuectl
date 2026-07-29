package com.queuectl.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Subcommand to stop worker processing.
 */
@Command(
    name = "stop",
    mixinStandardHelpOptions = true,
    description = "Stops active worker process(es)."
)
public class WorkerStopCommand implements Runnable {

    @Option(names = {"--worker-id"}, description = "Worker ID to stop")
    private String workerId = null;

    @Option(names = {"--all"}, defaultValue = "false", description = "Stop all workers")
    private boolean all = false;

    @Override
    public void run() {
        System.out.println("Would stop worker(s): id=" + workerId + " all=" + all);
    }
}
