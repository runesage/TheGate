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

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;

public class AbydosGUI extends GUIPages{

	ArrayList<GateObject> userGates = new ArrayList<GateObject>();
	ArrayList<ItemStack> gateItems = new ArrayList<>();
		
	public AbydosGUI(Player p, String name) {
		super(p, 9 * 6, name, "AbydosGUI");
		
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		addUIAccessPermission(Perms.thegate_admin_abydoscartouche.value(), Perms.thegate_user_abydoscartouche.value());
		setDefaultErrorMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
		
		for(int i = 0; i < 45; i++) {
			additemPerms(i, Perms.thegate_owner_editgate.value(), Perms.thegate_admin_editgate.value());
		}
		
	}
	
	public void setup() {
		Set<GateObject> fullSet = GateManager.getGatesAsSet();
		fullSet.addAll(GateManager.getGatesOnOtherServer());

		List<GateObject> sorted = fullSet.stream().sorted((x,y) -> {
			return x.getAddress().compareTo(y.getAddress());
		}).collect(Collectors.toList());

		List<GateObject> owned = new ArrayList<>(), accessable = new ArrayList<>(), rest = new ArrayList<>();
		
		if (getPlayer().hasPermission(Perms.thegate_admin_abydoscartouche.value())) {
			owned = sorted.stream().filter(x -> x.getOwnerUUID().equals(getPlayer().getUniqueId().toString())).collect(Collectors.toList());
			
			accessable = sorted.stream().filter(x -> x.getCoOwner().containsKey(getPlayer().getUniqueId().toString())).collect(Collectors.toList());
			
			rest = sorted.stream().filter(x -> !x.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) &&
					!x.getCoOwner().containsKey(getPlayer().getUniqueId().toString())).collect(Collectors.toList());
			
		} else if (getPlayer().hasPermission(Perms.thegate_user_abydoscartouche.value())) {
			owned = sorted.stream().filter(x -> x.getOwnerUUID().equals(getPlayer().getUniqueId().toString())).collect(Collectors.toList());
			
			accessable = sorted.stream().filter(x -> x.getCoOwner().containsKey(getPlayer().getUniqueId().toString())).collect(Collectors.toList());

			rest = sorted.stream().filter(x -> getPlayer().hasPermission(Perms.thegate_user_visablenetwork_.value() + x.getNetwork()) && x.isOpen() && !x.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) && !x.getCoOwner().containsKey(getPlayer().getUniqueId().toString())).collect(Collectors.toList());
			
		}
		
		userGates.addAll(owned);
		userGates.addAll(accessable);
		userGates.addAll(rest);
		
		for(int i = 0; i < userGates.size(); i++) {
			GateObject GATE = userGates.get(i);
			String GATENAME = GATE.getGateName();
			String WORLD = GATE.getWorldName();
			String LocX = GATE.getGate().getX() + "";
			String LocY = GATE.getGate().getY() + "";
			String LocZ = GATE.getGate().getZ() + "";
			String NET = GATE.getNetwork();
			String OWNER = GATE.getOwnerName();
			String DESCRIPTION = GATE.getDescription();
			List<String> lore = ConfigManager.getStringList("GUIS.AbydosGUI.Items.GateObjects.Lore",
					"{GATENAME}", GATENAME,
					"{WORLD}", WORLD,
					"{X}", LocX,
					"{Y}", LocY,
					"{Z}", LocZ,
					"{NETWORK}", NET, 
					"{OWNER}", OWNER,
					"{DESCRIPTION}", DESCRIPTION);
			gateItems.add(createItem(GATE.getAddress(), lore,Globals.UseBungee && !GATE.getServer().equals(Globals.ServerName) ? Material.ENDER_PEARL : Material.HEART_OF_THE_SEA));
		}
		this.setSorceList(gateItems);

		this.setNextPage(createItem(ConfigManager.getString("GUIS.AbydosGUI.Items.NextPage"), null, Material.PAPER));
		this.setPrevPage(createItem(ConfigManager.getString("GUIS.AbydosGUI.Items.PreviousPage"), null, Material.PAPER));
		
	}
	
	public void setupFunctions() {
		setGeneralFunction(x ->{
			if (x.event.getRawSlot() < 45) {
				if (x.item.getType().equals(Material.HEART_OF_THE_SEA)) {
					if (x.item.hasItemMeta()) {
						String name = x.item.getItemMeta().getDisplayName();
						if (GateManager.hasGateWithAddress(name) && ((GateManager.getGateWithAddress(name).isOwner(x.player) && x.player.hasPermission(Perms.thegate_owner_editgate.value())) ||
								(x.player.hasPermission(Perms.thegate_admin_editgate.value())) ||
								(x.player.hasPermission(Perms.thegate_owner_editgate.value()) && GateManager.getGateWithAddress(name).getCoOwner().get(x.player.getUniqueId().toString()) != null))) {
							x.player.closeInventory();

							EditGateGUI editGateGUI = new EditGateGUI(x.player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", name), name, GateManager.getGateWithAddress(name));
							if(editGateGUI.OpenGUI()) InventoryManager.addGUI(editGateGUI);
						}	
					}
				}
			}
		});
	}

}
