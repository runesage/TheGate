package thegate.commands;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import thegate.gate.CommandUseType;
import thegate.gate.CommandUser;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.Config;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;

public class TheGateCommand implements TabExecutor {

	public TheGateCommand() {
	}

	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if(args.length > 0) {
			Player player = null;
			if(sender instanceof Player)
				player = (Player) sender;
			
			switch(args[0].toLowerCase()) {
			case "reload":
				return (sender instanceof Player) ? reload(player) : reload(null);
			case "permission":
				if(args.length > 1 && args[1] != null) {
					switch(args[1].toLowerCase()) {
					case "info":
						if(args.length > 2 && args[2] != null) {
							switch(args[2].toLowerCase()) {
							case "admin":
								return (sender instanceof Player) ? permissionListAdmin(player) : false;
							case "owner":
								return (sender instanceof Player) ? permissionListOwner(player) : false;
							case "user":
								return (sender instanceof Player) ? permissionListUser(player) : false;
								default:
									if(sender instanceof Player) {
										permissionSelection(player);
										return true;
									}
									return false;
							}
						}else {
							if(sender instanceof Player) {
								permissionSelection(player);
								return true;
							}
							return false;
						}
					default:
						return false;
					}
				}
				return false;
			case "syncdb":
				return (sender instanceof Player) ? SyncGatesWithDatabase.syncDB(player) : SyncGatesWithDatabase.syncDB(null); 
			case "help":
				return (sender instanceof Player) ? theGateCommands(player) : theGateCommands(null); 
			case "gatedescription":
				if (args.length < 3) {
					if(sender instanceof Player) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.MissingArguments"));
						(player).sendMessage("/thegate gatedescription <address> <description>");
					}else {
						TheGateMain.theGateMain.getLogger().log(Level.WARNING, "Missing arguments!");
						TheGateMain.theGateMain.getLogger().log(Level.WARNING, "/thegate gatedescription <address> <description>");
					}
					return true;
				}
				if(sender instanceof Player) {
					if(!(player).hasPermission(Perms.thegate_owner_gatedescriptioncommand.value())
							&& !(player).hasPermission(Perms.thegate_admin_gatedescriptioncommand.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
				}
				String address = args[1];
				String desc = "";
				for(int i = 2; i < args.length; i++) {
					desc += args[i] + (i < args.length - 1 ? " " : "");
				}
				return (sender instanceof Player) ? gateDescriptionCommand(player, address, desc) : gateDescriptionCommand(null, address, desc);
				
			case "gatename":
				if (args.length < 3) {
					if(sender instanceof Player) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.MissingArguments"));
						(player).sendMessage("/gatename <address> <name>");
					}else {
						TheGateMain.theGateMain.getLogger().log(Level.WARNING, "Missing arguments!");
						TheGateMain.theGateMain.getLogger().log(Level.WARNING, "/thegate gatename <address> <name>");
					}
					return true;
				}
				
				if(sender instanceof Player) {
					if(!(player).hasPermission(Perms.thegate_owner_gatenamecommand.value())
							&& !(player).hasPermission(Perms.thegate_admin_gatenamecommand.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
				}
				
				String address2 = args[1];
				String name = "";
				
				for(int i = 2; i < args.length; i++) {
					name += args[i] + " ";
				}
				name = name.substring(0, name.length()-1);
				
				return (sender instanceof Player) ? gatenamingcommand(player, address2, name) : gatenamingcommand(null, address2, name);
			
			case "create":
				if(sender instanceof Player) {
					if(!(player).hasPermission(Perms.thegate_owner_gatecreatecommand.value())
							&& !(player).hasPermission(Perms.thegate_admin_gatecreatecommand.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
					if(!(player).hasPermission(Perms.thegate_owner_creategate.value())
							&& !(player).hasPermission(Perms.thegate_admin_creategate.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
					if(GateManager.GateInRadius((player).getLocation())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateNear"));
						return true;
					}
					if(!(player).hasPermission(Perms.thegate_admin_creategate.value()) && GateManager.getPlayerGateAmmount((player)) >= Globals.PlayerGateAmmount) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateAmmountCap"));
						return true;
					}
					if(args.length < 2) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.MissingArguments"));
						(player).sendMessage("/thegate create <address> [angle]");
						return true;
					}
					float rot = -1;
					if(args.length > 2) {
						try {
							rot = Float.valueOf(args[2]);
						}catch(Exception e) {
							(player).sendMessage(ChatColor.RED + "Casting error!");
							(player).sendMessage("/thegate create <address> [angle]");
							(player).sendMessage("angle := [0, 360]");
							return true;	
						}
					}
					return CreateGateCommand.create(player, args[1], "", 0, 0, 0, rot);
				}else {
					if(args.length < 7) {
						Bukkit.getLogger().log(Level.WARNING, "Missing agruments!");
						Bukkit.getLogger().log(Level.WARNING, "/thegate create <address> <worldname> <x> <y> <z> <angle>");
						return true;
					}
					try {
						int x = Integer.valueOf(args[3]);
						int y = Integer.valueOf(args[4]);
						int z = Integer.valueOf(args[5]);
						float rot = Float.valueOf(args[6]);

						return CreateGateCommand.create(null, args[1], args[2], x, y, z, rot);
					}catch(Exception e) {
						Bukkit.getLogger().log(Level.WARNING, "Argument casting faild!");
						Bukkit.getLogger().log(Level.WARNING, "One of the coordinates could not be casted to a number!");
						Bukkit.getLogger().log(Level.WARNING, "/thegate create <address> <worldname> <x> <y> <z> <angle>");
						Bukkit.getLogger().log(Level.WARNING, "angle := [0, 360]");
						return true;
					}
				}
			case "rotate":
				if(sender instanceof Player) {
					if(!(player).hasPermission(Perms.thegate_owner_gaterotatecommand.value())
							&& !(player).hasPermission(Perms.thegate_admin_gaterotatecommand.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
					if(args.length < 3) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.MissingArguments"));
						(player).sendMessage("/thegate rotate <address> <angle>");
						(player).sendMessage("angle := [0, 360]");
						return true;
					}
					return RotateGateCommand.RotateGate((player), args[1], args[2]);
				}else {
					if(args.length < 3) {
						Bukkit.getLogger().log(Level.WARNING, "Missing agruments!");
						Bukkit.getLogger().log(Level.WARNING, "/thegate rotate <address> <angle>");
						Bukkit.getLogger().log(Level.WARNING, "angle := [0, 360]");
						return true;
					}
					return RotateGateCommand.RotateGate(null, args[1], args[2]);
				}
			case "rotatenear":
				if(sender instanceof Player) {
					if(!(player).hasPermission(Perms.thegate_owner_gaterotatenearcommand.value())
							&& !(player).hasPermission(Perms.thegate_admin_gaterotatenearcommand.value())) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						return true;
					}
					if(args.length < 2) {
						(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.MissingArguments"));
						(player).sendMessage("/thegate rotatenear <angle>");
						(player).sendMessage("angle := [0, 360]");
						return true;
					}
					return RotateGateCommand.RotateGate((player), null, args[1]);
				}
				return true;
				
			case "commands":
				if(!(player).hasPermission(Perms.thegate_admin_editgatecommands.value())) {
					(player).sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
					return true;
				}
				return GateCommands.handle(args, player);
			default:
				return false; 
			}
		}else {
			if(sender instanceof Player) {
				return thegateInfo((Player) sender);
			}else {
				Plugin pl = TheGateMain.theGateMain;
				pl.getLogger().log(Level.INFO, "----------------------------------------------------------------------------");
				pl.getLogger().log(Level.INFO, "|                           The_Gate_debug_info                            |");
				pl.getLogger().log(Level.INFO, "----------------------------------------------------------------------------");
				pl.getLogger().log(Level.INFO, TheGateMain.theGateMain.getDescription().getName() + "_v" + TheGateMain.theGateMain.getDescription().getVersion());
				pl.getLogger().log(Level.INFO, "Minecraft Server Version: " + TheGateMain.theGateMain.getServer().getVersion());
				pl.getLogger().log(Level.INFO, "Minecraft Bukkit Version: " + TheGateMain.theGateMain.getServer().getBukkitVersion());
				pl.getLogger().log(Level.INFO, "Java version: " + System.getProperty("java.version"));
				pl.getLogger().log(Level.INFO, "Player count: " + TheGateMain.theGateMain.getServer().getOnlinePlayers().size() + "/" + TheGateMain.theGateMain.getServer().getMaxPlayers());
				pl.getLogger().log(Level.INFO, "----------------------------------------------------------------------------");
				pl.getLogger().log(Level.INFO, "Using bungee: " + Globals.UseBungee);
				pl.getLogger().log(Level.INFO, "Server name: " + Globals.ServerName);
				pl.getLogger().log(Level.INFO, "Used save format: " + Globals.SaveFromat);
				long min = ChronoUnit.MINUTES.between(TheGateMain.theGateMain.AutoSyncTime, LocalDateTime.now());
				long sec = ChronoUnit.SECONDS.between(TheGateMain.theGateMain.AutoSyncTime, LocalDateTime.now());
				if(TheGateMain.theGateMain.AutoSyncTime != null) pl.getLogger().log(Level.INFO, "LastAutoSync: " + (min <= 0 ? sec + "sec" : min + "min"));
				int total = GateManager.getGatesAsSet().size()+GateManager.getGatesOnOtherServer().size();
				pl.getLogger().log(Level.INFO, "Gate count: " + GateManager.getGatesAsSet().size() + "/" + total);
				pl.getLogger().log(Level.INFO, "----------------------------------------------------------------------------");
			}
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		if(args.length == 1) {
			return Lists.newArrayList("help", "reload", "permission", "syncdb", "gatedescription", "gatename", "create", "rotate", "rotatenear", "commands");
		}
		
		if(args.length == 2) {
			switch(args[0].toLowerCase()) {
			case "permission":
				return Lists.newArrayList("info");
			case "commands":
				return Lists.newArrayList("add", "list", "remove");
			case "rotate":
			case "gatedescription":
			case "gatename":
				return GateManager.getAvailableGatesForEdit((Player)sender)
						.stream().map(x -> x.getAddress()).collect(Collectors.toList());
				default:
					return Lists.newArrayList("");
			}
		}
		
		if(args.length == 3) {
			switch(args[0].toLowerCase()) {
			case "permission":
				return Lists.newArrayList("admin", "owner", "user");
			case "commands":
				return GateManager.getAvailableGatesForEdit((Player)sender)
						.stream().map(x -> x.getAddress()).collect(Collectors.toList());
				default:
					return Lists.newArrayList("");
			}
		}
		
		if(args.length == 4) {
			switch(args[0].toLowerCase()) {
			case "commands":
				if(args[1].equals("add")) return Arrays.stream(CommandUser.values()).map(x -> x.toString()).collect(Collectors.toList());
				if(args[1].equals("remove")) return Arrays.stream(CommandUseType.values()).map(x -> x.toString()).collect(Collectors.toList());
				default:
					return Lists.newArrayList("");
			}
		}
		
		if(args.length == 5) {
			switch(args[0].toLowerCase()) {
			case "commands":
				if(args[1].equals("add")) return Arrays.stream(CommandUseType.values()).map(x -> x.toString()).collect(Collectors.toList());
				default:
					return Lists.newArrayList("");
			}
		}
		return Lists.newArrayList("");
	}
	
	private boolean theGateCommands(Player p) {
		if(p != null) {
			p.sendMessage(ChatColor.GOLD + "(===============[" + ChatColor.YELLOW + "The_Gate Commands" + ChatColor.GOLD + "]===============)");
			p.sendMessage(ChatColor.GOLD + "{List contains only commands usable by the player}");
			if (p.hasPermission(Perms.thegate_user_gatetools.value())) textCommand(p, "[/gatetools]", "/gatetools", ChatColor.YELLOW);
			textCommand(p, "[/thegate]", "/thegate", ChatColor.YELLOW);
			textCommand(p, "[/thegate help]", "/thegate help", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_admin_reloadconfig.value())) textCommand(p, "[/thegate reload]", "/thegate reload", ChatColor.YELLOW);
			if(Globals.MoreInfo) textCommand(p, "[/thegate permission]", "/thegate permission info", ChatColor.YELLOW);
			if(p.hasPermission(Perms.thegate_admin_syncdatabase.value())) textCommand(p, "[/thegate syncdb]", "/thegate syncdb", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_owner_gatedescriptioncommand.value()) || p.hasPermission(Perms.thegate_admin_gatedescriptioncommand.value())) textCommandSuggest(p, "[/thegate gatedescription <address> <description>]", "/thegate gatedescription", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_owner_gatenamecommand.value()) || p.hasPermission(Perms.thegate_admin_gatenamecommand.value())) textCommandSuggest(p, "[/thegate gatename <address> <name>]", "/thegate gatename", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_owner_gatecreatecommand.value()) || p.hasPermission(Perms.thegate_admin_gatecreatecommand.value())) textCommandSuggest(p, "[/thegate create <address> [angle]]", "/thegate create", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_owner_gaterotatecommand.value()) || p.hasPermission(Perms.thegate_admin_gaterotatecommand.value())) textCommandSuggest(p, "[/thegate rotate <address> <angle>]", "/thegate rotate", ChatColor.YELLOW);
			if (p.hasPermission(Perms.thegate_owner_gaterotatenearcommand.value()) || p.hasPermission(Perms.thegate_admin_gaterotatenearcommand.value())) textCommandSuggest(p, "[/thegate rotatenear <angle>]", "/thegate rotatenear", ChatColor.YELLOW);

		}else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "This command will give an interactive list ingame.");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "Commands:");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "/thegate [reload|help|syncdb|gatedescription|gatename|create|rotate]");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "alias-> /tg");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "/gatetools");
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "alias-> /gt");
		}
		return true;
	}
	
	private boolean thegateInfo(Player p) {
		p.sendMessage("§6[The_Gate]: " + ((Plugin)TheGateMain.theGateMain).getDescription().getName() + "_v" + ((Plugin)TheGateMain.theGateMain).getDescription().getVersion());
		if(Globals.MoreInfo) {
			p.sendMessage("§6GateVisibilityRadius: " + "§3" + Globals.VisibilityRadius);
			p.sendMessage("§6ActiveTime: " + "§3" + Globals.GateTime);
			p.sendMessage("§6UserOwnedGates: " + "§3" + Globals.PlayerGateAmmount);
			p.sendMessage("§6DeadlyVortex: " + (Globals.DeadlyVortex ? "§2" : "§4") + Globals.DeadlyVortex);
			p.sendMessage("§6GateCanBreakBlocks: " + (Globals.GateCanBreakBlocks ? "§2" : "§4") + Globals.GateCanBreakBlocks);
			p.sendMessage("§6DestrucitonSettings: ");
			p.sendMessage("§6- -SelectionX: " + "§3" + Globals.SelectionX);
			p.sendMessage("§6- -Selection-X: " + "§3" + Globals.Selection_X);
			p.sendMessage("§6- -SelectionY: " + "§3" + Globals.SelectionY);
			p.sendMessage("§6- -Selection-Y: " + "§3" + Globals.Selection_Y);
			p.sendMessage("§6- -Distance: " + "§3" + Globals.DestructionDistance);
			p.sendMessage("§6- -DistanceMult: " + "§3" + Globals.DestructionDistanceMult);
			p.sendMessage("§6- -Radius: " + "§3" + Globals.Radius);
			p.sendMessage("§6GateExclusionRadius: " + "§3" + Globals.GateExclusionRadius);
			long min = ChronoUnit.MINUTES.between(TheGateMain.theGateMain.AutoSyncTime, LocalDateTime.now());
			long sec = ChronoUnit.SECONDS.between(TheGateMain.theGateMain.AutoSyncTime, LocalDateTime.now());
			if(TheGateMain.theGateMain.AutoSyncTime != null) p.sendMessage("§6LastAutoSync: " + "§3" + (min <= 0 ? sec + "sec" : min + "min"));
		}
		return true;
	}
	
	private void permissionSelection(Player p) {
		TextComponent PreText = new TextComponent("Show permissions: ");
		PreText.setColor(ChatColor.GOLD);
		TextComponent TextComAdmin = new TextComponent("[Admin]");
		TextComAdmin.setColor(ChatColor.DARK_RED);
		TextComAdmin.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thegate permission info admin"));
		TextComponent TextComOwner = new TextComponent(" [Owner] ");
		TextComOwner.setColor(ChatColor.YELLOW);
		TextComOwner.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thegate permission info owner"));
		TextComponent TextComUser = new TextComponent("[User]");
		TextComUser.setColor(ChatColor.GRAY);
		TextComUser.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/thegate permission info user"));
		p.spigot().sendMessage(PreText, TextComAdmin, TextComOwner, TextComUser);
	}
	
	private boolean permissionListAdmin(Player p) {
		if(!Globals.MoreInfo) return true;
		p.sendMessage(ChatColor.DARK_AQUA + "(===============[" + ChatColor.DARK_RED + "Admin" + ChatColor.GOLD + " Permissions" + ChatColor.DARK_AQUA + "]===============)");
		for(Perms perm : Perms.values()) {
			String permOut = perm.value();
			if(permOut.contains(".admin.")) p.sendMessage(hasPerm(p, permOut));
		}
		p.sendMessage(ChatColor.DARK_AQUA + "(============================================)");
		permissionSelection(p);
		return true;
	}
	
	private boolean permissionListOwner(Player p) {
		if(!Globals.MoreInfo) return true;
		p.sendMessage(ChatColor.DARK_AQUA + "(===============[" + ChatColor.YELLOW + "Owner" + ChatColor.GOLD + " Permissions" + ChatColor.DARK_AQUA + "]===============)");
		for(Perms perm : Perms.values()) {
			String permOut = perm.value();
			if(permOut.contains(".owner.")) p.sendMessage(hasPerm(p, permOut));
		}
		p.sendMessage(ChatColor.DARK_AQUA + "(============================================)");
		permissionSelection(p);
		return true;
	}
	
	private boolean permissionListUser(Player p) {
		if(!Globals.MoreInfo) return true;
		p.sendMessage(ChatColor.DARK_AQUA + "(===============[" + ChatColor.GRAY + "User" + ChatColor.GOLD + " Permissions" + ChatColor.DARK_AQUA + "]===============)");
		for(Perms perm : Perms.values()) {
			String permOut = perm.value();
			if(permOut.contains(".user.")) p.sendMessage(hasPerm(p, permOut));
		}
		p.sendMessage(ChatColor.DARK_AQUA + "(============================================)");
		permissionSelection(p);
		return true;
	}
	
	private boolean gateDescriptionCommand(Player p, String address, String description) {
		if (p != null) {
			GateObject obj = GateManager.getGateWithAddress(address);
			if (obj != null) {
				if(p.hasPermission(Perms.thegate_admin_editgate.value()) || p.hasPermission(Perms.thegate_admin_editallgates.value()) || (p.hasPermission(Perms.thegate_owner_editgate.value()) && obj.hasOwningRelation(p))) {
					
					if(description.length() > 64) {
						p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.ToManyChars", "{CHAR_AMMOUNT}", "64"));
						return true;
					}
					
					obj.setDescription(description);
					obj.Repair();
					TheGateMain.SaveLoadInterface.updateGate(obj);
					p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.DescriptionSet"));	
				}else {
					p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				}
			} else {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGateWithAddress", "{ADDRESS}", address));
				return false;
			}

		} else if (p == null) {

			GateObject obj = GateManager.getGateWithAddress(address);
			if (obj != null) {
				
				if(description.length() > 64) {
					TheGateMain.theGateMain.getLogger().log(Level.WARNING, "The given text is to long! Only 64 characters are allowed!");
					return false;
				}
				
				obj.setDescription(description);
				obj.Repair();
				TheGateMain.SaveLoadInterface.updateGate(obj);
				TheGateMain.theGateMain.getLogger().log(Level.INFO, "Gate Discription set!");
			} else {
				TheGateMain.theGateMain.getLogger().log(Level.WARNING, "Gate with address: " + address + " could not be found!");
				return false;
			}
		}

		return true;
	}
	
	private boolean gatenamingcommand(Player p, String address, String name) {
		if (p != null) {
			GateObject obj = GateManager.getGateWithAddress(address);
			if (obj != null) {
				if(p.hasPermission(Perms.thegate_admin_editgate.value()) || p.hasPermission(Perms.thegate_admin_editallgates.value()) || (p.hasPermission(Perms.thegate_owner_editgate.value()) && obj.hasOwningRelation(p))) {
					
					if(name.length() > 20) {
						p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.ToManyChars", "{CHAR_AMMOUNT}", "20"));
						return false;
					}
					obj.setGateName(name);
					obj.Repair();
					TheGateMain.SaveLoadInterface.updateGate(obj);
					p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateNameSet"));	
				}else {
					p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				}
			} else {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGateWithAddress", "{ADDRESS}", address));
				return false;
			}
		} else if (p == null) {
			GateObject obj = GateManager.getGateWithAddress(address);
			if (obj != null) {
				
				if(name.length() > 20) {
					TheGateMain.theGateMain.getLogger().log(Level.WARNING, "The given text is to long! Only 20 characters are allowed!");
					return false;
				}
				
				obj.setGateName(name);
				obj.Repair();
				TheGateMain.SaveLoadInterface.updateGate(obj);
				TheGateMain.theGateMain.getLogger().log(Level.INFO, "Gate Discription set!");
			} else {
				TheGateMain.theGateMain.getLogger().log(Level.WARNING, "Gate with address: " + name + " could not be found!");
				return false;
			}
		}
		return true;
	}
	
	private String hasPerm(Player p, String permission) {
		boolean hasPerm = p.hasPermission(permission);
		ChatColor c = hasPerm ? ChatColor.DARK_GREEN : ChatColor.DARK_RED;
		return ChatColor.GRAY + "->" + permission + (permission.charAt(permission.length()-1) == '.' ? "<args>" : "") + ": " + c + "[" + String.valueOf(hasPerm).toUpperCase() + "]";
	}
	
	private boolean reload(Player p) {
		if (p != null) {
			if (!p.hasPermission(Perms.thegate_admin_reloadconfig.value())) {
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));;
				return true;
			}
			if (p.hasPermission(Perms.thegate_admin_reloadconfig.value())) {
				TheGateMain.theGateMain.reloadConfig();
				Config.LoadConfig(TheGateMain.theGateMain);
				TheGateMain.theGateMain.configManager.CreateConfigFiles();
				GateManager.repairAll();
				p.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.ConfigReload"));
				return true;
			}
		}
		TheGateMain.theGateMain.reloadConfig();
		Config.LoadConfig(TheGateMain.theGateMain);
		TheGateMain.theGateMain.configManager.CreateConfigFiles();
		
		GateManager.setGatesOnOtherServer(TheGateMain.SaveLoadInterface.GateListOtherServers());
		GateManager.repairAll();
		TheGateMain.theGateMain.getLogger().log(Level.INFO, "Config reloaded!");
		return true;
	}
	
	private void textCommand(Player p, String msg, String command, ChatColor c) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
		message.setColor(c);
		p.spigot().sendMessage(message);
	}
	
	private void textCommandSuggest(Player p, String msg, String command, ChatColor c) {
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
		message.setColor(c);
		p.spigot().sendMessage(message);
	}

	
}
