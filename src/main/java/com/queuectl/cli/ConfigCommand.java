package com.queuectl.cli;

import picocli.CommandLine.Command;

/**
 * Parent command for managing configuration settings.
 */
@Command(
    name = "config",
    mixinStandardHelpOptions = true,
    subcommands = {
        ConfigGetCommand.class,
        ConfigSetCommand.class
    },
    description = "Manage queue system configuration settings."
)
public class ConfigCommand {
}
