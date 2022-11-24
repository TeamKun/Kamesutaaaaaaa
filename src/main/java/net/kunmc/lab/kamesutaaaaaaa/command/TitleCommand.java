package net.kunmc.lab.kamesutaaaaaaa.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.BooleanArgument;
import net.kunmc.lab.kamesutaaaaaaa.Config;
import net.kunmc.lab.kamesutaaaaaaa.KamesutaaaaaaaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class TitleCommand extends Command {
    public TitleCommand(Config config) {
        super("title");
        Deque<EditSession> history = new ArrayDeque<>();
        addChildren(new SetCommand(history, config), new UndoCommand(history));
    }

    private static class SetCommand extends Command {
        public SetCommand(Deque<EditSession> history, Config config) {
            super("set");

            execute(ctx -> {
                withArt(ctx, history, config);
            });

            argument(new BooleanArgument("onlyTitle"), (onlyTitle, ctx) -> {
                if (onlyTitle) {
                    sendTitle(config);
                } else {
                    withArt(ctx, history, config);
                }
            });
        }

        private void withArt(CommandContext ctx, Deque<EditSession> history, Config config) {
            if (!(ctx.getSender() instanceof Player)) {
                ctx.sendFailure("please execute from player");
                return;
            }
            Player p = ((Player) ctx.getSender());
            Location loc = p.getLocation();

            try (EditSession editSession = WorldEdit.getInstance()
                                                    .newEditSession(new BukkitWorld(p.getWorld()))) {
                InputStream artSchem = KamesutaaaaaaaPlugin.getInstance()
                                                           .getResource("art.schematic");
                ClipboardReader reader = BuiltInClipboardFormat.MCEDIT_SCHEMATIC.getReader(artSchem);

                ClipboardHolder holder = new ClipboardHolder(reader.read());
                Vector v = config.artOffset.value();
                holder.getClipboard()
                      .setOrigin(BlockVector3.at(v.getX(), v.getY(), v.getZ()));
                holder.setTransform(new AffineTransform().rotateY(-((loc.getYaw() + 270) % 360 + 180))
                                                         .scale(config.artScale.value()));

                Operations.complete(holder.createPaste(editSession)
                                          .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
                                          .build());
                history.addFirst(editSession);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendTitle(config);
                    }
                }.runTaskLater(KamesutaaaaaaaPlugin.getInstance(), 60);
            } catch (IOException | WorldEditException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendTitle(Config config) {
            Bukkit.getOnlinePlayers()
                  .forEach(x -> {
                      x.sendTitle(config.title.value(), config.subtitle.value(), 20, 100, 20);
                  });
        }
    }

    private static class UndoCommand extends Command {
        public UndoCommand(Deque<EditSession> history) {
            super("undo");

            execute(ctx -> {
                if (history.isEmpty()) {
                    ctx.sendFailure("history is empty");
                    return;
                }

                if (!(ctx.getSender() instanceof Player)) {
                    ctx.sendFailure("please execute from player");
                    return;
                }
                Player p = ((Player) ctx.getSender());

                try (EditSession newEditSession = WorldEdit.getInstance()
                                                           .newEditSession(new BukkitWorld(p.getWorld()))) {
                    EditSession editSession = history.pop();
                    editSession.undo(newEditSession);
                }
            });
        }
    }
}
