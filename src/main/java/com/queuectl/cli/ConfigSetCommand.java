package com.queuectl.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Subcommand to set a configuration key-value pair.
 */
@Command(
    name = "set",
    mixinStandardHelpOptions = true,
    description = "Sets configuration setting value."
)
public class ConfigSetCommand implements Runnable {

    @Parameters(index = "0", description = "The configuration key")
    private String key;

    @Parameters(index = "1", description = "The configuration value")
    private String value;

    @Override
    public void run() {
        System.out.println("Would set config key=" + key + " value=" + value);
    }
}
