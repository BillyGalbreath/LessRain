package net.pl3x.bukkit.lessrain;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aLessRain config reloaded"));
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (getConfig().getStringList("excludedWorlds").contains(world.getName())) {
            return;
        }
        if (event.toWeatherState()) {
            Long lastStorm = this.lastStorm.get(world);
            if (lastStorm != null) {
                if (lastStorm + getConfig().getInt("minDurationBetweenStorms", 1800) * 1000 < System.currentTimeMillis()) {
                    event.setCancelled(true);
                    return;
                }
                this.lastStorm.remove(world);
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
