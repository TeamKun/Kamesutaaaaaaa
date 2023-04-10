package net.kunmc.lab.kamesutaaaaaaa.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.authlib.GameProfile;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InjectPlayerInfoPacketAdapter extends PacketAdapter {
    private final Config config;

    public InjectPlayerInfoPacketAdapter(Plugin plugin, Config config) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);

        this.config = config;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (config.enabled.isFalse()) {
            return;
        }

        PacketPlayOutPlayerInfo packet = ((PacketPlayOutPlayerInfo) event.getPacket()
                                                                         .getHandle());
        try {
            Field actionField = PacketPlayOutPlayerInfo.class.getDeclaredField("a");
            actionField.setAccessible(true);
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = ((PacketPlayOutPlayerInfo.EnumPlayerInfoAction) actionField.get(
                    packet));
            if (action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER) {
                return;
            }

            Field playerInfosField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            playerInfosField.setAccessible(true);
            List playerInfos = ((List) playerInfosField.get(packet));
            List copyPlayerInfos = new ArrayList<>(playerInfos);

            Constructor<?> playerInfoConstructor = PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0].getDeclaredConstructors()[0];
            copyPlayerInfos.add(playerInfoConstructor.newInstance(packet,
                                                              new GameProfile(config.kamesutaUuid.value(),
                                                                              config.name.value()),
                                                              10,
                                                              EnumGamemode.SURVIVAL,
                                                              null));
            playerInfosField.set(packet, copyPlayerInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
