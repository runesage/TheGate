package thegate.guis;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gui.tools.guitools.GUIBase;

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;

public class GateCrystalGUI extends GUIBase{
	
	private String Address;
	
	public GateCrystalGUI(Player p, String name, String Address) {
		super(p, 9, name, "GateCrystalGUI");
		this.Address = Address;
		setup();
		CondPerms();
	}
	
	public void CondPerms() {
		setGUIAccessCondition(c ->{
			GateObject o = GateManager.getGateWithAddress(Address);
			if(o != null) {
				return o.getOwnerUUID().equals(getPlayer().getUniqueId().toString());
			}else {
				return false;
			}
		});
	}
	
	public void setup() {
		
		setItem(0, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		setItem(1, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		setItem(8, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		setItem(7, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		setItem(2, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(6, Material.RED_STAINED_GLASS_PANE, " ", null);
		
		ItemStack DHDCrystal = new ItemStack(Globals.DefaultGateCrystal);
		ItemMeta DHDCrystalMeta = DHDCrystal.getItemMeta();
		DHDCrystalMeta.setDisplayName(ConfigManager.getString("GUIS.GateCrystalGUI.Items.DHDCrystal.Name"));
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add(ConfigManager.getString("GUIS.GateCrystalGUI.Items.DHDCrystal.Lore"));
		Lore.add(Address);
		DHDCrystalMeta.setLore(Lore);
		DHDCrystal.setItemMeta(DHDCrystalMeta);
		setItem(4, DHDCrystal);
		
		addNoCancleRange(4);
		
	}

}
