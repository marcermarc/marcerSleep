package de.marcermarc.sleep.listener;

import de.marcermarc.sleep.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;

public class Sleep implements Listener {

    private final PluginController controller;

    private final HashMap<World, Integer> sleepingPlayerPerWorld;
    private final HashSet<Player> sleepingPlayer;

    private final HashMap<World, BukkitTask> tasks;

    private final HashSet<World> timerRun;

    public Sleep(PluginController controller) {
        this.controller = controller;
        this.sleepingPlayer = new HashSet<>();
        this.sleepingPlayerPerWorld = new HashMap<>();
        this.tasks = new HashMap<>();
        this.timerRun = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldUnload(WorldUnloadEvent event) {
        sleepingPlayerPerWorld.remove(event.getWorld());

        if (tasks.containsKey(event.getWorld())) {
            tasks.get(event.getWorld()).cancel();
            tasks.remove(event.getWorld());
        }

        timerRun.remove(event.getWorld());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (sleepingPlayer.remove(event.getPlayer())) {
            int anzahl = this.sleepingPlayerPerWorld.getOrDefault(event.getPlayer().getWorld(), 0);
            if (anzahl > 0) {
                this.sleepingPlayerPerWorld.put(event.getPlayer().getWorld(), anzahl - 1);
            }
        }
        testSleep(event.getPlayer().getWorld());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEnterBed(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            this.sleepingPlayerPerWorld.put(event.getPlayer().getWorld(), this.sleepingPlayerPerWorld.getOrDefault(event.getPlayer().getWorld(), 0) + 1);
            this.sleepingPlayer.add(event.getPlayer());
            sendSleepMessage(controller.getConfig().getMessageSomeoneGoSleep(), event.getPlayer(), event.getPlayer().getWorld());
            testSleep(event.getPlayer().getWorld());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeaveBed(PlayerBedLeaveEvent event) {
        int anzahl = this.sleepingPlayerPerWorld.getOrDefault(event.getPlayer().getWorld(), 0);
        this.sleepingPlayer.remove(event.getPlayer());

        if (anzahl > 0) {
            this.sleepingPlayerPerWorld.put(event.getPlayer().getWorld(), anzahl - 1);
            sendSleepMessage(controller.getConfig().getMessageSomeoneGetUp(), event.getPlayer(), event.getPlayer().getWorld());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        testSleep(event.getFrom());
    }

    private void testSleep(World world) {
        if (controller.getConfig().getPercentOfPlayerMustSleep() * world.getPlayers().size() <= sleepingPlayer.size()) {
            if (!timerRun.contains(world)) {
                Runnable r = () -> sleep(world);
                this.tasks.put(world, Bukkit.getScheduler().runTaskLater(controller.getMain(), r, 100L));
                this.timerRun.add(world);
            }
        } else {
            cancelTimer(world);
        }
    }

    private void cancelTimer(World world) {
        if (this.timerRun.contains(world)) {
            this.tasks.remove(world).cancel();
            this.timerRun.remove(world);
        }
    }

    private void sleep(World world) {
        synchronized (Bukkit.class) {
            world.setTime(0);
            sendSleepMessage(controller.getConfig().getMessageAfterSleep(), null, world);

            this.sleepingPlayerPerWorld.put(world, 0);

            if (world.hasStorm()) {
                world.setStorm(false);
            }

            if (world.isThundering()) {
                world.setThundering(false);
            }

            this.timerRun.remove(world);
        }
    }

    private void sendSleepMessage(String message, Player player, World world) {
        if (world.getPlayers().size() != 1) {
            if (player != null) {
                message = message.replaceAll("@player", player.getDisplayName());
            }
            message = message.replaceAll("@sleepingPlayer", "" + sleepingPlayer.size());
            message = message.replaceAll("@mustSleepPlayer", "" + (int) Math.ceil((controller.getConfig().getPercentOfPlayerMustSleep() * world.getPlayers().size())));
            for (Player p : world.getPlayers()) {
                p.sendMessage(message);
            }
        }
    }
}
