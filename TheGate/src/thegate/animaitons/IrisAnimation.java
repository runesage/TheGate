package thegate.animaitons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gui.tools.guitools.GUIBase;
import com.gui.tools.guitools.InventoryManager;

import thegate.gate.GateObject;
import thegate.gate.IrisPart;
import thegate.guis.IrisIDCGUI;
import thegate.main.ConfigManager;
import thegate.main.Globals;
import thegate.main.TheGateMain;

public class IrisAnimation extends BukkitRunnable{

	private boolean open;
	private GateObject gate;
	private double maxTime = 60.0;
	private double dTime = 0.0;
	private double dTimeInc = Globals.IrisSpeed;
	private double angle = 60;
	private double delay = 5;
	public IrisAnimation(GateObject gate, boolean open) {
		this.open = open;
		this.gate = gate;
		if(open) {
			dTime = 60.0;
		}else {
			dTime = 0.0;
		}
		if(!open) {
			for(IrisPart part : gate.getPackages().getIris()) {
				for(Player p : gate.getPlayerInRange()) {
					part.display(p);
				}
			}
		}
		if(!Globals.DoIrisAnimaiton) {
			if(gate.isIrisClosed()) {
				for(IrisPart part : gate.getPackages().getIris()) {
					for(Player p : gate.getPlayerInRange()) {
						part.vanish(p);
					}
				}
			}else {
				for(IrisPart part : gate.getPackages().getIris()) {
					part.setRotation(Math.toRadians(open ? 0 : 60));
					for(Player p : gate.getPlayerInRange()) {
						part.move(p);
					}
				}
			}
			for(GUIBase gui : InventoryManager.getGUIbyTag("IDCGUI")) {
				if(gui instanceof IrisIDCGUI) {
					IrisIDCGUI guiIDC = (IrisIDCGUI) gui;
					if(guiIDC.getConnectedGate().getAddress().equals(gate.getAddress())) {
						guiIDC.setItem(26, Material.GREEN_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.Open"), null);
						guiIDC.refreshItems();
					}
				}
			}
			gate.setIrisClosed(!open);
			return;
		}else {
			this.runTaskTimerAsynchronously(TheGateMain.theGateMain, 5, Globals.IrisAnimaitonTicks);
		}
	}
	
	@Override
	public void run() {
		double change = dTime/maxTime;
		
		if(change > 1 || change < 0) {
			
			if(delay <= 0) {
				gate.setIrisClosed(!open);
				if(!gate.isIrisClosed()) {
					for(IrisPart part : gate.getPackages().getIris()) {
						for(Player p : gate.getPlayerInRange()) {
							part.vanish(p);
						}
					}
				}
				gate.irisAnimaiton = null;
				this.cancel();
				//UPDATE GUIS
				for(GUIBase gui : InventoryManager.getGUIbyTag("IDCGUI")) {
					if(gui instanceof IrisIDCGUI) {
						IrisIDCGUI guiIDC = (IrisIDCGUI) gui;
						if(guiIDC.getConnectedGate().getAddress().equals(gate.getAddress())) {
							guiIDC.setItem(26, Material.GREEN_CONCRETE, ConfigManager.getString("GUIS.IDCTransmitter.Items.Open"), null);
							guiIDC.refreshItems();
						}
					}
				}
			}else {
				delay--;
			}
			
		}else {
			for(IrisPart part : gate.getPackages().getIris()) {
				part.setRotation(Math.toRadians(angle * change));
				for(Player p : gate.getPlayerInRange()) {
					part.move(p);
				}
			}
			
			if(open) {
				this.dTime -= dTimeInc;
			}else {
				this.dTime += dTimeInc;
			}
		}
	}
	
}
