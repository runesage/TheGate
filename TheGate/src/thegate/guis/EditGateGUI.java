package thegate.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import com.gui.tools.guitools.GUIBase;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.BlockedState;
import thegate.gate.CreateGate;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;
import thegate.math.GateMath;

public class EditGateGUI extends GUIBase{
	public String address;
	public GateObject GATE;
	
	public EditGateGUI(Player p, String name, String Address, GateObject gate) {
		super(p, 9 * 4, name, "EditGateGUI");
		this.address = Address;
		this.GATE = gate;
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		setDefaultErrorMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
		setFilterUnaccassable(false);
		
		
		addUIAccessPermission(Perms.thegate_owner_editgate.value(), Perms.thegate_admin_editgate.value());
		
		additemPerms(0, Perms.thegate_owner_lockgate.value(), Perms.thegate_admin_lockgate.value());
		additemPerms(1, Perms.thegate_owner_setprivate.value(), Perms.thegate_admin_setprivate.value());
		additemPerms(3, Perms.thegate_owner_repairgate.value(), Perms.thegate_admin_repairgate.value());
		additemPerms(4, Perms.thegate_owner_repairgate.value(), Perms.thegate_admin_repairgate.value());
		additemPerms(5, Perms.thegate_owner_repairgate.value(), Perms.thegate_admin_repairgate.value());
		additemPerms(8, Perms.thegate_owner_removegate.value(), Perms.thegate_admin_removegate.value());
		additemPerms(13, Perms.thegate_owner_tptogate.value(), Perms.thegate_admin_tptogate.value());
		additemPerms(24, Perms.thegate_owner_editnetwork.value(), Perms.thegate_admin_editnetwork.value());
		additemPerms(25, Perms.thegate_owner_editnetwork.value(), Perms.thegate_admin_editnetwork.value());
		additemPerms(17, Perms.thegate_owner_creategate.value(), Perms.thegate_admin_creategate.value());
		
		for (int i = 9*3; i < 9 * 4; i++) {
			additemPerms(i, Perms.thegate_owner_editmaterial.value(), Perms.thegate_admin_editmaterial.value());
		}
		
		setGUIAccessCondition(c -> {
			if(!this.GATE.getOwnerUUID().equals(c.getPlayer().getUniqueId().toString()) && c.getPlayer().hasPermission(Perms.thegate_admin_editgate.value())) {
				return false;
			}
			return true;
		});
	}
	
	public void setup() {
		boolean isCoowner = GATE.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString());
		for (int i = 0; i < 9 * 4; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}

		if((getPlayer().hasPermission(Perms.thegate_owner_lockgate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_lockgate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_lockgate.value())) {
			setItem(0, GATE.getBlockedState().getMaterial(), GATE.getBlockedState().getText(), null);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_repairgate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_repairgate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_repairgate.value())) {
			setItem(3, Material.GOLDEN_PICKAXE, ConfigManager.getString("GUIS.EditGateGUI.Items.Repair"), null);

			ArrayList<String> RemoveHorizon = new ArrayList<String>();
			RemoveHorizon.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Event_Horizon.Lore1"));
			RemoveHorizon.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Event_Horizon.Lore2"));
			setItem(4, Material.ENDER_PEARL, ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Event_Horizon.Name"), RemoveHorizon);

			ArrayList<String> RemoveRing = new ArrayList<String>();
			RemoveRing.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Ring.Lore"));
			setItem(5, Material.LAVA_BUCKET, ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Ring.Name"), RemoveRing);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_creategate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_creategate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_creategate.value())) {
			ArrayList<String> RemoveDHDLore = new ArrayList<String>();
			RemoveDHDLore.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_DHD.Lore"));
			setItem(17, Globals.DefaultDHDMaterial, ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_DHD.Name"), RemoveDHDLore);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_removegate.value()) && GATE.isOwner(getPlayer())) ||
				getPlayer().hasPermission(Perms.thegate_admin_removegate.value())) {
			ArrayList<String> RemoveGateLore = new ArrayList<String>();
			RemoveGateLore.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Gate.Lore1"));
			RemoveGateLore.add(ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Gate.Lore2"));
			setItem(8, Material.SKELETON_SKULL, ConfigManager.getString("GUIS.EditGateGUI.Items.Remove_Gate.Name"), RemoveGateLore);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_editgateuseperms.value()) && GATE.hasOwningRelation(getPlayer())) ||
				getPlayer().hasPermission(Perms.thegate_admin_editgateuseperms.value())) {
			List<String> Lore = ConfigManager.getStringList("GUIS.EditGateGUI.Items.UseGatePerms.Lore", "{ADDRESS}", GATE.getAddress());
			setItem(9, (this.GATE.isUseGatePerms() ? Material.GREEN_STAINED_GLASS : Material.RED_STAINED_GLASS), ConfigManager.getString("GUIS.EditGateGUI.Items.UseGatePerms.Name", "{VALUE}", this.GATE.isUseGatePerms() + ""), Lore);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_tptogate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_tptogate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_tptogate.value())) {
			setItem(13, Material.ELYTRA, ConfigManager.getString("GUIS.EditGateGUI.Items.Teleport_to_Gate"), null);
		}
			
		if((getPlayer().hasPermission(Perms.thegate_owner_setprivate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_setprivate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_setprivate.value())) {
			if (GATE.isOpen()) {
				setItem(1, Material.CHEST, ConfigManager.getString("GUIS.EditGateGUI.Items.Open"), null);
			} else if (!GATE.isOpen()) {
				setItem(1, Material.ENDER_CHEST, ConfigManager.getString("GUIS.EditGateGUI.Items.Private"), null);
			}
		}

		if((getPlayer().hasPermission(Perms.thegate_owner_editnetwork.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_editnetwork.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_editnetwork.value())) {
			setItem(24, Material.END_CRYSTAL, "§6" + GATE.getNetwork(), null);

			if (GATE.getNetwork().equals(Globals.Networks.get(0)) && GATE.getSecondaryNetwork().equals("null")) {
				setItem(25, Material.WHITE_STAINED_GLASS_PANE, ConfigManager.getString("GUIS.EditGateGUI.Items.Secondary_Network"), null);
			} else if (!GATE.getNetwork().equals(Globals.Networks.get(0))) {
				setItem(25, Material.RED_STAINED_GLASS_PANE, ConfigManager.getString("GUIS.EditGateGUI.Items.Secondary_Network"), null);
			} else if (GATE.getNetwork().equals(Globals.Networks.get(0)) && !GATE.getSecondaryNetwork().equals("null")) {
				setItem(25, Material.END_CRYSTAL, "§6" + GATE.getSecondaryNetwork(), null);
			}
		}

		if((getPlayer().hasPermission(Perms.thegate_owner_editmaterial.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_editmaterial.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_editmaterial.value())) {
			setItem(27, Material.OAK_SIGN, ConfigManager.getString("GUIS.EditGateGUI.Items.Gate_Material"), null);
			setItem(28, Material.ENDER_PEARL, ConfigManager.getString("GUIS.EditGateGUI.Items.Reset_Material"), null);
			setItem(35, Material.REDSTONE_TORCH, ConfigManager.getString("GUIS.EditGateGUI.Items.Set_Gate_Material"), null);

			setItem(29, GATE.getRingMaterial(), ConfigManager.getString("GUIS.EditGateGUI.Items.Ring_Material"), null);
			setItem(30, GATE.getChevron_botMaterial(), ConfigManager.getString("GUIS.EditGateGUI.Items.Chevron_tip"), null);
			setItem(31, GATE.getChevrons_frameMaterial(), ConfigManager.getString("GUIS.EditGateGUI.Items.Chevron_frame"), null);
			setItem(32, GATE.getChevron_lightMaterial(), ConfigManager.getString("GUIS.EditGateGUI.Items.Chevron_Light_off"), null);
			setItem(33, GATE.getChevron_lightMaterial_on(), ConfigManager.getString("GUIS.EditGateGUI.Items.Chevron_Light_on"), null);
			setItem(34, GATE.getHorizonMaterial(), ConfigManager.getString("GUIS.EditGateGUI.Items.Horizon_Material"), null);	
		}
		
		//18 19 20
		if(!GATE.getOwnerUUID().equals("null")) {
			Player p = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(GATE.getOwnerUUID()));
			if(p != null) {
				ItemStack item = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setOwningPlayer(p);
				meta.setDisplayName(ConfigManager.getString("GUIS.EditGateGUI.Items.GateOwner.Name", "{OWNER_NAME}", GATE.getOwnerName()));
				item.setItemMeta(meta);
				setItem(18, item);
			}else {
				ItemStack item = new ItemStack(Material.SKELETON_SKULL);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ConfigManager.getString("GUIS.EditGateGUI.Items.GateOwner.Name", "{OWNER_NAME}", GATE.getOwnerName()));
				item.setItemMeta(meta);
				setItem(18, item);
			}
		}else {
			ItemStack item = new ItemStack(Material.SKELETON_SKULL);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ConfigManager.getString("GUIS.EditGateGUI.Items.GateOwner.Name", "{OWNER_NAME}", GATE.getOwnerName()));
			item.setItemMeta(meta);
			setItem(18, item);
		}
		
		
		if(GATE.isOwner(getPlayer()) || getPlayer().hasPermission(Perms.thegate_admin_editcoowner.value())) setItem(19, Material.PLAYER_HEAD, ConfigManager.getString("GUIS.EditGateGUI.Items.ListCoOwner.Name"), null);
		if(GATE.isOwner(getPlayer()) || getPlayer().hasPermission(Perms.thegate_admin_editcoowner.value())) setItem(20, Material.PLAYER_HEAD, ConfigManager.getString("GUIS.EditGateGUI.Items.AddCoOwner.Name"), null);
		
		setItem(22, Material.FIRE_CHARGE, ConfigManager.getString("GUIS.EditGateGUI.Items.IrisSettings.Name"), null);
		
	}
	
	public void setupFunctions() {
		boolean isCoowner = GATE.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString());

		//TODO: NO
		//REFACTOR ADD UI FUNCTION!!!!
		for(BlockedState b : BlockedState.values()) {
			addGUIFunction(0, x ->{
				GATE.setBlockedState(BlockedState.values()[(GATE.getBlockedState().ordinal() + 1) % BlockedState.values().length]);
				GATE.setUpdated(true);
				setItem(0, GATE.getBlockedState().getMaterial(), GATE.getBlockedState().getText(), null);
				refreshItems();
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message2", "{ADDRESS}", GATE.getAddress(), "{STATE}", GATE.getBlockedState().getText()));
			}, b.getMaterial());
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_repairgate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_repairgate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_repairgate.value())) {
			addGUIFunction(3, x ->{
				getPlayer().closeInventory();
				GATE.Repair();
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message4", "{ADDRESS}", GATE.getAddress()));
			}, Material.GOLDEN_PICKAXE);
			
			addGUIFunction(4, x ->{
				if (GATE.getDiled() != "") {
					if (GateManager.hasGateWithAddress(GATE.getDiled())) {
						GateObject otherGate = GateManager.getGateWithAddress(GATE.getDiled());
						otherGate.Deactivate();
					}
				}
				GATE.Deactivate();
				getPlayer().closeInventory();
			}, Material.ENDER_PEARL);
			
			addGUIFunction(5, x ->{
				GATE.Vanish();
				if(Globals.CreateBarrier) CreateGate.RemoveBarrier(GATE.getGate(), GATE.getFacing());
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message5", "{ADDRESS}", GATE.getAddress()));
				getPlayer().closeInventory();
			}, Material.LAVA_BUCKET);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_removegate.value()) && GATE.isOwner(getPlayer())) ||
				getPlayer().hasPermission(Perms.thegate_admin_removegate.value())) {
			addGUIFunction(8, x ->{
				GATE.Vanish();

				if(Globals.CreateBarrier) CreateGate.RemoveBarrier(GATE.getGate(), GATE.getFacing());
				TheGateMain.SaveLoadInterface.DeleateElementFromDatabase(GATE);
				
				GateManager.RemoveGate(GATE);
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message6", "{ADDRESS}", GATE.getAddress()));
				getPlayer().closeInventory();
			}, Material.SKELETON_SKULL);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_editgateuseperms.value()) && GATE.hasOwningRelation(getPlayer())) ||
				getPlayer().hasPermission(Perms.thegate_admin_editgateuseperms.value())) {
			addGUIFunction(9, x ->{
				GATE.setUseGatePerms(false);
				GATE.setUpdated(true);
				ItemStack item = this.getItem(9);
				item.setType(Material.RED_STAINED_GLASS);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ConfigManager.getString("GUIS.EditGateGUI.Items.UseGatePerms.Name", "{VALUE}", this.GATE.isUseGatePerms() + ""));
				item.setItemMeta(meta);
				this.setItem(9, item);
				this.refreshItems();
			}, Material.GREEN_STAINED_GLASS);
			
			addGUIFunction(9, x ->{
				GATE.setUseGatePerms(true);
				GATE.setUpdated(true);
				ItemStack item = this.getItem(9);
				item.setType(Material.GREEN_STAINED_GLASS);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ConfigManager.getString("GUIS.EditGateGUI.Items.UseGatePerms.Name", "{VALUE}", this.GATE.isUseGatePerms() + ""));
				item.setItemMeta(meta);
				this.setItem(9, item);
				this.refreshItems();
			}, Material.RED_STAINED_GLASS);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_creategate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_creategate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_creategate.value())) {
			addGUIFunction(17, x->{
				GATE.setDHD(null);
				GATE.setUpdated(true);
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message10", "{ADDRESS}", GATE.getAddress()));
				getPlayer().closeInventory();
			}, Globals.DefaultDHDMaterial);
		}
		
		if((getPlayer().hasPermission(Perms.thegate_owner_setprivate.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_setprivate.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_setprivate.value())) {
			addGUIFunction(1, x->{
				GATE.setOpen(false);
				GATE.setUpdated(true);
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message7", "{ADDRESS}", GATE.getAddress()));
				setItem(1, Material.ENDER_CHEST, ConfigManager.getString("GUIS.EditGateGUI.Items.Private"), null);
				refreshItems();
			}, Material.CHEST);
			
			addGUIFunction(1, x->{
				GATE.setOpen(true);
				GATE.setUpdated(true);
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message8", "{ADDRESS}", GATE.getAddress()));
				setItem(1, Material.CHEST, ConfigManager.getString("GUIS.EditGateGUI.Items.Open"), null);
				refreshItems();
			}, Material.ENDER_CHEST);
		}
		
		//addPerms
		if(getPlayer().hasPermission(Perms.thegate_admin_editcoowner.value()) || GATE.isOwner(getPlayer())) {
			addGUIFunction(20, x->{
				getPlayer().closeInventory();
				CoOwnerAddGUI coowneraddgui = new CoOwnerAddGUI(getPlayer(), ConfigManager.getString("GUIS.AddCoOwner.GUIName", "{ADDRESS}", GATE.getAddress()), GATE);
				
				coowneraddgui.setNextPage(createItem(ConfigManager.getString("GUIS.AddCoOwner.Items.NextPage"), null, Material.PAPER));
				coowneraddgui.setPrevPage(createItem(ConfigManager.getString("GUIS.AddCoOwner.Items.PreviousPage"), null, Material.PAPER));
				coowneraddgui.addControleItem(0, createItem(ConfigManager.getString("GUIS.AddCoOwner.Items.BackToEditGUI"), null, Material.HOPPER));
				coowneraddgui.addControleItemFunctions(0, f->{
					getPlayer().closeInventory();
					EditGateGUI editGateGUI = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
					if(editGateGUI.OpenGUI()) InventoryManager.addGUI(editGateGUI);
				});
				
				coowneraddgui.addControleItem(8, createItem(ConfigManager.getString("GUIS.AddCoOwner.Items.Info.Name"), null, Material.DARK_OAK_SIGN));
				coowneraddgui.addControleItemFunctions(8, f->getPlayer().sendMessage(ConfigManager.getString("GUIS.AddCoOwner.Items.Info.Text")));
				
				if(coowneraddgui.OpenGUI()) InventoryManager.addGUI(coowneraddgui);
			}, Material.PLAYER_HEAD);
			
			addGUIFunction(19, x->{
				getPlayer().closeInventory();
				CoOwnerListGUI coownerlistgui = new CoOwnerListGUI(getPlayer(), ConfigManager.getString("GUIS.RemoveCoOwner.GUIName", "{ADDRESS}", GATE.getAddress()), GATE, false);
				
				coownerlistgui.setNextPage(createItem(ConfigManager.getString("GUIS.RemoveCoOwner.Items.NextPage"), null, Material.PAPER));
				coownerlistgui.setPrevPage(createItem(ConfigManager.getString("GUIS.RemoveCoOwner.Items.PreviousPage"), null, Material.PAPER));
				coownerlistgui.addControleItem(0, createItem(ConfigManager.getString("GUIS.RemoveCoOwner.Items.BackToEditGUI"), null, Material.HOPPER));
				coownerlistgui.addControleItemFunctions(0, f->{
					getPlayer().closeInventory();
					EditGateGUI editGateGUI = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
					if(editGateGUI.OpenGUI()) InventoryManager.addGUI(editGateGUI);
				});

				coownerlistgui.addControleItem(8, createItem(ConfigManager.getString("GUIS.RemoveCoOwner.Items.Info.Name"), null, Material.DARK_OAK_SIGN));
				coownerlistgui.addControleItemFunctions(8, f->getPlayer().sendMessage(ConfigManager.getString("GUIS.RemoveCoOwner.Items.Info.Text")));
				
				coownerlistgui.setGeneralFunction(f ->{
					if(f.index < 9*5) {
						String name = f.item.getItemMeta().getDisplayName();
						if(name != null || name != "") {
							for(String k : GATE.getCoOwner().keySet()) {
								if(GATE.getCoOwner().get(k).equals(name)) {
									Player newCoOwner = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(k));
									if(newCoOwner != null) newCoOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.RemoveCoOwner.Message1", "{ADDRESS}", GATE.getAddress()));
									
									GATE.removeCoOwner(k);
									TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(k, GATE.getAddress());
									getPlayer().closeInventory();
									getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.RemoveCoOwner.Message2", "{PLAYER_NAME}", name));
									EditGateGUI gui = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
									if(gui.OpenGUI()) InventoryManager.addGUI(gui); 
									return;
								}
							}
						}
					}
				});
				if(coownerlistgui.OpenGUI()) InventoryManager.addGUI(coownerlistgui);
			}, Material.PLAYER_HEAD);
		}
		
		if(GATE.isOwner(getPlayer()) || (getPlayer().hasPermission(Perms.thegate_admin_editowner.value()) && TheGateMain.theGateMain.getServer().getPlayer(GATE.getOwnerUUID()) != null)) {
			addGUIFunction(18, x->{
				getPlayer().closeInventory();
				CoOwnerListGUI coownerlistgui = new CoOwnerListGUI(getPlayer(), ConfigManager.getString("GUIS.SetGateOwner.GUIName", "{ADDRESS}", GATE.getAddress()), GATE, true);
				
				coownerlistgui.setNextPage(createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.NextPage"), null, Material.PAPER));
				coownerlistgui.setPrevPage(createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.PreviousPage"), null, Material.PAPER));
				coownerlistgui.addControleItem(0, createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.BackToEditGUI"), null, Material.HOPPER));
				coownerlistgui.addControleItemFunctions(0, f->{
					getPlayer().closeInventory();
					EditGateGUI editGateGUI = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
					if(editGateGUI.OpenGUI()) InventoryManager.addGUI(editGateGUI);
				});

				coownerlistgui.addControleItem(8, createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.Info.Name"), null, Material.DARK_OAK_SIGN));
				coownerlistgui.addControleItemFunctions(8, f->getPlayer().sendMessage(ConfigManager.getString("GUIS.SetGateOwner.Items.Info.Text")));
				
				coownerlistgui.setGeneralFunction(f ->{
					if(f.index < 9*5) {
						String name = f.item.getItemMeta().getDisplayName();
						if(name != null || name != "") {
							if(name.equals(Globals.dummyOwnerName)) {
								Player prevOwner = TheGateMain.theGateMain.getServer().getPlayer(GATE.getOwnerUUID());
								GATE.setOwnerUUID("null");
								GATE.setOwnerName(name);
								GATE.Repair();
								GATE.setUpdated(true);
								TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(getPlayer().getUniqueId().toString(), GATE.getAddress());
								getPlayer().closeInventory();
								if(prevOwner != null) prevOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message2", "{ADDRESS}", GATE.getAddress()));
								EditGateGUI gui = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
								if(gui.OpenGUI()) InventoryManager.addGUI(gui);
								return;
							}
							for(String k : GATE.getCoOwner().keySet()) {
								if(GATE.getCoOwner().get(k).equals(name)) {
									Player newOwner = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(k));
									Player prevOwner = null;
									if(!GATE.getOwnerUUID().equals("null")) {
										prevOwner = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(GATE.getOwnerUUID()));
									}
									boolean CanOverrideOwnership = false;
									if(newOwner != null) {
										if(newOwner.hasPermission(Perms.thegate_admin_creategate.value())) {
											CanOverrideOwnership = true;
										}else if(newOwner.hasPermission(Perms.thegate_admin_creategate.value()) && GateManager.getPlayerGateAmmount(newOwner) < Globals.PlayerGateAmmount) {
											CanOverrideOwnership = true;
										}
									}else if(GateManager.getPlayerGateAmmount(k) < Globals.PlayerGateAmmount){
										CanOverrideOwnership = true;
									}
									
									if(CanOverrideOwnership) {
										if(newOwner != null) newOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message1", "{ADDRESS}", GATE.getAddress()));
										if(prevOwner != null) GATE.addCoOwner(GATE.getOwnerUUID(), GATE.getOwnerName());
										if(prevOwner != null) TheGateMain.SaveLoadInterface.AddPlayerToCoowner(GATE.getOwnerUUID(), GATE.getOwnerName(), GATE.getAddress());
										GATE.setOwnerUUID(k);
										GATE.setOwnerName(name);
										GATE.removeCoOwner(k);
										GATE.Repair();
										GATE.setUpdated(true);
										TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(getPlayer().getUniqueId().toString(), GATE.getAddress());
										getPlayer().closeInventory();
										if(prevOwner != null) prevOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message2", "{ADDRESS}", GATE.getAddress()));
										EditGateGUI gui = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
										if(gui.OpenGUI()) InventoryManager.addGUI(gui);
									}else {
										getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message3"));
									}
									return;
								}
							}
						}
					}
				});
				if(coownerlistgui.OpenGUI()) InventoryManager.addGUI(coownerlistgui);
			}, Material.PLAYER_HEAD);
		}
		
		if(!GATE.isOwner(getPlayer()) && getPlayer().hasPermission(Perms.thegate_admin_editcoowner.value()) && TheGateMain.theGateMain.getServer().getPlayer(GATE.getOwnerUUID()) == null) {
			addGUIFunction(18, x->{
				getPlayer().closeInventory();
				CoOwnerListGUI coownerlistgui = new CoOwnerListGUI(getPlayer(), ConfigManager.getString("GUIS.SetGateOwner.GUIName", "{ADDRESS}", GATE.getAddress()), GATE, true);
				
				coownerlistgui.setNextPage(createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.NextPage"), null, Material.PAPER));
				coownerlistgui.setPrevPage(createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.PreviousPage"), null, Material.PAPER));
				coownerlistgui.addControleItem(0, createItem(ConfigManager.getString("GUIS.SetGateOwner.Items.BackToEditGUI"), null, Material.HOPPER));
				coownerlistgui.addControleItemFunctions(0, f->{
					getPlayer().closeInventory();
					EditGateGUI editGateGUI = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
					if(editGateGUI.OpenGUI()) InventoryManager.addGUI(editGateGUI);
				});
				
				coownerlistgui.setGeneralFunction(f ->{
					if(f.index < 9*5) {
						String name = f.item.getItemMeta().getDisplayName();
						if(name != null || name != "") {
							if(name.equals(Globals.dummyOwnerName)) {
								Player prevOwner = TheGateMain.theGateMain.getServer().getPlayer(GATE.getOwnerUUID());
								GATE.setOwnerUUID("null");
								GATE.setOwnerName(name);
								GATE.Repair();
								GATE.setUpdated(true);
								TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(getPlayer().getUniqueId().toString(), GATE.getAddress());
								getPlayer().closeInventory();
								if(prevOwner != null) prevOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message2", "{ADDRESS}", GATE.getAddress()));
								EditGateGUI gui = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
								if(gui.OpenGUI()) InventoryManager.addGUI(gui);
								return;
							}
							for(String k : GATE.getCoOwner().keySet()) {
								if(GATE.getCoOwner().get(k).equals(name)) {
									Player newOwner = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(k));
									Player prevOwner = null;
									if(!GATE.getOwnerUUID().equals("null")) {
										prevOwner = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(GATE.getOwnerUUID()));
									}
									boolean CanOverrideOwnership = false;
									if(newOwner != null) {
										if(newOwner.hasPermission(Perms.thegate_admin_creategate.value())) {
											CanOverrideOwnership = true;
										}else if(newOwner.hasPermission(Perms.thegate_owner_creategate.value()) && GateManager.getPlayerGateAmmount(newOwner) < Globals.PlayerGateAmmount) {
											CanOverrideOwnership = true;
										}
									}else if(GateManager.getPlayerGateAmmount(k) < Globals.PlayerGateAmmount){
										CanOverrideOwnership = true;
									}
									
									if(CanOverrideOwnership) {
										if(newOwner != null) newOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message1", "{ADDRESS}", GATE.getAddress()));
										if(prevOwner != null) GATE.addCoOwner(GATE.getOwnerUUID(), GATE.getOwnerName());
										if(prevOwner != null) TheGateMain.SaveLoadInterface.AddPlayerToCoowner(GATE.getOwnerUUID(), GATE.getOwnerName(), GATE.getAddress());
										GATE.setOwnerUUID(k);
										GATE.setOwnerName(name);
										GATE.removeCoOwner(k);
										GATE.Repair();
										TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(getPlayer().getUniqueId().toString(), GATE.getAddress());
										getPlayer().closeInventory();
										if(prevOwner != null) prevOwner.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message2", "{ADDRESS}", GATE.getAddress()));
										EditGateGUI gui = new EditGateGUI(getPlayer(), ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", GATE.getAddress()), GATE.getAddress(), GATE);
										if(gui.OpenGUI()) InventoryManager.addGUI(gui);
									}else {
										getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.SetOwner.Message3"));
									}
									return;
								}
							}
						}
					}
				});
				if(coownerlistgui.OpenGUI()) InventoryManager.addGUI(coownerlistgui);
			}, Material.SKELETON_SKULL);
		}
		//########
		
		addGUIFunction(24, x-> {
			getPlayer().closeInventory();
			String netPrio = ConfigManager.getString("GUIS.NetworkAssignGUI.Primary");
			NetworkAssignGUI nag = new NetworkAssignGUI(getPlayer(), ConfigManager.getString("GUIS.NetworkAssignGUI.GUIName", "{NETNAME}", netPrio), netPrio, GATE, this);
			if(nag.OpenGUI()) InventoryManager.addGUI(nag);
		}, Material.END_CRYSTAL);
		
		addGUIFunction(25, x->{
			getPlayer().closeInventory();
			String netPrio = ConfigManager.getString("GUIS.NetworkAssignGUI.Secondary");
			NetworkAssignGUI nag = new NetworkAssignGUI(getPlayer(), ConfigManager.getString("GUIS.NetworkAssignGUI.GUIName", "{NETNAME}", netPrio), netPrio, GATE, this);
			if(nag.OpenGUI()) InventoryManager.addGUI(nag);
		}, Material.END_CRYSTAL);
		
		addGUIFunction(25, x->{
			getPlayer().closeInventory();
			String netPrio = ConfigManager.getString("GUIS.NetworkAssignGUI.Secondary");
			NetworkAssignGUI nag = new NetworkAssignGUI(getPlayer(), ConfigManager.getString("GUIS.NetworkAssignGUI.GUIName", "{NETNAME}", netPrio), netPrio, GATE, this);
			if(nag.OpenGUI()) InventoryManager.addGUI(nag);
		}, Material.WHITE_STAINED_GLASS_PANE);
		
		addGUIFunction(13, x->{
			Location DiledLocation = GATE.getGate();
			Vector v = GateMath.getRotY(new Vector(0,0,2), Math.toRadians((GATE.getFacing() * 90f)));
			Location tpLoc = new Location(DiledLocation.getWorld(), DiledLocation.getBlockX() + 0.5 + v.getX(),
					DiledLocation.getBlockY() + 1, DiledLocation.getBlockZ() + 0.5 + v.getZ());
			tpLoc.setYaw(GATE.getFacing() * 90);
			getPlayer().teleport(tpLoc);
			getPlayer().closeInventory();
		}, Material.ELYTRA);
		
		if((getPlayer().hasPermission(Perms.thegate_owner_editmaterial.value()) && GATE.isOwner(getPlayer())) ||
				(getPlayer().hasPermission(Perms.thegate_owner_editmaterial.value()) && isCoowner) ||
				getPlayer().hasPermission(Perms.thegate_admin_editmaterial.value())) {
			addGUIFunction(28, x->{
				GATE.setRingMaterial(Globals.DefaultringMaterial);
				GATE.setChevrons_frameMaterial(Globals.Defaultchevrons_frameMaterial);
				GATE.setChevron_botMaterial(Globals.Defaultchevron_botMaterial);
				GATE.setChevron_lightMaterial(Globals.Defaultchevron_lightMaterial);
				GATE.setChevron_lightMaterial_on(Globals.Defaultchevron_lightMaterial_ON);
				GATE.setHorizonMaterial(Globals.DefaulthorizonMaterial);
				GATE.Refresh();
				getPlayer().closeInventory();
				GATE.setUpdated(true);
			}, Material.ENDER_PEARL);
			
			setGeneralFunction(x->{
				if (x.index > 28 && x.index < 35) {
					if(!x.event.getView().getCursor().getType().equals(Material.AIR)) {
						ItemStack i = getItem(x.index);
						i.setType(x.event.getView().getCursor().getType());
						setItem(x.index, i);
						refreshItems();
					}
				}
			});
			
			addGUIFunction(35, x->{
				GATE.setRingMaterial(getItem(29).getType());
				GATE.setChevron_botMaterial(getItem(30).getType());
				GATE.setChevrons_frameMaterial(getItem(31).getType());
				GATE.setChevron_lightMaterial(getItem(32).getType());
				GATE.setChevron_lightMaterial_on(getItem(33).getType());
				GATE.setHorizonMaterial(getItem(34).getType());
				GATE.Refresh();
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditGateGUI.Message9", "{ADDRESS}", GATE.getAddress()));
				GATE.setUpdated(true);
			}, Material.REDSTONE_TORCH);
			
		}
		
		addGUIFunction(22, x -> {
			x.player.closeInventory();
			IrisEditGUI irisEditGUI = new IrisEditGUI(x.player, ConfigManager.getString("GUIS.IrisEditGUI.GUIName"), "IrisEditGUI", GATE, this);
			if(irisEditGUI.OpenGUI()) InventoryManager.addGUI(irisEditGUI);
		}, Material.FIRE_CHARGE);
		
	}
	

}
