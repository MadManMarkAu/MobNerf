package net.madmanmarkau.MobNerf;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

public class MobNerfEntityListener extends EntityListener {
	public static MobNerf plugin;
	
	public MobNerfEntityListener(MobNerf instance) {
		plugin = instance;
	}

	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!plugin.isMobEnabled(event.getLocation().getWorld(), event.getCreatureType())) {
			event.setCancelled(true);
		}
	}
}
