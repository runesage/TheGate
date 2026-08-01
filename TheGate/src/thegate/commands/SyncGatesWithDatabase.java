package thegate.commands;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;


public class SyncGatesWithDatabase {

	protected static boolean syncDB(Player p) {
		if(p == null && Globals.SaveFromat.equalsIgnoreCase("mysql")) {
			long timeStart = System.currentTimeMillis();
			TheGateMain.SaveLoadInterface.SaveDataFromSet(GateManager.getGatesAsSet());
			
			GateManager.setGatesOnOtherServer(TheGateMain.SaveLoadInterface.GateListOtherServers());
			long timeEnd = System.currentTimeMillis();
			long total = timeEnd - timeStart;
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "Data has been synchronized! Time: " + total + "ms");
			
			for (GateObject obj : GateManager.getGatesAsSet()) {
				Location Loc = obj.getDHD();
				if(Loc != null) {
					for(int i = 0; i < 100; i++) {
						obj.getDHD().getWorld().spawnParticle(Particle.ENCHANT, Loc.getX() + 0.5 + ((new Random().nextDouble() * 2) - 1), Loc.getY() + 1 + ((new Random().nextDouble() * 2) - 1) * 0.5, Loc.getZ() + 0.5 + ((new Random().nextDouble() * 2) - 1), 0);
					}
				}
			}
			TheGateMain.theGateMain.AutoSyncTime = LocalDateTime.now();
			
		}else if(p != null && Globals.SaveFromat.equalsIgnoreCase("mysql")){
			if(p.hasPermission(Perms.thegate_admin_syncdatabase.value())) {
				
				TheGateMain.SaveLoadInterface.SaveDataFromSet(GateManager.getGatesAsSet());
				
				GateManager.setGatesOnOtherServer(TheGateMain.SaveLoadInterface.GateListOtherServers());
			
				for (GateObject obj : GateManager.getGatesAsSet()) {
					Location Loc = obj.getDHD();
					if(Loc != null) {
						for(int i = 0; i < 100; i++) {
							obj.getDHD().getWorld().spawnParticle(Particle.ENCHANT, Loc.getX() + 0.5 + ((new Random().nextDouble() * 2) - 1), Loc.getY() + 1 + ((new Random().nextDouble() * 2) - 1) * 0.5, Loc.getZ() + 0.5 + ((new Random().nextDouble() * 2) - 1), 0);
						}
					}
				}
				TheGateMain.theGateMain.AutoSyncTime = LocalDateTime.now();
				p.sendMessage(ChatColor.DARK_GREEN + "Database has been synchronized.");
			}else {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
			}
		}
		return true;
	}
}
