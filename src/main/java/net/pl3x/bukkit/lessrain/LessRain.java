package net.pl3x.bukkit.lessrain;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class LessRain extends JavaPlugin implements Listener {
    private final Map<World, BukkitTask> stopTasks = new HashMap<>();
    private final Map<World, Long> lastStorm = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        stopTasks.forEach((world, task) -> {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        });
        stopTasks.clear();
        getLogger().info(getName() + " disabled");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (getConfig().getStringList("excludedWorlds").contains(world.getName())) {
            return;
        }
        if (event.toWeatherState()) {
            Long lastStorm = this.lastStorm.remove(world);
            if (lastStorm != null && lastStorm + getConfig().getInt("minDurationBetweenStorms", 1800) * 1000 < System.currentTimeMillis()) {
                event.setCancelled(true);
                return;
            }
            stopTasks.put(world, new BukkitRunnable() {
                @Override
                public void run() {
                    getLogger().info("Force stopping rain in world: " + world.getName());
                    world.setStorm(false);
                    stopTasks.remove(world);
                }
            }.runTaskLater(this, getConfig().getInt("maxDurationOfStorms", 300) * 20));
        } else {
            lastStorm.put(world, System.currentTimeMillis());
            BukkitTask task = stopTasks.remove(world);
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
    }
}