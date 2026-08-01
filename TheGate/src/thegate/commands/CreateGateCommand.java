package thegate.commands;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


import net.md_5.bungee.api.ChatColor;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.Globals;
import thegate.math.GateMath;

public class CreateGateCommand {

	public static boolean create(Player p,String address, String world, int x, int y, int z, float rot) {
		
		if(p != null) {
			if(address.length() > 7 || address.length() < 7 || !checkAddress(address) || GateManager.getGateWithAddress(address) != null) {
				p.sendMessage(ChatColor.RED+"Illegal Address: " + address);
				return true;
			}
			Location loc = p.getLocation().getBlock().getLocation().add(new Vector(0,-1,0));
			GateObject go = new GateObject(loc, loc.getWorld().getName(), ((rot < 0.0f) ? GateMath.getPlayerDirection(p) : ((rot/360.0f)*4)), address, p.getUniqueId().toString(), p.getName());
			go.setUpdated(true);
			GateManager.addGate(go);
			p.sendMessage(ChatColor.GREEN+"Gate created!");
		}else {
			if(address.length() > 7 || address.length() < 7 || !checkAddress(address) || GateManager.getGateWithAddress(address) != null) {
				Bukkit.getLogger().log(Level.WARNING, "Illegal Address: " + address);
				return true;
			}
			World w = Bukkit.getWorld(world);
			if(w == null) {
				Bukkit.getLogger().log(Level.WARNING, "Could not find world: " + world);
				return true;
			}
			float r = ((rot/360.0f)*4.0f)%4;
			
			Location loc = new Location(w, x, y, z).getBlock().getLocation().add(new Vector(0,-1,0));
			GateObject gate = new GateObject(loc, world, r, address, "null", Globals.dummyOwnerName);
			gate.setUpdated(true);
			GateManager.addGate(gate);
			Bukkit.getLogger().log(Level.INFO, "Gate: " + gate.getAddress() + " created.");
			Bukkit.getLogger().log(Level.INFO, gate.toString());
		}
		return true;
	}
	
	private static char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	
	private static boolean checkAddress(String s) {
		String o = s;
		for(char c : chars) {
			o = o.replace(c+"", "");
		}
		return o.length() < 1;
	}
	
}
