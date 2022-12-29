package net.kunmc.lab.kamesutaaaaaaa.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.kamesutaaaaaaa.Config;

public class MainCommand extends Command {
    public MainCommand(ConfigCommand configCommand, Config config) {
        super("kamesuta");
        addChildren(configCommand,
                    new TitleCommand(config),
                    new StartCommand(config),
                    new StopCommand(config),
                    new ExplainCommand(config));
    }
}
