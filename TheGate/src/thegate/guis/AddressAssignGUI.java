package thegate.guis;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.gui.tools.guitools.GUIBase;
import com.gui.tools.guitools.InventoryManager;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;
import thegate.math.GateMath;

public class AddressAssignGUI extends GUIBase{

	private Location location;
	
	public AddressAssignGUI(Player p, String name, Location loc, Material m) {
		super(p, 9 * 6, name, "AddressAssignGUI");
		if(m.toString().toLowerCase().contains("slab")) {
			loc.add(new Vector(0,-0.5,0));
		}
		
		this.location = loc;
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		addUIAccessPermission(Perms.thegate_owner_creategate.value(), Perms.thegate_admin_creategate.value());
	}
	
	public void setup() {
		

		for (int i = 9; i < 53; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}

		setItem(0, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(8, Material.RED_STAINED_GLASS_PANE, " ", null);

		setItem(20, Globals.SymbolMaterial[0], "0", null);
		setItem(21, Globals.SymbolMaterial[1], "1", null);
		setItem(22, Globals.SymbolMaterial[2], "2", null);
		setItem(23, Globals.SymbolMaterial[3], "3", null);
		setItem(24, Globals.SymbolMaterial[4], "4", null);
		setItem(29, Globals.SymbolMaterial[5], "5", null);
		setItem(30, Globals.SymbolMaterial[6], "6", null);
		setItem(32, Globals.SymbolMaterial[7], "7", null);
		setItem(33, Globals.SymbolMaterial[8], "8", null);
		setItem(38, Globals.SymbolMaterial[9], "9", null);
		setItem(39, Globals.SymbolMaterial[10], "a", null);
		setItem(40, Globals.SymbolMaterial[11], "b", null);
		setItem(41, Globals.SymbolMaterial[12], "c", null);
		setItem(42, Globals.SymbolMaterial[13], "d", null);
		setItem(48, Globals.SymbolMaterial[14], "e", null);
		setItem(50, Globals.SymbolMaterial[15], "f", null);
		
		setItem(53, Material.BARRIER,  ConfigManager.getString("GUIS.AddressAssignGUI.Items.Address-not-allowed"), null);
		setItem(45, Material.SKELETON_SKULL,  ConfigManager.getString("GUIS.AddressAssignGUI.Items.Clear"), null);
		

		setItem(35, Material.CREEPER_SPAWN_EGG, ConfigManager.getString("GUIS.AddressAssignGUI.Items.Randomize"), null);
	}
	
	String[] list = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public boolean ListHasString(String s) {
		for (int i = 0; i < this.list.length; i++) {
			if (s.equals(this.list[i])) {
				return true;
			}
		}
		return false;
	}
	
	private GateObject CreateNewGate(Player player, String Address, Location loc, String WorldName) {
		GateObject go = new GateObject(loc,WorldName, GateMath.getPlayerDirection(player), Address, player.getUniqueId().toString(), player.getName());
		go.setUpdated(true);
		GateManager.addGate(go);
		
		player.closeInventory();
		
		player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message1"));
		player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message2") + "(" + Globals.DefaultDHDMaterial.toString() + ")");
		player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message3"));
		player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message4"));
		
		TextComponent message0 = new TextComponent(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message5"));
		message0.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/thegate gatename <address> <name>"));
		player.spigot().sendMessage(message0);
		message0 = new TextComponent(ConfigManager.getString("PlayerMessages.FromGUI.AddressAssignGUI.Message6"));
		message0.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/thegate gatedescription <address> <description>"));
		player.spigot().sendMessage(message0);
		
		GateCrystalGUI gateCrystalGUI = new GateCrystalGUI(getPlayer(), ConfigManager.getString("GUIS.GateCrystalGUI.GUIName"), Address);
		if(gateCrystalGUI.OpenGUI()) InventoryManager.addGUI(gateCrystalGUI);
		return go;
	}
	
	public void setupFunctions() {
		setGeneralFunction(x->{
			if (x.event.getRawSlot() > 9 && x.event.getRawSlot() < 54 && ListHasString(x.item.getItemMeta().getDisplayName())) {
				for (int i = 1; i < 8; i++) {
					if (getItem(i) == null) {
						setItem(i, x.item);
						refreshItems();
						break;
					}
				}
			}
			
			Boolean AllowCreate = false;
			String Address = "";
			for(int i = 1; i < 8; i++) {
				AllowCreate = true;
				if (getItem(i) == null) {
					 AllowCreate = false;
				}else {
					Address = Address + getItem(i).getItemMeta().getDisplayName();
				}
			}
			
			if(AllowCreate && !GateManager.hasGateWithAddress(Address) && !GateManager.hasGateOnOtherServerWithAddress(Address)) {
				setItem(53, Material.END_CRYSTAL, ConfigManager.getString("GUIS.AddressAssignGUI.Items.CreateGate"), null);
				refreshItems();
			}else {
				setItem(53, Material.BARRIER, ConfigManager.getString("GUIS.AddressAssignGUI.Items.Address-not-allowed"), null);
				refreshItems();
			}
		});
		
		addGUIFunction(35, x -> {
			String s = "";
			int itterator = 0;
			while(itterator < 100) {
				s = "";
				int[] indices = new int[7];
				for(int i = 0; i < 7; i++) {
					int index = new Random().nextInt(15);
					s = s + list[index];
					indices[i] = index;
				}
				if(!GateManager.hasGateWithAddress(s) && !GateManager.hasGateOnOtherServerWithAddress(s)) {
					for(int i = 0; i < 7; i++) {
						setItem(1 + i, Globals.SymbolMaterial[indices[i]], list[indices[i]], null);
					}
					break;
				}
				itterator++;
			}
			refreshItems();
		}, Material.CREEPER_SPAWN_EGG);
		
		addGUIFunction(45, x -> {
			for (int i = 1; i < 8; i++) {
				setItem(i, null);
			}
			refreshItems();
		}, Material.SKELETON_SKULL);
		
		addGUIFunction(53, x -> {
				String Address = "";
				for (int i = 1; i < 8; i++) {
					if (x.event.getInventory().getItem(i) == null) {
						return;
					} else {
						Address = Address + x.event.getInventory().getItem(i).getItemMeta().getDisplayName();
					}
				}

				if (Address != "" && !GateManager.hasGateWithAddress(Address) && !GateManager.hasGateOnOtherServerWithAddress(Address)) {
					
					if(Globals.SaveFromat.equalsIgnoreCase("mysql") && TheGateMain.SaveLoadInterface.hasGateWithAddressInTableGates(Address)) {
						x.player.sendMessage(ConfigManager.getString("PlayerMessages.GlobalText.GateAllreadyInDatabase", "{ADDRESS}", Address));
						x.player.closeInventory();
						return;
					}
						
					GateObject obj = CreateNewGate(x.player, Address, location, location.getWorld().getName().toString());
					obj.addPlayerInRange(x.player);
					TheGateMain.SaveLoadInterface.SaveSingleGate(obj);
				}
		}, Material.END_CRYSTAL);
	}

}
