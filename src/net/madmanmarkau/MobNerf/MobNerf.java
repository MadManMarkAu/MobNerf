package net.madmanmarkau.MobNerf;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.CreatureType;
import org.bukkit.util.config.Configuration;
import org.bukkit.event.Event;

// Permissions:
// if (iStick.Permissions.has(player, "foo.bar")) {}
//String messageFormat = Config.getString("nChat.messageformat", "[+prefix+group+suffix&f] +name: +message");
//String colorCharacter = Config.getString("nChat.colorcharacter", "~");

public class MobNerf extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
	public Configuration Config;
    public PluginDescriptionFile pdfFile;
	
    private MobNerfEntityListener entityListener = new MobNerfEntityListener(this);
    private String configDirectory;

	@Override
	public void onDisable() {
	    log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " unloaded");
	}

	@Override
	public void onEnable() {
		this.pdfFile = this.getDescription();
		this.configDirectory = "plugins" + File.separator + pdfFile.getName() + File.separator;

		readConfig();
		registerEvents();
		
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " loaded");
	}

	public void readConfig() {
		File file = new File(configDirectory);
		if ( !(file.exists()) ) {
			file.mkdir();
		}

		// Create configuration file if not exist
		file = new File(configDirectory + "/config.yml");
		if ( !file.exists() ) {
			try {
				FileWriter fstream = new FileWriter(configDirectory + "/config.yml");
				BufferedWriter out = new BufferedWriter(fstream);

				out.write("# ModNerf: A plugin to disable mobs on a global or per-world basis.\n");
				out.write("# Usage:\n");
				out.write("#   MobNerf.global.disable: List globally disabled mobs here. These mobs will be disabled everywhere.\n");
				out.write("#   MobNerf.worlds.<worldname>.disable: List per-world disabled mobs here. These mobs will be disabled in the specified world.\n");
				out.write("MobNerf:\n");
				out.write("    global:\n");
				out.write("        disable:\n");
				out.write("         - creeper\n");
				out.write("         - ghast\n");
				out.write("         - slime\n");
				out.write("    worlds:\n");
				for (World world : getServer().getWorlds()) {
					out.write("        " + world.getName() + ":\n");
					out.write("            disable:\n");
					out.write("             - creeper\n");
					out.write("             - ghast\n");
					out.write("             - slime\n");
				}

				out.close();
			} catch (Exception e) {
				log.warning(pdfFile.getName() + " could not write the default config file.");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}

    	// Reading from YML file
		Config = new Configuration(new File(configDirectory + "/config.yml"));
		Config.load();
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Event.Priority.Normal, this);
	}

	public boolean isMobEnabled(World world, CreatureType creatureType) {
		List<String> disabledMobs;
		
		disabledMobs = Config.getStringList("MobNerf.global.disable", new ArrayList<String>());
		if (disabledMobs.contains(creatureType.getName().toLowerCase())) {
			return false;
		}

		disabledMobs = Config.getStringList("MobNerf." + world.getName() + ".disable", new ArrayList<String>());
		if (disabledMobs.contains(creatureType.getName().toLowerCase())) {
			return false;
		}

		return true;
	}
}
