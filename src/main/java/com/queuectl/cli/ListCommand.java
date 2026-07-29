package com.queuectl.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command to list jobs with optional status filtering.
 */
@Command(
    name = "list",
    mixinStandardHelpOptions = true,
    description = "Lists jobs in the queue system."
)
public class ListCommand implements Runnable {

    @Option(names = {"--status"}, description = "Filter jobs by status (PENDING, RUNNING, SUCCEEDED, FAILED, DEAD)")
    private String status = null;

    @Override
    public void run() {
        String filter = (status != null) ? status : "ALL";
        System.out.println("Would list jobs with status filter=" + filter);
    }
}
