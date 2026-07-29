package com.queuectl.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Subcommand to retrieve a configuration value.
 */
@Command(
    name = "get",
    mixinStandardHelpOptions = true,
    description = "Gets configuration setting value."
)
public class ConfigGetCommand implements Runnable {

    @Parameters(index = "0", description = "The configuration key")
    private String key;

    @Override
    public void run() {
        System.out.println("Would get config key=" + key);
    }
}
