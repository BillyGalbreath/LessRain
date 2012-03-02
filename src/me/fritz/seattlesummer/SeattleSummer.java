package me.fritz.seattlesummer;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/** Main SeattleSummer Class
 *  
 * @Author Fritz
 * @version 2.0.0
 */
public class SeattleSummer extends JavaPlugin{
	public final String prefix = "[qQuests] ";
	
    /**
     * Config object
     */
    public SeattleSummerConfigHandler config;
    static final Logger log = Logger.getLogger("Minecraft");

    /** Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        config = new SeattleSummerConfigHandler(this);

        getServer().getPluginManager().registerEvents(new SeattleSummerWeatherListener(this), this);

        log.info(this.prefix + "v" + this.getDescription().getVersion() + " by Quaz3l and Fritz: Enabled");
    }

    /** Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
    	log.info(this.prefix + "v" + this.getDescription().getVersion() + " by Quaz3l and Fritz: Disabled");
    }
}