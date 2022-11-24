package net.kunmc.lab.kamesutaaaaaaa.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.UUID;

public class FakeAppearancePacketAdapter extends PacketAdapter {
    private final Config config;
    private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    public FakeAppearancePacketAdapter(Plugin plugin, Config config) {
        super(plugin, PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        this.config = config;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (config.enabled.isFalse()) {
            return;
        }

        try {
            Class<PacketPlayOutSpawnEntityLiving> spawnLivingClass = PacketPlayOutSpawnEntityLiving.class;

            Field uuidField = spawnLivingClass.getDeclaredField("b");
            uuidField.setAccessible(true);
            UUID uuid = (UUID) uuidField.get(event.getPacket()
                                                  .getHandle());
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null && !config.entityTypeToEnabledMap.getOrDefault(entity.getType(), false)) {
                return;
            }

            Field idField = spawnLivingClass.getDeclaredField("a");
            idField.setAccessible(true);
            int id = (int) idField.get(event.getPacket()
                                            .getHandle());

            Field xField = spawnLivingClass.getDeclaredField("d");
            xField.setAccessible(true);
            double x = (double) xField.get(event.getPacket()
                                                .getHandle());
            Field yField = spawnLivingClass.getDeclaredField("e");
            yField.setAccessible(true);
            double y = (double) yField.get(event.getPacket()
                                                .getHandle());
            Field zField = spawnLivingClass.getDeclaredField("f");
            zField.setAccessible(true);
            double z = (double) zField.get(event.getPacket()
                                                .getHandle());

            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
            Class<PacketPlayOutNamedEntitySpawn> clazz = PacketPlayOutNamedEntitySpawn.class;
            // EntityId(int)
            Field a = clazz.getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, id);

            // UUID
            Field b = clazz.getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, config.kamesutaUuid.value());

            // x(double)
            Field c = clazz.getDeclaredField("c");
            c.setAccessible(true);
            c.set(packet, x);

            // y(double)
            Field d = clazz.getDeclaredField("d");
            d.setAccessible(true);
            d.set(packet, y);

            // z(double)
            Field e = clazz.getDeclaredField("e");
            e.setAccessible(true);
            e.set(packet, z);

            event.setCancelled(true);
            PacketContainer container = PacketContainer.fromPacket(packet);
            manager.sendServerPacket(event.getPlayer(), container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
