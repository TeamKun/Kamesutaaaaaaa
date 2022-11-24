package net.kunmc.lab.kamesutaaaaaaa.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.kunmc.lab.kamesutaaaaaaa.KamesutaaaaaaaPlugin;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCommand extends Command {
    public StartCommand(Config config) {
        super("start");

        execute(ctx -> {
            if (config.enabled.isTrue()) {
                ctx.sendFailure("already started");
                return;
            }
            config.modify(config.enabled, true);

            Bukkit.getOnlinePlayers()
                  .forEach(x -> {
                      x.sendTitle("かめぱわ～～～～！！！！", "", 20, 80, 20);
                      x.playSound(Sound.sound(NamespacedKey.fromString("kamepower"),
                                              Sound.Source.AMBIENT,
                                              config.volume.value(),
                                              1.0F));
                  });

            ((CraftServer) Bukkit.getServer()).getHandle()
                                              .getServer()
                                              .getPlayerList()
                                              .sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER));

            new BukkitRunnable() {
                @Override
                public void run() {
                    KamesutaaaaaaaPlugin.getInstance()
                                        .updateEntitiesAppearance();
                }
            }.runTaskLater(config.plugin(), 60);
        });
    }

}
