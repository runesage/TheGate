package thegate.animaitons;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.packageing.tools.packagetools.PackageManager;
import com.packageing.tools.packagetools.entitys.ArmorStand;

import thegate.gate.CreateGate;
import thegate.gate.GateChevron;
import thegate.gate.GateObject;
import thegate.main.Globals;
import thegate.main.TheGateMain;
import thegate.math.GateMath;

public class AnimationDialingSingleGate extends BukkitRunnable {

	char[] list = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	ArmorStand[] symbols;
	Vector[] PositionSymbols;
	Vector[] PositionSymbolsAfter;
	float speed = Globals.DialingSpeed;
	ArrayList<GateChevron> chevrons;
	
	Vector up = new Vector(0,1,0);
	Vector middle;
	
	boolean dialingDone = false;
	
	GateObject gate;
	Plugin plug;
	
	private String address = "";
	private int currentChar = 0;
	private int currentIndex = 0;
	private float[] currentHeadAngle = new float[16];
	private boolean stop = false;
	private int time = 0;
	private int till = 0;
	private float angle = 0;
	private float angDone = 0;
	private boolean LockingIn = false;
	private int dir = -1;
	private int waitforrailsound = 0;
	private float prefAng = 0f;
	private Player player;
	
	public AnimationDialingSingleGate(GateObject gate, Plugin p, String Address, Player player){
		this.player = player;
		plug = p;
		symbols = gate.getPackages().getSymbol();
		PositionSymbols = gate.getPackages().getSymbolPositionAfterRotation() != null ? gate.getPackages().getSymbolPositionAfterRotation() : CreateGate.getSymbolVectors(gate);
		PositionSymbolsAfter = PositionSymbols.clone();
		chevrons = gate.getPackages().getChevrons();
		this.gate = gate;
		speed = Globals.DialingSpeed;
		this.address = Address;
		middle = new Vector(gate.getGate().getX() + 0.5,gate.getGate().getY() + 1.5,gate.getGate().getZ() + 0.5);
		
		for(int i = 0; i < 16; i++) {
			currentHeadAngle[i] = 0f;
		}
		currentIndex = getIndex(address.charAt(currentChar));
		this.runTaskTimerAsynchronously(plug, 0, Globals.DialingAnimationTicks);
	}
	
	@Override
	public void run() {
		if(aniStop) {
			Stopping();
			return;
		}
		time++;
		if(stop && time < till) return;
		time = 0;
		if(currentChar > 6) {
			LockAnimation();
			gate.getPackages().setSymbolPositionAfterRotation(PositionSymbolsAfter);
			gate.dialingSingle = null;
			gate.Activate(address, true, this.player);
			this.cancel();
			return;
		} 
		if(angle > 360) {
			angle = 0;
		}
		if((dir < 0 ? (this.prefAng > -currentHeadAngle[currentIndex]) : (this.prefAng < -currentHeadAngle[currentIndex])) && angDone > 40) {
			LockingIn = true;
		}
		
		if(LockingIn) LockAnimation();
		if(LockingIn) return;
		if(!dialingDone) {
			angle += speed * dir;
			angDone += Math.abs(speed);
			RotateSymbols();
			if (waitforrailsound <= 0) {
				Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> 
				this.gate.getGate().getWorld().playSound(this.gate.getGate(), Globals.DefaultSpinningSound, Globals.DefaultSpinningVolume, Globals.DefaultSpinningPitch));
				waitforrailsound = 40;
			}
			waitforrailsound--;
		}
	}
	
	public void RotateSymbols() {
		this.prefAng = -currentHeadAngle[currentIndex];
		for(int i = 0; i < 16; i++) {
			Vector v = GateMath.getRotZ(PositionSymbols[i], Math.toRadians(angle));
			PositionSymbolsAfter[i] = v;
			currentHeadAngle[i] = (float) -Math.toDegrees(getAngle(v));
			symbols[i].setHeadRotation(0, 0, currentHeadAngle[i]);
			v = GateMath.getRotY(v, Math.toRadians(gate.getFacing() * 90));
			symbols[i].setLocation(v.getX() + middle.getX(), v.getY() + middle.getY(), v.getZ() +  middle.getZ());
			symbols[i].setBodyRotation(180 + gate.getFacing() * 90);
			for(Player p : gate.getPlayerInRange()) {
				PackageManager.SendTeleport(symbols[i], p);
			}
		}
	}
	
	public void Stop() {
		aniStop = true;
	}
	
	private boolean aniStop = false;
	private void Stopping() {
		for(GateChevron c : gate.getPackages().getChevrons()) {
			if(c.On) {
				c.On = false;
				c.UpdateLight();
			}
		}
		GateChevron c = chevrons.get(0);
		c.LFrameBotLeft.setLocation(c.getV1().getX(), c.getV1().getY(), c.getV1().getZ());
		c.LFrameBotLeft.setBodyRotation(90 * gate.getFacing());
		c.LFrameBotRight.setLocation(c.getV2().getX(), c.getV2().getY(), c.getV2().getZ());
		c.LFrameBotRight.setBodyRotation(90 * gate.getFacing());
		for(Player p : gate.getPlayerInRange()) {
			PackageManager.SendTeleport(c.LFrameBotLeft, p);
			PackageManager.SendTeleport(c.LFrameBotRight, p);
		}
		gate.getPackages().setSymbolPositionAfterRotation(PositionSymbolsAfter);
		this.cancel();
	}
	
	boolean down = true;
	public void LockAnimation() {
		GateChevron c = chevrons.get(0);
		c.LFrameBotLeft.setLocation(c.LFrameBotLeft.getLocationX(), c.LFrameBotLeft.getLocationY() + (down ? (-0.125) : 0.125), c.LFrameBotLeft.getLocationZ());
		c.LFrameBotLeft.setBodyRotation(90 * gate.getFacing());
		c.LFrameBotRight.setLocation(c.LFrameBotRight.getLocationX(), c.LFrameBotRight.getLocationY() + (down ? (-0.125) : 0.125), c.LFrameBotRight.getLocationZ());
		c.LFrameBotRight.setBodyRotation(90 * gate.getFacing());
		for(Player p : gate.getPlayerInRange()) {
			PackageManager.SendTeleport(c.LFrameBotLeft, p);
			PackageManager.SendTeleport(c.LFrameBotRight, p);
		}
		int currentOn = currentChar+1 > 3 ? (currentChar + 3)%9 : (currentChar+1)%9;
		if(down) {
			if(currentChar < 7) currentChar++;
			stop = true;
			till = 20;
			down = false;
			c.On = true;
			angDone = 0;
			chevrons.get(currentOn).On = true;
			chevrons.get(currentOn).UpdateLight();
			c.UpdateLight();
			Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> 
			plug.getServer().getWorld(gate.getWorldName()).playSound(this.gate.getGate(), Globals.DefaultChevronOpenSound, Globals.DefaultChevronOpenVolume, Globals.DefaultChevronOpenPitch));
		}else {
			if(currentChar < 7) currentIndex = getIndex(address.charAt(currentChar));
			dir *= -1;
			c.On = false;
			LockingIn = false;
			stop = false;
			till = 20;
			down = true;
			Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> 
			plug.getServer().getWorld(gate.getWorldName()).playSound(this.gate.getGate(), Globals.DefaultChevronLockSound, Globals.DefaultChevronLockVolume, Globals.DefaultChevronLockPitch));
			if(currentChar != 7) c.UpdateLight();
		}
		
	}
	
	public int getIndex(char c) {
		for(int i = 0; i < 16; i++) {
			if(list[i] == c) return i;
		}
		return 0;
	}
		
	private double getAngle(Vector v1) {
		double ang = Math.acos(((v1.getX()*up.getX() + v1.getY()*up.getY() + v1.getZ()*up.getZ())/(v1.length() * up.length())));
		if(cross(v1) < 0) ang = 2*Math.PI - ang;
		return ang;
	}
	
	private double cross(Vector v) {
		return (v.getY()*up.getZ() - v.getZ()*up.getY()) + (v.getZ()*up.getX() - v.getX()*up.getZ()) + (v.getX()*up.getY() - v.getY()*up.getX());
	}

}
