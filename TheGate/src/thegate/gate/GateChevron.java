package thegate.gate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.packageing.tools.packagetools.PackageManager;
import com.packageing.tools.packagetools.entitys.ArmorStand;

public class GateChevron {

	public int Index;
	public ArmorStand LightBot,
	LightTop,
	FrameBot,
	LFrameBotLeft,
	LFrameBotRight,
	LFrameTopLeft,
	LFrameTopRight;
		
	private Vector v1, v2;
	
	public boolean On = false;
	
	private GateObject gate;
	
	public GateChevron(int index, ArmorStand LightBot,
			ArmorStand LightTop,
			ArmorStand FrameBot,
			ArmorStand LFrameBotLeft,
			ArmorStand LFrameBotRight,
			ArmorStand LFrameTopLeft,
			ArmorStand LFrameTopRight, GateObject gate) {
		this.Index = index;
		this.LightBot = LightBot;
		this.LightTop = LightTop;
		this.FrameBot = FrameBot;
		this.LFrameBotLeft = LFrameBotLeft;
		this.LFrameBotRight = LFrameBotRight;
		this.LFrameTopLeft = LFrameTopLeft;
		this.LFrameTopRight = LFrameTopRight;
		this.gate = gate;
	}
	
	public void UpdateLight() {
		if(On) {
			for(Player p : gate.getPlayerInRange()) {
				LightBot.setHeadMaterial(new ItemStack(gate.getChevron_lightMaterial_on()));
				LightTop.setHeadMaterial(new ItemStack(gate.getChevron_lightMaterial_on()));
				PackageManager.SendUpdate(LightBot, p);
				PackageManager.SendUpdate(LightTop, p);
			}
		}else {
			for(Player p : gate.getPlayerInRange()) {
				LightBot.setHeadMaterial(new ItemStack(gate.getChevron_lightMaterial()));
				LightTop.setHeadMaterial(new ItemStack(gate.getChevron_lightMaterial()));
				PackageManager.SendUpdate(LightBot, p);
				PackageManager.SendUpdate(LightTop, p);
			}
		}
	}

	public Vector getV1() {
		return v1;
	}

	public void setV1(Vector v1) {
		this.v1 = v1;
	}

	public Vector getV2() {
		return v2;
	}

	public void setV2(Vector v2) {
		this.v2 = v2;
	}
	
}
