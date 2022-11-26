package net.kunmc.lab.kamesutaaaaaaa;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.kunmc.lab.kamesutaaaaaaa.command.MainCommand;
import net.kunmc.lab.kamesutaaaaaaa.packet.FakeAppearancePacketAdapter;
import net.kunmc.lab.kamesutaaaaaaa.packet.InjectPlayerInfoPacketAdapter;
import net.kunmc.lab.kamesutaaaaaaa.task.AppleTask;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public final class KamesutaaaaaaaPlugin extends JavaPlugin {
    private static KamesutaaaaaaaPlugin INSTANCE;
    private Config config;

    public static KamesutaaaaaaaPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.config = new Config(this);
        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();
        CommandLib.register(this, new MainCommand(configCommand, config));

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new FakeAppearancePacketAdapter(this, config));
        manager.addPacketListener(new InjectPlayerInfoPacketAdapter(this, config));

        new AppleTask(config).runTaskTimer(this, 0, 20);
    }

    public void updateEntitiesAppearance() {
        PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle()
                                                                  .getServer()
                                                                  .getPlayerList();

        List<Entity> targets = Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e")
                                     .stream()
                                     .filter(x -> config.entityTypeToEnabledMap.getOrDefault(x.getType(), false))
                                     .collect(Collectors.toList());

        playerList.sendAll(new PacketPlayOutEntityDestroy(targets.stream()
                                                                 .mapToInt(Entity::getEntityId)
                                                                 .toArray()));

        new BukkitRunnable() {
            @Override
            public void run() {
                targets.stream()
                       .map(x -> ((CraftEntity) x).getHandle())
                       .forEach(x -> {
                           playerList.sendAll(new PacketPlayOutSpawnEntity(x));
                       });
            }
        }.runTaskLater(config.plugin(), 1);
    }
}
