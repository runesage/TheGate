package thegate.guis;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import com.gui.tools.guitools.GUIPages;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import thegate.gate.GateObject;
import thegate.main.Globals;
import thegate.main.TheGateMain;

public class CoOwnerListGUI extends GUIPages{

	private GateObject gate;
	
	private boolean editOwner = false;
	
	public CoOwnerListGUI(Player p, String name, GateObject Gate, boolean editOwner) {
		super(p, 9*6, name, "CoOwnerListGUI");
		this.gate = Gate;
		this.editOwner = editOwner;
		Perms();
		setup();
		setupFunctions();
	}
	
	public void Perms() {
		
	}
	
	public void setup() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(editOwner && Globals.UseDummyOwner) {
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			//OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString("9fa4d6dc-2d30-4554-8597-2dce8130d697"));
			//meta.setOwningPlayer(offlinePlayer);
			meta.setDisplayName(Globals.dummyOwnerName);
			item.setItemMeta(meta);
			items.add(item);
		}
		
		Map<String, String> onlinePlayer = gate.getCoOwner();
		for(String u: onlinePlayer.keySet()) {
			Player p = TheGateMain.theGateMain.getServer().getPlayer(UUID.fromString(u));
			if(p != null) {
				ItemStack item = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setOwningPlayer(p);
				meta.setDisplayName(p.getName());
				item.setItemMeta(meta);
				items.add(item);
			}else {
				ItemStack item = new ItemStack(Material.ENDER_PEARL);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(onlinePlayer.get(u));
				item.setItemMeta(meta);
				items.add(item);
			}
		}
		
		setSorceList(items);
	}
	
	public void setupFunctions() {
		
	}

}
