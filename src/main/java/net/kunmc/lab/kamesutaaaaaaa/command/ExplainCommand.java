package net.kunmc.lab.kamesutaaaaaaa.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import org.bukkit.entity.Player;

public class ExplainCommand extends Command {
    public ExplainCommand(Config config) {
        super("explain");

        execute(ctx -> {
            if (!(ctx.getSender() instanceof Player)) {
                ctx.sendFailure("please execute from player.");
                return;
            }
            Player p = ((Player) ctx.getSender());
            config.explainTexts.forEach(p::chat);
        });
    }
}
