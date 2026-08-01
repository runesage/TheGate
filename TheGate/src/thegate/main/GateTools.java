package thegate.main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gui.tools.guitools.InventoryManager;

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.guis.AbydosGUI;
import thegate.guis.AddressAssignGUI;
import thegate.guis.DHD_GUI;
import thegate.guis.EditAllGatesGUI;
import thegate.guis.EditGateGUI;
import thegate.guis.GateCrystalGUI;
import thegate.guis.IrisIDCGUI;
import thegate.math.GateMath;

public class GateTools implements CommandExecutor, Listener {
	
	public GateTools() {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (player.hasPermission(Perms.thegate_user_gatetools.value())) {

				GATETOOLSGUI(player);

			} else {
				player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
			}
		} else {
			TheGateMain.theGateMain.getLogger().log(Level.INFO, "Command only for players");
		}

		return true;
	}

	private void GATETOOLSGUI(Player player) {

		Inventory invent = Bukkit.createInventory(null, 9 * 1, ConfigManager.getString("GUIS.GateTools.GUIName"));

		if (player.hasPermission(Perms.thegate_admin_creategate.value())
				|| player.hasPermission(Perms.thegate_owner_creategate.value())) {
			ItemStack GateCreator = new ItemStack(Globals.DefaultGateCreationTool);
			ItemMeta GateCreatormeta = GateCreator.getItemMeta();
			GateCreatormeta.setDisplayName(ConfigManager.getString("GUIS.GateTools.Items.CreateTool.Name"));
			ArrayList<String> itemlore = new ArrayList<String>();
			itemlore.add(ConfigManager.getString("GUIS.GateTools.Items.CreateTool.Lore"));
			GateCreatormeta.setLore(itemlore);
			GateCreator.setItemMeta(GateCreatormeta);
			invent.setItem(2, GateCreator);
		}

		if (player.hasPermission(Perms.thegate_admin_editgate.value())
				|| player.hasPermission(Perms.thegate_admin_editallgates.value())
				|| player.hasPermission(Perms.thegate_owner_editgate.value())) {
			ItemStack GateEditor = new ItemStack(Globals.DefaultGateEditTool);
			ItemMeta GateEditormeta = GateEditor.getItemMeta();
			GateEditormeta.setDisplayName(ConfigManager.getString("GUIS.GateTools.Items.EditTool.Name"));
			ArrayList<String> GateEditorlore = new ArrayList<String>();
			GateEditorlore.add(ConfigManager.getString("GUIS.GateTools.Items.EditTool.Lore"));
			GateEditormeta.setLore(GateEditorlore);
			GateEditor.setItemMeta(GateEditormeta);
			invent.setItem(3, GateEditor);
		}

		if (player.hasPermission(Perms.thegate_admin_abydoscartouche.value())
				|| player.hasPermission(Perms.thegate_user_abydoscartouche.value())) {
			ItemStack abydoscartouche = new ItemStack(Globals.DefaultAbydosCartouche);
			ItemMeta abydoscartouchemeta = abydoscartouche.getItemMeta();
			abydoscartouchemeta.setDisplayName(ConfigManager.getString("GUIS.GateTools.Items.AbydosCartouche.Name"));
			ArrayList<String> abydoscartouchelore = new ArrayList<String>();
			abydoscartouchelore.add(ConfigManager.getString("GUIS.GateTools.Items.AbydosCartouche.Lore"));
			abydoscartouchemeta.setLore(abydoscartouchelore);
			abydoscartouche.setItemMeta(abydoscartouchemeta);
			invent.setItem(4, abydoscartouche);
		}
		
		if (player.hasPermission(Perms.thegate_user_idctransmitter.value())) {
			ItemStack IDCTransmitter = new ItemStack(Globals.DefaultIDCTransmitter);
			ItemMeta IDCTransmittermeta = IDCTransmitter.getItemMeta();
			IDCTransmittermeta.setDisplayName(ConfigManager.getString("GUIS.GateTools.Items.IDCTransmitter.Name"));
			ArrayList<String> IDCTransmitterLore = new ArrayList<String>();
			IDCTransmitterLore.add(ConfigManager.getString("GUIS.GateTools.Items.IDCTransmitter.Lore"));
			IDCTransmittermeta.setLore(IDCTransmitterLore);
			IDCTransmitter.setItemMeta(IDCTransmittermeta);
			invent.setItem(5, IDCTransmitter);
		}

		if (player.hasPermission(Perms.thegate_user_gatetools.value())) {
			player.openInventory(invent);
		} else {
			player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getMaterial().equals(Globals.DefaultGateCreationTool)) {
			if (e.getItem().hasItemMeta()) {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().contains(ConfigManager.getString("GUIS.GateTools.Items.CreateTool.Name"))) {
					
					if(GateManager.GateInRadius(e.getClickedBlock().getLocation())) {
						player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateNear"));
						return;
					}
					
					if (meta.hasLore()) {
						List<String> itemlore = meta.getLore();
						if (itemlore.get(0).equals(ConfigManager.getString("GUIS.GateTools.Items.CreateTool.Lore"))) {

							if (player.hasPermission(Perms.thegate_admin_creategate.value())) {
								if (!player.getOpenInventory().getTitle().contains(ConfigManager.getString("GUIS.AddressAssignGUI.GUIName"))) {
									AddressAssignGUI addressAssignGUI = new AddressAssignGUI(e.getPlayer(), ConfigManager.getString("GUIS.AddressAssignGUI.GUIName"), e.getClickedBlock().getLocation(), e.getClickedBlock().getType());
									if(addressAssignGUI.OpenGUI()) InventoryManager.addGUI(addressAssignGUI);
								}
								return;
							} else if (player.hasPermission(Perms.thegate_owner_creategate.value())) {
								if (!player.getOpenInventory().getTitle().contains(ConfigManager.getString("GUIS.AddressAssignGUI.GUIName"))
										&& GateManager.getPlayerGateAmmount(player) < Globals.PlayerGateAmmount) {
									AddressAssignGUI addressAssignGUI = new AddressAssignGUI(e.getPlayer(), ConfigManager.getString("GUIS.AddressAssignGUI.GUIName"), e.getClickedBlock().getLocation(), e.getClickedBlock().getType());
									if(addressAssignGUI.OpenGUI()) InventoryManager.addGUI(addressAssignGUI);
								}else {
									player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateAmmountCap"));
									return;
								}
								return;
							} else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
							}

						}

					}
				}
			}
		}else if(e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getMaterial().equals(Globals.DefaultGateCreationTool)) {
			if (e.getItem().hasItemMeta()) {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().contains(ConfigManager.getString("GUIS.GateTools.Items.CreateTool.Name"))) {
					GateObject obj = GateManager.getClosestGateTo(player.getLocation());
					if(obj == null) {
						player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGatesNearBy"));
						return;
					}
					
					boolean isCoowner = obj.isCoowner(player);
					if(player.hasPermission(Perms.thegate_admin_creategate.value()) ||
							player.hasPermission(Perms.thegate_owner_creategate.value()) ||
							player.hasPermission(Perms.thegate_admin_editgate.value())) {
						
						if(obj.getOwnerUUID().equals(player.getUniqueId().toString()) || player.hasPermission(Perms.thegate_admin_editgate.value()) || (isCoowner && player.hasPermission(Perms.thegate_owner_creategate.value()))) {
							if(GateMath.getDistance(obj.getGate().getX(), obj.getGate().getY(), obj.getGate().getZ(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()) <= 10) {
								GateCrystalGUI gcg = new GateCrystalGUI(player, ConfigManager.getString("GUIS.GateCrystalGUI.GUIName"), obj.getAddress());
								if(gcg.OpenGUI()) InventoryManager.addGUI(gcg);	
							}else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoGatesNearBy"));
								return;
							}
						}else {
							player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoOwnership"));
						}
						
					}
				}
			}
		}

		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getMaterial().equals(Globals.DefaultGateEditTool)) {
			if (e.getItem().hasItemMeta()) {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().contains(ConfigManager.getString("GUIS.GateTools.Items.EditTool.Name"))) {
					if (meta.hasLore()) {
						List<String> itemlore = meta.getLore();
						if (itemlore.get(0).equals(ConfigManager.getString("GUIS.GateTools.Items.EditTool.Lore"))) {

							if (player.hasPermission(Perms.thegate_admin_editallgates.value())) {
								EditAllGatesGUI editAllGates = new EditAllGatesGUI(player, ConfigManager.getString("GUIS.EditAllGatesGUI.GUIName"));
								if(editAllGates.OpenGUI()) InventoryManager.addGUI(editAllGates);
							} else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
							}

						}

					}
				}
			}
		}

		if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& e.getMaterial().equals(Globals.DefaultAbydosCartouche)) {
			if (e.getItem().hasItemMeta()) {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().contains(ConfigManager.getString("GUIS.GateTools.Items.AbydosCartouche.Name"))) {
					if (meta.hasLore()) {
						List<String> itemlore = meta.getLore();
						if (itemlore.get(0).equals(ConfigManager.getString("GUIS.GateTools.Items.AbydosCartouche.Lore"))) {
								
							if (player.hasPermission(Perms.thegate_admin_abydoscartouche.value())
									|| player.hasPermission(Perms.thegate_user_abydoscartouche.value())) {
								AbydosGUI abydosGUI = new AbydosGUI(e.getPlayer(), ConfigManager.getString("GUIS.AbydosGUI.GUIName"));
								if(abydosGUI.OpenGUI()) InventoryManager.addGUI(abydosGUI);
							} else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
							}

						}

					}
				}
			}
		}

		if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.getMaterial().equals(Globals.DefaultGateCrystal)) {
			if (e.getItem().hasItemMeta()) {
				ItemStack item = e.getItem();
				ItemMeta meta = item.getItemMeta();
				if (meta.getDisplayName().equals(ConfigManager.getString("GUIS.GateCrystalGUI.Items.DHDCrystal.Name"))) {
					if (meta.hasLore()) {
						List<String> itemlore = meta.getLore();
						if (itemlore.get(0).equals(ConfigManager.getString("GUIS.GateCrystalGUI.Items.DHDCrystal.Lore"))) {

							if (player.hasPermission(Perms.thegate_admin_creategate.value()) || player.hasPermission(Perms.thegate_owner_creategate.value())) {
								if (GateManager.hasGateWithAddress(itemlore.get(1))) {
									CreateDHD(e.getClickedBlock().getLocation(), itemlore.get(1), player);
								}
							} else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
							}

						}

					}
				}
			}

		}
			
		if ((e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			if (e.getClickedBlock().getType().equals(Globals.DefaultDHDMaterial)) {
				if (GateManager.hasDHDatLocation(e.getClickedBlock().getLocation())) {
					e.setCancelled(true);
					e.setCancelled(true);
					if (e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
						ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						if (item.getType().equals(Globals.DefaultGateEditTool)
								&& meta.getDisplayName().contains(ConfigManager.getString("GUIS.GateTools.Items.EditTool.Name"))) {
							GateObject gate = GateManager.getGateByDHD(e.getClickedBlock().getLocation());
							if(gate == null) return;
							if (player.hasPermission(Perms.thegate_admin_editgate.value())
									|| player.hasPermission(Perms.thegate_owner_editgate.value()) || (gate.getCoOwner().containsKey(player.getUniqueId().toString()) && player.hasPermission(Perms.thegate_owner_editgate.value()))) {
								if (!player.hasPermission(Perms.thegate_admin_editallgates.value())) {
									if (player.hasPermission(Perms.thegate_owner_editgate.value())) {
										if (!gate.getOwnerUUID().equals(player.getUniqueId().toString()) && !gate.getCoOwner().containsKey(player.getUniqueId().toString())) {
											player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoOwnership"));
											return;
										}
									}
								}

								EditGateGUI egg = new EditGateGUI(player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", gate.getAddress()), gate.getAddress(), gate);
								if(egg.OpenGUI()) InventoryManager.addGUI(egg);
							
							} else {
								player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
							}

						}

					} else if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
						GateObject gate = GateManager.getGateByDHD(e.getClickedBlock().getLocation());

						if (player.hasPermission(Perms.thegate_user_dialgate.value()) || player.hasPermission(Perms.thegate_admin_dialgate.value())) {
							if (gate != null) {

								if (!player.getOpenInventory().getTitle().contains(ConfigManager.getString("GUIS.DHD_GUI.GUIName"))) {
									DHD_GUI dhd = new DHD_GUI(e.getPlayer(), ConfigManager.getString("GUIS.DHD_GUI.GUIName", "{ADDRESS}", gate.getAddress()), gate);
									if(dhd.OpenGUI()) InventoryManager.addGUI(dhd);
								}

							}
						} else {
							player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
						}

					}

				}
			}
		}

		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack i = e.getItem();
			if(i != null && i.getType().equals(Globals.DefaultIDCTransmitter)) {
				if(i.hasItemMeta() && i.getItemMeta().getDisplayName().equals(ConfigManager.getString("GUIS.GateTools.Items.IDCTransmitter.Name")) &&
						i.getItemMeta().hasLore() && i.getItemMeta().getLore().get(0).equals(ConfigManager.getString("GUIS.GateTools.Items.IDCTransmitter.Lore"))) {
					GateObject obj = GateManager.getClosestGateTo(e.getPlayer().getLocation());
					e.setCancelled(true);
					if(obj == null) return;
					Vector v = obj.getGate().toVector().add(e.getPlayer().getLocation().toVector().multiply(-1));
					if(GateMath.getVectorLength(v) < 30 && obj.isActive() && obj.isDialingout()) {
						GateObject connected = GateManager.getGateWithAddress(obj.getDiled());
						if(connected == null) return;
						IrisIDCGUI idcGUI = new IrisIDCGUI(e.getPlayer(), "IDCGUI", "IDCGUI", connected);
						if(idcGUI.OpenGUI()) InventoryManager.addGUI(idcGUI);
					}
				}
			}
		}
		
	}

	public void CreateDHD(Location loc, String Address, Player player) {
		if (loc.getBlock().getType().equals(Globals.DefaultDHDMaterial)) {

			if (GateManager.hasGateWithAddress(Address)) {
				GateManager.getGateWithAddress(Address).setDHD(loc);
				TheGateMain.SaveLoadInterface.updateGate(GateManager.getGateWithAddress(Address));
				player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.LinkGate", "{ADDRESS}", Address));

				player.getInventory().setItemInMainHand(null);
				
			} else {
				player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateNotFound", "{ADDRESS}", Address));
}

		} else {

			player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.WrongDHDMaterial") + Globals.DefaultDHDMaterial.toString());

		}
	}

}
