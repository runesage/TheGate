package thegate.guis;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gui.tools.guitools.GUIPages;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;

public class NetworkAssignGUI extends GUIPages{

	public GateObject gate;

	ArrayList<String> Network = new ArrayList<String>();
	
	
	private String NetPrio;
	
	private EditGateGUI ui;
	
	public NetworkAssignGUI(Player p, String name, String NetPrio, GateObject gate, EditGateGUI ui) {
		super(p, 9 * 6, name, "NetworkAssignGUI");
		this.gate = gate;
		this.ui = ui;
		this.NetPrio = NetPrio;
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		setDefaultErrorMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
		addUIAccessPermission(Perms.thegate_owner_editnetwork.value(), Perms.thegate_admin_editgate.value());
	}
	
	public void setup() {
		
		if(NetPrio.equals(ConfigManager.getString("GUIS.NetworkAssignGUI.Secondary"))) {
			Network.add("null");
		}
		
		for (int i = 0; i < Globals.Networks.size(); i++) {
			String network = Globals.Networks.get(i);
			if (getPlayer().hasPermission(Perms.thegate_user_visablenetwork_.value() + network) && !(NetPrio.contains("SecundNetwork") && network.equals(Globals.Networks.get(0)))) {
				Network.add(network);
			}
		}

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (int i = 0; i < Network.size(); i++) {
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(Network.get(i));
			items.add(createItem(ConfigManager.getString("GUIS.NetworkAssignGUI.Items.Network"), lore, Material.END_CRYSTAL));
		}
		
		this.setSorceList(items);
		this.addControleItem(0, this.createItem(ConfigManager.getString("GUIS.NetworkAssignGUI.Items.Back"), null, Material.HOPPER));
		
	}
	
	public void setupFunctions() {
		
		this.addControleItemFunctions(0, x->{
			x.player.closeInventory();
			if(ui.OpenGUI()) InventoryManager.addGUI(ui);
		});
		
		setGeneralFunction(x -> {
			gate.setUpdated(true);
			if(x.index < 45 && x.index > -1) {
				if(x.event.getView().getTitle().contains(ConfigManager.getString("GUIS.NetworkAssignGUI.Primary"))) {
					if(x.item.getItemMeta().getLore().get(0).contains(Globals.Networks.get(0))) {
						gate.setNetwork(x.item.getItemMeta().getLore().get(0));
						gate.Repair();
						x.player.closeInventory();
						EditGateGUI egg = new EditGateGUI(x.player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", gate.getAddress()), gate.getAddress(), gate);
						if(egg.OpenGUI()) InventoryManager.addGUI(egg);
					}else {
						gate.setNetwork(x.item.getItemMeta().getLore().get(0));
						gate.setSecundaryNetwork("null");
						gate.Repair();
						x.player.closeInventory();
						
						EditGateGUI egg = new EditGateGUI(x.player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", gate.getAddress()), gate.getAddress(), gate);
						if(egg.OpenGUI()) InventoryManager.addGUI(egg);
					}
				}else if(x.event.getView().getTitle().contains(ConfigManager.getString("GUIS.NetworkAssignGUI.Secondary"))){
					gate.setSecundaryNetwork(x.item.getItemMeta().getLore().get(0));
					gate.Repair();
					x.player.closeInventory();
					EditGateGUI egg = new EditGateGUI(x.player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{ADDRESS}", gate.getAddress()), gate.getAddress(), gate);
					if(egg.OpenGUI()) InventoryManager.addGUI(egg);
				}
			}
		});
		
	}
	
	public boolean ContaindInNetworkList(String net) {
		for (int i = 0; i < Globals.Networks.size(); i++) {
			if (net.equals(Globals.Networks.get(i))) {
				return true;
			}
		}
		return false;
	}

}
