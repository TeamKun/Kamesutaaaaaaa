package net.kunmc.lab.kamesutaaaaaaa;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.Value;
import net.kunmc.lab.configlib.value.*;
import net.kunmc.lab.configlib.value.map.Enum2BooleanMapValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class Config extends BaseConfig {
    public transient final BooleanValue enabled = new BooleanValue(false);
    public final UUIDValue kamesutaUuid = new UUIDValue(UUID.fromString("4f2a2943-2d95-4959-b53e-60cd86edd245"));
    public final StringValue name = new StringValue("Kamesuta");
    public final Enum2BooleanMapValue<EntityType> entityTypeToEnabledMap = new Enum2BooleanMapValue<>();
    public final BooleanValue eatApple = new BooleanValue(true);
    public final StringValue title = new StringValue("かぁぁめすたぁぁぁ！！");
    public final StringValue subtitle = new StringValue(ChatColor.AQUA + "提供: " + ChatColor.GREEN + "KUN Lab(Marutaso)");
    public final VectorValue artOffset = new VectorValue(new Vector(60, 30, 103));
    public final DoubleValue artScale = new DoubleValue(0.8);
    private final StringValue resourcePackUrl = new StringValue(
            "https://cdn.discordapp.com/attachments/747508075753373719/1045313627969835018/KamesutaPluginResourcePack.zip");
    private final StringValue resourcePackSha1 = new StringValue("620b7c7d199cd33efdee5aac869fa363e13366ad");
    public final FloatValue volume = new FloatValue(0.8F);

    public Config(@NotNull Plugin plugin) {
        super(plugin);

        Arrays.stream(EntityType.values())
              .filter(EntityType::isAlive)
              .filter(x -> x != EntityType.ARMOR_STAND)
              .filter(x -> x != EntityType.PLAYER)
              .forEach(x -> {
                  entityTypeToEnabledMap.put(x, true);
              });
        entityTypeToEnabledMap.put(EntityType.ARROW, true);
        entityTypeToEnabledMap.put(EntityType.SPECTRAL_ARROW, true);
        entityTypeToEnabledMap.put(EntityType.WITHER_SKULL, true);
        entityTypeToEnabledMap.put(EntityType.ENDER_CRYSTAL, true);
        entityTypeToEnabledMap.put(EntityType.ENDER_SIGNAL, true);
        entityTypeToEnabledMap.put(EntityType.ENDER_PEARL, true);

        onInitialize(this::changeResourcePackUrl);
    }

    private void changeResourcePackUrl() {
        ((CraftServer) Bukkit.getServer()).getHandle()
                                          .getServer()
                                          .setResourcePack(resourcePackUrl.value(), resourcePackSha1.value());
    }

    public <T extends Value<E, T>, E> void modify(T valueObj, E newValue) {
        valueObj.value(newValue);
        saveConfigIfPresent();
    }
}
