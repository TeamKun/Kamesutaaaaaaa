package net.kunmc.lab.kamesutaaaaaaa.task;

import net.kunmc.lab.kamesutaaaaaaa.Config;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetTargetTask extends BukkitRunnable {
    private final Config config;
    private static final String metadataKey = "KamesutaPluginTargetTask";

    public SetTargetTask(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        if (config.enableSetTarget.isFalse()) {
            return;
        }

        Map<World, List<Player>> worldToTargetPlayerMap = Bukkit.getOnlinePlayers()
                                                                .stream()
                                                                .filter(x -> config.targetCandidates.contains(x.getUniqueId()))
                                                                .collect(Collectors.groupingBy(Entity::getWorld));
        worldToTargetPlayerMap.keySet()
                              .stream()
                              .filter(x -> x instanceof Mob)
                              .map(Mob.class::cast)
                              .forEach(x -> {
                                  List<Player> candidates = worldToTargetPlayerMap.get(x.getWorld());
                                  if (candidates.isEmpty()) {
                                      return;
                                  }
                                  Collections.shuffle(candidates);
                                  Player candidate = candidates.get(0);

                                  if (!x.hasMetadata(metadataKey)) {
                                      x.setMetadata(metadataKey,
                                                    new FixedMetadataValue(config.plugin(), Bukkit.getCurrentTick()));
                                      x.setTarget(candidate);
                                      return;
                                  }

                                  int beforeTick = x.getMetadata(metadataKey)
                                                    .get(0)
                                                    .asInt();
                                  if (Bukkit.getCurrentTick() - beforeTick < config.changeTargetIntervalTick.value()) {
                                      return;
                                  }

                                  x.setTarget(candidate);
                                  x.setMetadata(metadataKey,
                                                new FixedMetadataValue(config.plugin(), Bukkit.getCurrentTick()));
                              });
    }
}
