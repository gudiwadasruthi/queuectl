package com.queuectl.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Root command for the queuectl application.
 */
@Command(
    name = "queuectl",
    mixinStandardHelpOptions = true,
    version = "queuectl 1.0",
    description = "CLI-based persistent job queue system",
    subcommands = {
        EnqueueCommand.class,
        WorkerCommand.class,
        StatusCommand.class,
        ListCommand.class,
        ConfigCommand.class
    }
)
public class QueueCtlApplication implements Runnable {

    @Override
    public void run() {
        System.out.println("queuectl - job queue CLI");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new QueueCtlApplication()).execute(args);
        System.exit(exitCode);
    }
}
