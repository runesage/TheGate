package thegate.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gui.tools.guitools.GUIBase;

import thegate.animaitons.IrisAnimation;
import thegate.gate.GateObject;
import thegate.main.ConfigManager;

public class IrisIDCGUI extends GUIBase{

	private GateObject connectedGate;
	
	private int index = 1;
	private String transmittCode = "";
	
	public IrisIDCGUI(Player p, String name, String Tag, GateObject connectedGate) {
		super(p, 9*5, name, Tag);
		this.connectedGate = connectedGate;
		setup();
		setupFunciton();
	}
	
	public void setup() {
		for(int i = 9; i < 9*5; i++) {
			setItem(i, Material.BLACK_STAINED_GLASS_PANE, " ", null);
		}
		
		setItem(0, Material.RED_STAINED_GLASS_PANE, "", null);
		setItem(8, Material.RED_STAINED_GLASS_PANE, "", null);
		
		setItem(12, Material.MUSIC_DISC_WAIT, "1", null);
		setItem(13, Material.MUSIC_DISC_13, "2", null);
		setItem(14, Material.MUSIC_DISC_BLOCKS, "3", null);
		setItem(21, Material.MUSIC_DISC_CAT, "4", null);
		setItem(22, Material.MUSIC_DISC_CHIRP, "5", null);
		setItem(23, Material.MUSIC_DISC_FAR, "6", null);
		setItem(30, Material.MUSIC_DISC_MALL, "7", null);
		setItem(31, Material.MUSIC_DISC_MELLOHI, "8", null);
		setItem(32, Material.MUSIC_DISC_STAL, "9", null);
		setItem(40, Material.MUSIC_DISC_STRAD, "0", null);
		
		setItem(36, Material.BARRIER, ConfigManager.getString("GUIS.IDCTransmitter.Items.Clear"), null);
		setItem(44, Material.END_CRYSTAL, ConfigManager.getString("GUIS.IDCTransmitter.Items.Transmitt"), null);
		

		if(connectedGate.isIrisClosed()) {
			setItem(26, Material.RED_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.Closed"), null);
		}else if(!connectedGate.isIrisClosed()) {
			setItem(26, Material.GREEN_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.Open"), null);
		}
	}

	public void setupFunciton() {
		addGUIFunction(36, x->{
			index = 1;
			transmittCode = "";
			for(int i = 1; i < 8; i++) {
				setItem(i, new ItemStack(Material.AIR));
			}
			refreshItems();
		}, Material.BARRIER);
		
		addGUIFunction(12, x ->{
			if(index < 8) {
				transmittCode += 1;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_WAIT);
		
		addGUIFunction(13, x ->{
			if(index < 8) {
				transmittCode += 2;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_13);
		
		addGUIFunction(14, x ->{
			if(index < 8) {
				transmittCode += 3;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_BLOCKS);
		
		addGUIFunction(21, x ->{
			if(index < 8) {
				transmittCode += 4;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_CAT);
		
		addGUIFunction(22, x ->{
			if(index < 8) {
				transmittCode += 5;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_CHIRP);
		
		addGUIFunction(23, x ->{
			if(index < 8) {
				transmittCode += 6;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_FAR);
		
		addGUIFunction(30, x ->{
			if(index < 8) {
				transmittCode += 7;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_MALL);
		
		addGUIFunction(31, x ->{
			if(index < 8) {
				transmittCode += 8;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_MELLOHI);
		
		addGUIFunction(32, x ->{
			if(index < 8) {
				transmittCode += 9;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_STAL);
		
		addGUIFunction(40, x ->{
			if(index < 8) {
				transmittCode += 0;
				setItem(index++, x.item);
				refreshItems();
			}
		}, Material.MUSIC_DISC_STRAD);
		
		addGUIFunction(44, x->{
			if(!transmittCode.equals(connectedGate.getIrisCode())) {
				setItem(26, Material.BLACK_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.CodeDenied"), null);
				refreshItems();
				return;
			}
			if(connectedGate.isActive() && connectedGate.isIrisClosed() && connectedGate.irisAnimaiton == null) {
				connectedGate.irisAnimaiton = new IrisAnimation(connectedGate, true);
				setItem(26, Material.ORANGE_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.Opening"), null);
				refreshItems();
			}
		}, Material.END_CRYSTAL);

	}

	public GateObject getConnectedGate() {
		return connectedGate;
	}

	public void setConnectedGate(GateObject connectedGate) {
		this.connectedGate = connectedGate;
	}
	
}
