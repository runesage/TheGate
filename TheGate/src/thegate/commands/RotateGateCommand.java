package thegate.commands;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Perms;

public class RotateGateCommand {

	public static boolean RotateGate(Player p, String address, String angle) {

		float a = 0f;
		try {
			a = Float.valueOf(angle);
		}catch(NumberFormatException e) {
			if(p != null) {
				p.sendMessage(ChatColor.RED + "Casting error!");
			}else {
				Bukkit.getLogger().log(Level.WARNING, "Casting Error!");
			}
			return true;
		}
		
		GateObject gate = address != null ? GateManager.getGateWithAddress(address) : GateManager.getClosestGateTo(p.getLocation());
		if(p != null && gate == null) {
			p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGateWithAddress", "{ADDRESS}", address));
			return true;
		}
		if(p != null) {
			if(!(p.hasPermission(Perms.thegate_owner_editgate.value()) && gate.hasOwningRelation(p)) && !p.hasPermission(Perms.thegate_admin_editgate.value())) {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				return true;
			}
		}
		
		if(gate != null) {
			gate.setFacing(((a/360.0f)*4.0f)%4);
			gate.setUpdated(true);
			gate.Repair();
		}else {
			if(p != null) {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGateWithAddress", "{ADDRESS}", address));
			}else {
				Bukkit.getLogger().log(Level.WARNING, "No gate found with address: " + address);
			}
			return true;
		}
		
		return true;
	}
	
}
