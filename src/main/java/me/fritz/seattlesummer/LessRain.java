package me.fritz.seattlesummer;

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
    private final Map<World, BukkitTask> tasks = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(getName() + " v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getName() + " disabled");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (getConfig().getStringList("excludedWorlds").contains(world.getName())) {
            return;
        }
        if (event.toWeatherState()) {
            tasks.put(world, new BukkitRunnable() {
                @Override
                public void run() {
                    getLogger().info("Force stopping rain in world: " + world.getName());
                    world.setStorm(false);
                }
            }.runTaskLater(this, getConfig().getInt("maxDuration", 300) * 20));
        } else {
            BukkitTask task = tasks.remove(world);
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
        }
    }
}
