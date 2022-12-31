package net.kunmc.lab.kamesutaaaaaaa.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.papermc.paper.adventure.AdventureComponent;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutKickDisconnect;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PreventInvalidEntityAttackPacketAdapter extends PacketAdapter {
    private final Config config;

    public PreventInvalidEntityAttackPacketAdapter(Plugin plugin, Config config) {
        super(plugin, PacketType.Play.Server.KICK_DISCONNECT, PacketType.Play.Client.USE_ENTITY);
        this.config = config;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (config.enabled.isFalse()) {
            return;
        }

        PacketPlayInUseEntity packet = ((PacketPlayInUseEntity) event.getPacket()
                                                                     .getHandle());
        if (packet.b() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
            return;
        }

        Bukkit.getWorlds()
              .stream()
              .map(World::getEntities)
              .reduce(new ArrayList<>(), (x, y) -> {
                  x.addAll(y);
                  return x;
              })
              .stream()
              .filter(x -> x.getEntityId() == packet.getEntityId())
              .map(Entity::getType)
              .filter(x -> x == EntityType.DROPPED_ITEM || x == EntityType.EXPERIENCE_ORB || x == EntityType.ARROW || x == EntityType.SPECTRAL_ARROW)
              .filter(x -> config.entityTypeToEnabledMap.getOrDefault(x, false))
              .findFirst()
              .ifPresent(x -> {
                  event.setCancelled(true);
              });
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketPlayOutKickDisconnect packet = ((PacketPlayOutKickDisconnect) event.getPacket()
                                                                                 .getHandle());
        try {
            Field f = PacketPlayOutKickDisconnect.class.getDeclaredField("a");
            f.setAccessible(true);
            IChatBaseComponent message = ((IChatBaseComponent) f.get(packet));
            if (message instanceof AdventureComponent) {
                String text = message.getString();
                if (text.equals("multiplayer.disconnect.invalid_entity_attacked")) {
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
