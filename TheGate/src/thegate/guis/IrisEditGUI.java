package thegate.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gui.tools.guitools.GUIBase;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.GateObject;
import thegate.main.ConfigManager;
import thegate.main.Perms;

public class IrisEditGUI extends GUIBase {

	private GateObject gate;
	
	private String newCode = "";
	private int index = 1;
	
	private EditGateGUI ui;
	
	public IrisEditGUI(Player p, String name, String Tag, GateObject gate, EditGateGUI ui) {
		super(p, 9*5, name, Tag);
		this.gate = gate;
		this.ui = ui;
		setup();
		setupFunctions();
	}
	
	public void setup() {
		this.addUIAccessPermission(Perms.thegate_owner_editgate.value(), Perms.thegate_admin_editgate.value());
		for(int i = 9; i < 9*5; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}
		setItem(0, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(8, Material.RED_STAINED_GLASS_PANE, " ", null);
		
		setItem(14, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(23, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(32, Material.RED_STAINED_GLASS_PANE, " ", null);
		setItem(41, Material.RED_STAINED_GLASS_PANE, " ", null);
		
		
		if(((gate.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) ||
				gate.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString())) && getPlayer().hasPermission(Perms.thegate_owner_editidccode.value())) || getPlayer().hasPermission(Perms.thegate_admin_editidccode.value())) {
			setItem(17, Material.REDSTONE_TORCH, ConfigManager.getString("GUIS.IrisEditGUI.Items.SetNewIDC"), null);
			
			setItem(10, Material.MUSIC_DISC_WAIT, "1", null);
			setItem(11, Material.MUSIC_DISC_13, "2", null);
			setItem(12, Material.MUSIC_DISC_BLOCKS, "3", null);
			setItem(19, Material.MUSIC_DISC_CAT, "4", null);
			setItem(20, Material.MUSIC_DISC_CHIRP, "5", null);
			setItem(21, Material.MUSIC_DISC_FAR, "6", null);
			setItem(28, Material.MUSIC_DISC_MALL, "7", null);
			setItem(29, Material.MUSIC_DISC_MELLOHI, "8", null);
			setItem(30, Material.MUSIC_DISC_STAL, "9", null);
			setItem(38, Material.MUSIC_DISC_STRAD, "0", null);
			
			setItem(15, Material.OAK_SIGN, ConfigManager.getString("GUIS.IrisEditGUI.Items.CurrentIDC", "{IDC}", gate.getIrisCode()), null);

			setItem(40, Material.BARRIER, ConfigManager.getString("GUIS.IrisEditGUI.Items.Clear"), null);
		}
		
		setItem(36, Material.HOPPER, ConfigManager.getString("GUIS.IrisEditGUI.Items.Back"), null);
		
		
		if(((gate.getOwnerUUID().equals(getPlayer().getUniqueId().toString()) ||
				gate.getCoOwner().keySet().contains(getPlayer().getUniqueId().toString())) && getPlayer().hasPermission(Perms.thegate_owner_editirismaterial.value())) || getPlayer().hasPermission(Perms.thegate_admin_editirismaterial.value())) {
			setItem(35, Material.REDSTONE_TORCH, ConfigManager.getString("GUIS.IrisEditGUI.Items.SetIrisMaterial"), null);
			setItem(33, gate.getIrisMaterial(), ConfigManager.getString("GUIS.IrisEditGUI.Items.IrisMaterial"), null);
		}
		
		setItem(16, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(24, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(25, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(26, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(34, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(42, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(43, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		setItem(44, Material.WHITE_STAINED_GLASS_PANE, " ", null);
		
		
	}
	
	public void setupFunctions() {
		
		addGUIFunction(36, x->{
			x.player.closeInventory();
			if(ui.OpenGUI()) InventoryManager.addGUI(ui);
		}, Material.HOPPER);
		
		addGUIFunction(40, x->{
			index = 1;
			newCode = "";
			for(int i = 1; i < 8; i++) {
				setItem(i, new ItemStack(Material.AIR));
			}
			refreshItems();
		}, Material.BARRIER);
		
		addGUIFunction(10, x ->{
			if(index < 8) {
				newCode += 1;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_WAIT);
		
		addGUIFunction(11, x ->{
			if(index < 8) {
				newCode += 2;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_13);
		
		addGUIFunction(12, x ->{
			if(index < 8) {
				newCode += 3;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_BLOCKS);
		
		addGUIFunction(19, x ->{
			if(index < 8) {
				newCode += 4;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_CAT);
		
		addGUIFunction(20, x ->{
			if(index < 8) {
				newCode += 5;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_CHIRP);
		
		addGUIFunction(21, x ->{
			if(index < 8) {
				newCode += 6;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_FAR);
		
		addGUIFunction(28, x ->{
			if(index < 8) {
				newCode += 7;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_MALL);
		
		addGUIFunction(29, x ->{
			if(index < 8) {
				newCode += 8;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_MELLOHI);
		
		addGUIFunction(30, x ->{
			if(index < 8) {
				newCode += 9;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_STAL);
		
		addGUIFunction(38, x ->{
			if(index < 8) {
				newCode += 0;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_STRAD);
		
		addGUIFunction(17, x ->{
			if(index == 8 && newCode.length() == 7) {
				gate.setIrisCode(newCode);
				setItem(15, Material.OAK_SIGN, ConfigManager.getString("GUIS.IrisEditGUI.Items.CurrentIDC", "{IDC}", gate.getIrisCode()), null);
				refreshItems();
				x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.IrisEditGUI.Message1"));
				gate.setUpdated(true);
			}else {
				x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.IrisEditGUI.Message2"));
			}
		}, Material.REDSTONE_TORCH);
		
		addGUIFunction(35, x ->{
			if(!x.base.getItem(33).getType().equals(gate.getIrisMaterial())) {
				gate.setIrisMaterial(x.base.getItem(33).getType());
				x.player.sendMessage(ConfigManager.getString("PlayerMessages.FromGUI.IrisEditGUI.Message3"));
				gate.setUpdated(true);
				gate.Repair();
			}
		}, Material.REDSTONE_TORCH);
		
		setGeneralFunction(x ->{
			if(x.index == 33) {
				if(!x.event.getView().getCursor().getType().equals(Material.AIR)) {
					ItemStack i = getItem(x.index);
					i.setType(x.event.getView().getCursor().getType());
					setItem(x.index, i);
					refreshItems();
				}
			}
		});
		
	}

}
