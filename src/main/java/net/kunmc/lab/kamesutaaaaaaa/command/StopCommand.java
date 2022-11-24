package net.kunmc.lab.kamesutaaaaaaa.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.kunmc.lab.kamesutaaaaaaa.KamesutaaaaaaaPlugin;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

public class StopCommand extends Command {
    public StopCommand(Config config) {
        super("stop");

        execute(ctx -> {
            if (config.enabled.isFalse()) {
                ctx.sendFailure("not started");
                return;
            }
            config.modify(config.enabled, false);

            ((CraftServer) Bukkit.getServer()).getHandle()
                                              .getServer()
                                              .getPlayerList()
                                              .sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER));

            KamesutaaaaaaaPlugin.getInstance()
                                .updateEntitiesAppearance();

            ctx.sendSuccess("stop succeed");
        });
    }
}
