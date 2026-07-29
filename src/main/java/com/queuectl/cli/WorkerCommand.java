package com.queuectl.cli;

import picocli.CommandLine.Command;

/**
 * Parent command for managing worker process operations.
 */
@Command(
    name = "worker",
    mixinStandardHelpOptions = true,
    subcommands = {
        WorkerStartCommand.class,
        WorkerStopCommand.class
    },
    description = "Manage worker process operations."
)
public class WorkerCommand {
}
