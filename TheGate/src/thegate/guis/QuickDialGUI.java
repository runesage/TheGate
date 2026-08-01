package thegate.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gui.tools.guitools.GUIPages;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.BlockedState;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;

public class QuickDialGUI extends GUIPages {
	
	GateObject gate;

	ArrayList<GateObject> userGates = new ArrayList<GateObject>();
	
	private DHD_GUI ui;
	
	public QuickDialGUI(Player p, String name, GateObject gate, DHD_GUI ui) {
		super(p, 9 * 6, name, "QuickDialGUI");
		this.gate = gate;
		this.ui = ui;
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		addUIAccessPermission(Perms.thegate_user_quickdial.value());
		setDefaultErrorMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
	}
	
	public void setup() {
		Set<GateObject> gates = GateManager.getGatesAsSet();
		if (Globals.UseBungee) gates.addAll(GateManager.getGatesOnOtherServer());
		List<GateObject> sortedList = gates.stream().sorted((x,y) -> {
			return x.getAddress().compareTo(y.getAddress());
		}).collect(Collectors.toList());
		List<GateObject> owned, accessable;
		
		owned = sortedList.stream().filter(x ->{
			if ((x.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) || x.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString())) && !x.getAddress().equals(gate.getAddress())) {
				if ((gate.getNetwork().equals(x.getNetwork()))
						|| (gate.getNetwork().equals(x.getSecondaryNetwork())
								|| (gate.getSecondaryNetwork().equals(x.getNetwork())))) {
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}).collect(Collectors.toList());
		
		accessable = sortedList.stream().filter(x ->{
			if (!x.getAddress().equals(gate.getAddress()) && (getPlayer().hasPermission(Perms.thegate_user_visablenetwork_.value() + x.getNetwork()) || (x.getSecondaryNetwork().equals("null") ? false : getPlayer().hasPermission(Perms.thegate_user_visablenetwork_.value() + x.getSecondaryNetwork())))
					&& (x.isOpen() || getPlayer().hasPermission(Perms.thegate_admin_dialgate.value())) && !x.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) && !x.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString())) {
				if ((gate.getNetwork().equals(x.getNetwork()))
						|| (gate.getNetwork().equals(x.getSecondaryNetwork())
								|| (gate.getSecondaryNetwork().equals(x.getNetwork())))) {
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}).collect(Collectors.toList());
		
		userGates.addAll(owned);
		userGates.addAll(accessable);

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < userGates.size(); i++) {
			GateObject GATE = userGates.get(i);
			String GATENAME = GATE.getGateName();
			String WORLD = GATE.getWorldName();
			String LocX = GATE.getGate().getX() + "";
			String LocY = GATE.getGate().getY() + "";
			String LocZ = GATE.getGate().getZ() + "";
			String NET = GATE.getNetwork();
			String OWNER = GATE.getOwnerName();
			String DESCRIPTION = GATE.getDescription();
			List<String> lore =	ConfigManager.getStringList("GUIS.QuickDialGUI.Items.GateObjects.Lore",
							"{GATENAME}", GATENAME, "{WORLD}", WORLD,
							"{X}", LocX, "{Y}", LocY, "{Z}", LocZ,
							"{NETWORK}", NET, "{OWNER}", OWNER,
							"{DESCRIPTION}", DESCRIPTION);

			items.add(createItem(GATE.getAddress(), lore, (Globals.UseBungee && !GATE.getServer().equals(Globals.ServerName) ? Material.ENDER_PEARL : Material.HEART_OF_THE_SEA)));
		}
		this.setSorceList(items);
		
		this.setNextPage(createItem(ConfigManager.getString("GUIS.QuickDialGUI.Items.NextPage"), null, Material.PAPER));
		this.setPrevPage(createItem(ConfigManager.getString("GUIS.QuickDialGUI.Items.PreviousPage"), null, Material.PAPER));
		this.addControleItem(0, this.createItem(ConfigManager.getString("GUIS.QuickDialGUI.Items.Back"), null, Material.HOPPER));
	}
	
	public void setupFunctions() {
		
		this.addControleItemFunctions(0, x->{
			x.player.closeInventory();
			if(ui.OpenGUI()) InventoryManager.addGUI(ui);
		});
		
		setGeneralFunction(x -> {
			if(x.item == null || x.event.getRawSlot() >= 45 || x.item.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) return;
			String Address = x.item.getItemMeta().getDisplayName();
			GateObject destinationGate = GateManager.getGateWithAddress(Address);
			
			if(gate.isDialinginProssed() || gate.isDialingout()) {
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message3"));
				return;
			}
			
			if(gate.isUseGatePerms()) {
				if(!getPlayer().hasPermission(Perms.thegate_user_dial_.value() + gate.getAddress()) &&
						!getPlayer().hasPermission(Perms.thegate_admin_dialgate.value())) {
					getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
					return;
				}
			}
			
			if (!Globals.UseBungee && destinationGate == null) {
				getPlayer().sendMessage(
						ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message1", "{ADDRESS}", Address));
				getPlayer().closeInventory();
				return;
			}

			if (!Globals.UseBungee && !destinationGate.isOpen() && !destinationGate.getOwnerUUID().equals(getPlayer().getUniqueId().toString())) {
				getPlayer().sendMessage(
						ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message2")
								);
				getPlayer().closeInventory();
				return;
			}
			
			if(Globals.UseBungee && destinationGate == null && GateManager.hasGateOnOtherServerWithAddress(Address) && !gate.isActive() && !gate.isDialinginProssed()) {
				bungeeDial(destinationGate, this.gate, Address);
				return;
			}

			if(destinationGate.isUseGatePerms()) {
				if(!getPlayer().hasPermission(Perms.thegate_user_dial_.value() + destinationGate.getAddress()) &&
						!getPlayer().hasPermission(Perms.thegate_admin_dialgate.value())) {
					getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
					return;
				}
			}
			
			if ((gate.getNetwork().equals(destinationGate.getNetwork()))
					|| (gate.getNetwork().equals(destinationGate.getSecondaryNetwork())
							|| (gate.getSecondaryNetwork().equals(destinationGate.getNetwork())))) {
				
				if (GateManager.hasGateWithAddress(Address)) {
					if ((!destinationGate.isActive() && !gate.isActive()) && !(destinationGate.getBlockedState().equals(BlockedState.block_incoming) ||
							destinationGate.getBlockedState().equals(BlockedState.locked))
							&& !(gate.getBlockedState().equals(BlockedState.block_outgoing) ||
									gate.getBlockedState().equals(BlockedState.locked)) && !destinationGate.isDialinginProssed()
							&& !gate.isDialinginProssed()) {
						destinationGate.Activate(gate.getAddress(), false, this.getPlayer());
						gate.Activate(destinationGate.getAddress(), true, this.getPlayer());
						getPlayer().closeInventory();
					} else {
						getPlayer().sendMessage(
								ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message3"));
						getPlayer().closeInventory();
					}
				}
			} else {
				getPlayer().sendMessage(
						ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message4"));
				getPlayer().closeInventory();
			}
		});
	}
	
	private void bungeeDial(GateObject destinationGate, GateObject gate, String Address) {
		destinationGate = GateManager.getGateOnOtherServerWithAddress(Address);
		
		if (destinationGate == null) {
			getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.QuickDialGUI.Message5", "{ADDRESS}", Address));
			getPlayer().closeInventory();
			return;
		}
		
		if(destinationGate.isUseGatePerms()) {
			if(!getPlayer().hasPermission(Perms.thegate_user_dial_.value() + destinationGate.getAddress()) &&
					!getPlayer().hasPermission(Perms.thegate_admin_dialgate.value())) {
				getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
				return;
			}
		}
		
		if(!((gate.getNetwork().equals(destinationGate.getNetwork()))
				|| (gate.getNetwork().equals(destinationGate.getSecondaryNetwork())
						|| (gate.getSecondaryNetwork().equals(destinationGate.getNetwork()))))) {
			getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message12"));
			getPlayer().closeInventory();
			return;
		}
		if(!((!destinationGate.isActive() && !gate.isActive()) && 
				!(destinationGate.getBlockedState().equals(BlockedState.block_incoming) ||
				destinationGate.getBlockedState().equals(BlockedState.locked))
				&& !(gate.getBlockedState().equals(BlockedState.block_outgoing) ||
						gate.getBlockedState().equals(BlockedState.locked)) && destinationGate != null && !destinationGate.isDialinginProssed()
				&& !gate.isDialinginProssed())) {
			getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message11"));
			getPlayer().closeInventory();
			return;
		}
		if(TheGateMain.SaveLoadInterface.hasGateWithAddressInTableGates(Address) && destinationGate.isOpen()) {
			gate.Activate(Address, true, this.getPlayer());

			getPlayer().closeInventory();
			return;
		}else {
			getPlayer().sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.DHD_GUI.Message11"));
			getPlayer().closeInventory();
			return;
		}
	}

}
