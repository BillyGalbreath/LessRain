package me.fritz.seattlesummer;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

/** Manages loading or creating the configuration file for SeattleSummer
 * 
 * @author Quaz3l
 *
 */
public class SeattleSummerConfigHandler {

    private Integer configRainDuration;
    private List<Object> configExcludedWorlds = new ArrayList<Object>();
    private static SeattleSummer plugin;

    /** Loads configuration, creates default config file if none exists.
     * 
     * @param instance The main SeattleSummer plugin instance
     */
    public SeattleSummerConfigHandler (SeattleSummer instance) {
        plugin = instance;

        FileConfiguration config = plugin.getConfig();

        this.configExcludedWorlds = config.getList("excludedWorlds");
        this.configRainDuration = config.getInt("maxDuration");

        config.options().copyDefaults(true);
    plugin.saveConfig();
    }

    /** Retrieve maxDuration defined in config file
     * 
     * @return maxDuration setting converted to milliseconds
     */
    public long rainDuration() {
        return configRainDuration * 1000;
    }

    /** Checks if a world is excluded by config file
     * 
     * @param worldName the name of the world being checked
     * 
     * @return false if world is in exclusion list
     */
    public Boolean isManaged(String worldName) {
        return !(configExcludedWorlds.contains(worldName));
    }
}
