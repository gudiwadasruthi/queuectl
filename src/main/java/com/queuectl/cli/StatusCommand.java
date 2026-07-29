package com.queuectl.cli;

import picocli.CommandLine.Command;

/**
 * Command to display current job queue status summary.
 */
@Command(
    name = "status",
    mixinStandardHelpOptions = true,
    description = "Displays status summary."
)
public class StatusCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Would show status summary");
    }
}
