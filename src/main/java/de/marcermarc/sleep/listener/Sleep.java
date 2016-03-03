package de.marcermarc.sleep.listener;

import de.marcermarc.sleep.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class Sleep implements Listener {

    private PluginController controller;

    private HashSet<Player> sleepingPlayer;

    private Timer timer;

    private boolean timerRun = false;

    public Sleep(PluginController controller) {
        this.controller = controller;
        this.sleepingPlayer = new HashSet<>();
    }

//    @EventHandler(priority = EventPriority.LOW)
//    public void onPlayerJoin(PlayerJoinEvent event) {
//
//    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerLeave(PlayerQuitEvent event) {
        sleepingPlayer.remove(event.getPlayer());
        testSleep();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEnterBed(PlayerBedEnterEvent event) {
        sleepingPlayer.add(event.getPlayer());
        sendSleepMessage(controller.getConfig().getMessageSomeoneGoSleep(), event.getPlayer());
        testSleep();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeaveBed(PlayerBedLeaveEvent event) {
        sleepingPlayer.remove(event.getPlayer());
        sendSleepMessage(controller.getConfig().getMessageSomeoneGetUp(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        testSleep();
    }

    private void testSleep() {
        if (controller.getConfig().getPercentOfPlayerMustSleep() * Bukkit.getWorlds().get(0).getPlayers().size() <= sleepingPlayer.size()) {
            if (!timerRun) {
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        sleep();
                    }
                };
                timer = new Timer();
                timer.schedule(tt, 5000L);
                timerRun = true;
            }
        } else {
            if (timerRun) {
                timer.cancel();
                timerRun = false;
            }
        }
    }

    private void sleep() {
        synchronized (Bukkit.class) {
            Bukkit.getWorlds().get(0).setTime(0);
            sendSleepMessage(controller.getConfig().getMessageAfterSleep(), null);

            if (Bukkit.getWorlds().get(0).hasStorm()) {
                Bukkit.getWorlds().get(0).setStorm(false);
            }

            if (Bukkit.getWorlds().get(0).isThundering()) {
                Bukkit.getWorlds().get(0).setThundering(false);
            }
        }
    }

    private void sendSleepMessage(String message, Player player) {
        if (player != null) {
            message = message.replaceAll("@player", player.getDisplayName());
        }
        message = message.replaceAll("@sleepingPlayer", "" + sleepingPlayer.size());
        message = message.replaceAll("@mustSleepPlayer", "" + (int) Math.ceil((controller.getConfig().getPercentOfPlayerMustSleep() * Bukkit.getWorlds().get(0).getPlayers().size())));
        for (Player p : Bukkit.getWorlds().get(0).getPlayers()) {
            p.sendMessage(message);
        }
    }

}
