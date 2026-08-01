package thegate.guis;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gui.tools.guitools.GUIBase;

import thegate.gate.CreateGate;
import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;

public class EditAllGatesGUI extends GUIBase{
	
	public EditAllGatesGUI(Player p, String name) {
		super(p, 9 * 2, name, "EditAllGatesGUI");
		setup();
		setupFunctions();
		CondPerms();
	}
	
	public void CondPerms() {
		setDefaultErrorMessage(ConfigManager.getString("PlayerMessages.GlobalText.NoPermission"));
		addUIAccessPermission(Perms.thegate_admin_editallgates.value());
	}
	
	public void setup() {
		for (int i = 0; i < 9 * 2; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}

		setItem(0, Material.WRITABLE_BOOK, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Save"), null);
		setItem(1, Material.BOOK, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Load"),
				null);
		setItem(3, Material.GOLDEN_PICKAXE, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Repair"), null);
		ArrayList<String> RemoveGateLore = new ArrayList<String>();
		RemoveGateLore.add(ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Remove_Armorstands.Lore"));
		setItem(4, Material.LAVA_BUCKET, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Remove_Armorstands.Name"),
				RemoveGateLore);
		ArrayList<String> RemoveAllGateLore = new ArrayList<String>();
		RemoveAllGateLore.add(ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Clear_Gate_List.Lore1"));
		RemoveAllGateLore.add(ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Clear_Gate_List.Lore2"));
		setItem(5, Material.BARRIER, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Clear_Gate_List.Name"), RemoveAllGateLore);
		setItem(7, Material.TNT, ConfigManager.getString("GUIS.EditAllGatesGUI.Items.Cut_all_Gate_connections"), null);
	}
	
	public void setupFunctions() {
		
		addGUIFunction(0, x->{
			TheGateMain.SaveLoadInterface.SaveDataFromSet(GateManager.getGatesAsSet());
			x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message7"));
		}, Material.WRITABLE_BOOK);
		
		addGUIFunction(1, x->{
			TheGateMain.SaveLoadInterface.Load(TheGateMain.theGateMain, x.player);
			TheGateMain.SaveLoadInterface.LoadCoowners(TheGateMain.theGateMain);
			x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message1"));
		}, Material.BOOK);
		
		addGUIFunction(3, x->{
			for (GateObject obj : GateManager.getGatesAsSet()) {
				obj.Repair();
				x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message2", "{ADDRESS}", obj.getAddress()));
			}
		}, Material.GOLDEN_PICKAXE);
		
		addGUIFunction(4, x->{
			for (GateObject obj : GateManager.getGatesAsSet()) {
				obj.Vanish();
				if(Globals.CreateBarrier) CreateGate.RemoveBarrier(obj.getGate(), obj.getFacing());
			}

			x.player.sendMessage(
					ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message3"));
			x.player.sendMessage(
					ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message4"));
		}, Material.LAVA_BUCKET);
		
		addGUIFunction(5, x->{
			for (GateObject obj : GateManager.getGatesAsSet()) {
				obj.Vanish();
				if(Globals.CreateBarrier) CreateGate.RemoveBarrier(obj.getGate(), obj.getFacing());
			}
			GateManager.ClearLists();
			TheGateMain.SaveLoadInterface.clearDatabase();
			x.player.sendMessage(
					ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message6"));
		}, Material.BARRIER);
		
		addGUIFunction(7, x->{
			for (GateObject obj : GateManager.getGatesAsSet()) {
				obj.Deactivate();
			}
			x.player.sendMessage(
					ConfigManager.getString("PlayerMessages.FromGUI.EditAllGatesGUI.Message5"));
		}, Material.TNT);
		
	}

}
