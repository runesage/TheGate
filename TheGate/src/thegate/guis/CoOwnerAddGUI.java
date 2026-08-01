package thegate.guis;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Sets;
import com.gui.tools.guitools.GUIPages;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Perms;
import thegate.main.TheGateMain;

public class CoOwnerAddGUI extends GUIPages{

	GateObject gate;
	
	public CoOwnerAddGUI(Player p, String name, GateObject gate) {
		super(p, 9*6, name, "CoOwnerAddGUI");
		this.gate = gate;
		setup();
		setupFunctions();
	}
	
	public void Perms() {
		
	}
	
	public void setup() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		Set<Player> onlinePlayer = Sets.newHashSet(TheGateMain.theGateMain.getServer().getOnlinePlayers());
		for(Player p: onlinePlayer) {
			if(!p.equals(getPlayer()) || p.hasPermission(Perms.thegate_admin_editowner.value())) {
				ItemStack item = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setOwningPlayer(p);
				meta.setDisplayName(p.getName());
				item.setItemMeta(meta);
				items.add(item);
			}
		}
		setSorceList(items);
		
	}
	
	public void setupFunctions() {
		setGeneralFunction(x ->{
			if(x.index < 9*5) {
				String name = x.item.getItemMeta().getDisplayName();
				if(name != null || name != "") {
					Player p = TheGateMain.theGateMain.getServer().getPlayer(name);
					if(p != null && !gate.getCoOwner().keySet().contains(p.getUniqueId().toString())) {
						gate.addCoOwner(p.getUniqueId().toString(), p.getName());
						x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddCoOwner.Message2", "{PLAYER_NAME}", p.getName()));
						p.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddCoOwner.Message1", "{ADDRESS}", gate.getAddress()));
						TheGateMain.SaveLoadInterface.AddPlayerToCoowner(p.getUniqueId().toString(), p.getName(), gate.getAddress());
						x.player.closeInventory();
						EditGateGUI gui = new EditGateGUI(x.player, ConfigManager.getString("GUIS.EditGateGUI.GUIName", "{PLAYER_NAME}", p.getName()), gate.getAddress(), gate);
						if(gui.OpenGUI()) InventoryManager.addGUI(gui);
					}
				}
			}
		});
	}

}
