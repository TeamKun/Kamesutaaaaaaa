package net.kunmc.lab.kamesutaaaaaaa.task;

import net.kunmc.lab.kamesutaaaaaaa.Config;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Optional;

public class AppleTask extends BukkitRunnable {
    private final Config config;

    public AppleTask(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        if (config.enabled.isFalse()) {
            return;
        }
        if (config.eatApple.isFalse()) {
            return;
        }

        Bukkit.selectEntities(Bukkit.getConsoleSender(), "@e[type=!player]")
              .stream()
              .filter(x -> config.entityTypeToEnabledMap.getOrDefault(x.getType(), false))
              .filter(x -> x instanceof LivingEntity)
              .map(LivingEntity.class::cast)
              .filter(Entity::isOnGround)
              .filter(LivingEntity::hasAI)
              .forEach(kamesuta -> {
                  Optional<Item> optionalApple = kamesuta.getNearbyEntities(1.0, 1.0, 1.0)
                                                         .stream()
                                                         .filter(y -> y instanceof Item)
                                                         .map(Item.class::cast)
                                                         .filter(y -> y.getItemStack()
                                                                       .getType() == Material.APPLE)
                                                         .filter(Entity::isValid)
                                                         .findFirst();

                  optionalApple.ifPresent(apple -> {
                      Location loc = kamesuta.getLocation();

                      kamesuta.setAI(false);
                      kamesuta.setInvulnerable(true);
                      if (kamesuta.getEquipment() != null) {
                          kamesuta.getEquipment()
                                  .setItemInMainHand(null);
                      }

                      apple.remove();
                      ArmorStand armorStand = (ArmorStand) kamesuta.getWorld()
                                                                   .spawnEntity(loc,
                                                                                EntityType.ARMOR_STAND,
                                                                                CreatureSpawnEvent.SpawnReason.CUSTOM,
                                                                                entity -> {
                                                                                    ArmorStand as = ((ArmorStand) entity);
                                                                                    as.setInvisible(true);
                                                                                    as.setGravity(false);
                                                                                    as.setItem(EquipmentSlot.HAND,
                                                                                               new ItemStack(Material.APPLE));
                                                                                    as.setInvulnerable(true);
                                                                                });

                      new BukkitRunnable() {
                          private int count = 0;

                          @Override
                          public void run() {
                              loc.getWorld()
                                 .playSound(loc, Sound.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                              loc.getWorld()
                                 .spawnParticle(Particle.ITEM_CRACK,
                                                armorStand.getEyeLocation()
                                                          .add(armorStand.getLocation()
                                                                         .getDirection()
                                                                         .multiply(0.2)
                                                                         .subtract(new Vector(0, 0.25, 0))),
                                                5,
                                                0,
                                                0,
                                                0,
                                                0.1,
                                                new ItemStack(Material.APPLE));

                              count++;
                              if (count > 7) {
                                  kamesuta.remove();
                                  armorStand.remove();
                                  cancel();
                              }
                          }
                      }.runTaskTimer(config.plugin(), 20, 5);
                  });
              });
    }
}
