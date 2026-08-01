package com.easycommands.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CMDManager implements TabExecutor{
	
	private Map<String, CMDStruct> rootsToCMDS = new HashMap<>();
	
	private Plugin plugin;
	private String firstLabel = "[/]*[a-zA-Z0-9]*";
	private MissingPermissionHandle missingPermsHandle = (err) -> { 
		err.getPlayer().sendMessage(err.getMessage());
		return err;
	};
	
	public CMDManager(Plugin plugin) {
		this.plugin = plugin;
	}
	
	private String[] preParse(String cmd) {
		String[] parts = cmd.split(" ");
		if(!Pattern.matches(firstLabel, parts[0])) {
			parts[0] = "/" + parts[0];
		}
		return parts;
	}
	
	private String preParseLabel(String label) {
		if(Pattern.matches(firstLabel, label)) {
			return label;
		}
		return "/" + label;
	}
	
	public void register(String cmd, CMDFunction func) {
		String[] parts = preParse(cmd);
		if(!rootsToCMDS.containsKey(parts[0])) {
			rootsToCMDS.put(parts[0], new CMDStruct(parts[0], null));
		}
		try {
			rootsToCMDS.get(parts[0]).addCMD(parts, func);
		} catch (EasyCommandError e) {
			e.printStackTrace();
		}
	}
	
	public void addLookup(String cmd, CMDTabLookup lookup) {
		String[] parts = preParse(cmd);
		if(!rootsToCMDS.containsKey(parts[0])) {
			rootsToCMDS.put(parts[0], new CMDStruct(parts[0], null));
		}
		try {
			rootsToCMDS.get(parts[0]).addCMDLookup(parts, lookup);
		} catch (EasyCommandError e) {
			e.printStackTrace();
		}
	}
	
	public void addPermissionCheck(String cmd, PermissionCheck check) {
		addPermissionCheck(cmd, check, null);
	}
	
	public void addPermissionCheck(String cmd, PermissionCheck check, MissingPermissionHandle handle) {
		String[] parts = preParse(cmd);
		if(!rootsToCMDS.containsKey(parts[0])) {
			rootsToCMDS.put(parts[0], new CMDStruct(parts[0], null));
		}
		try {
			rootsToCMDS.get(parts[0]).addPermissionCheck(parts, check, handle);
		} catch (EasyCommandError e) {
			e.printStackTrace();
		}
	}
	
	public boolean call(CommandSender sender, Command cmd, String label, String[] args) throws MissingPermissions {
		label = preParseLabel(label);
		String[] tempArr = new String[args.length + 1];
	    tempArr[0] = label;
	    System.arraycopy(args, 0, tempArr, 1, args.length);
	    
	    if(rootsToCMDS.containsKey(label)) {
	    	try {
				CMDStruct struct = rootsToCMDS.get(label).search(tempArr);
				
				if(struct != null && struct.getFunc() != null) {
					if(sender instanceof Player && struct.hasPermissionCheck() && !struct.getPermissionCheck().check((Player)sender))
						throw new MissingPermissions((Player)sender, struct.getMissingPermissinHandle(), "Missing Permissions", label, args);
					return struct.getFunc().func(sender, cmd, label, args);
				}
				return false;
			} catch (EasyCommandError e) {
				e.printStackTrace();
				return false;
			}
	    }
	    return false;
	}
	
	public ArrayList<String> tabComplete(CommandSender sender, Command cmd, String label, String[] args){
		label = preParseLabel(label);
		String[] tempArr = new String[args.length];
	    tempArr[0] = label;
	    System.arraycopy(args, 0, tempArr, 1, args.length-1);
		if(rootsToCMDS.containsKey(label)) {
			return rootsToCMDS.get(label).getTabList(tempArr);
		}else {
			return null;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			return call(sender, cmd, label, args);
		} catch (MissingPermissions e) {
			MissingPermissions err = null;
			if(e.getHandle() != null) {
				err = e.getHandle().handleMissingPermission(e);
			}else if(this.missingPermsHandle != null) {
				err = this.missingPermsHandle.handleMissingPermission(e);
			}
			if(err != null) {
				return err.returnStatus();
			}
			return false;
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return tabComplete(sender, cmd, label, args);
	}
	
}
