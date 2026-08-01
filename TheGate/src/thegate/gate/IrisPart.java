package thegate.gate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.packageing.tools.packagetools.PackageManager;
import com.packageing.tools.packagetools.entitys.ArmorStand;

import thegate.math.GateMath;

public class IrisPart {

	private double[] headangels = new double[13];
	private ArmorStand[] stands = new ArmorStand[13];
	private Vector[] standsPos = new Vector[13];
	
	private Vector rotationPoint1, rotationPoint2, rotationPoint3;
	private Vector rotationVector = new Vector(1,0,0);
	private Vector ringCenter = new Vector(0,0,0);
	double rotation = 0;
	
	private double rotationYDEG;
	
	private Material irisMaterial = Material.GRAY_STAINED_GLASS_PANE;
	
	double r = 3;
	
	public void setRotation(double angle) {
		Vector backOffset = new Vector(0,0,0.1);
		for(int i = 0; i < 6; i++) {
			if(standsPos[i] == null) continue;
			Vector v = GateMath.getRotZ(standsPos[i], angle);
//			v.multiply(r);
			v.add(rotationPoint1);
			v.add(backOffset);
			v = GateMath.getRotY(v, Math.toRadians(rotationYDEG));
			v.add(ringCenter);
			
			stands[i].setLocation(v.getX(), v.getY(), v.getZ());
			stands[i].setBodyRotation((float) rotationYDEG+180);
			stands[i].setHeadRotation(90,4f,(float) Math.toDegrees(headangels[i] + angle));
		}
		for(int i = 6; i < 9; i++) {
			if(standsPos[i] == null) continue;
			Vector v = GateMath.getRotZ(standsPos[i], angle*1.05);
//			v.multiply(r);
			v.add(rotationPoint2);
			v.add(backOffset);
			v = GateMath.getRotY(v, Math.toRadians(rotationYDEG));
			v.add(ringCenter);
			
			stands[i].setLocation(v.getX(), v.getY(), v.getZ());
			stands[i].setBodyRotation((float) rotationYDEG+180);
			stands[i].setHeadRotation(90,0.1f,(float) Math.toDegrees(headangels[i] + angle*1.05));
			
		}
		for(int i = 9; i < 13; i++) {
			if(standsPos[i] == null) continue;
			Vector v = GateMath.getRotZ(standsPos[i], angle*1.05);
//			v.multiply(r);
			v.add(rotationPoint3);
			v.add(backOffset);
			v = GateMath.getRotY(v, Math.toRadians(rotationYDEG));
			v.add(ringCenter);
			
			stands[i].setLocation(v.getX(), v.getY(), v.getZ());
			stands[i].setBodyRotation((float) rotationYDEG+180);
			stands[i].setHeadRotation(90,0.1f,(float) Math.toDegrees(headangels[i] + angle*1.05));
		}
		
	}
	
	public void calcRelativePosition() {
		for(int i = 0; i < 6; i++) {
			if(standsPos[i] == null) continue;
			Vector v2 = new Vector(standsPos[i].getX()- rotationPoint1.getX(),
					standsPos[i].getY()- rotationPoint1.getY(),
					standsPos[i].getZ()- rotationPoint1.getZ());
			standsPos[i] = v2;
		}
		for(int i = 6; i < 9; i++) {
			if(standsPos[i] == null) continue;
			Vector v2 = new Vector(standsPos[i].getX()- rotationPoint2.getX(),
					standsPos[i].getY()- rotationPoint2.getY(),
					standsPos[i].getZ()- rotationPoint2.getZ());
			standsPos[i] = v2;
		}
		for(int i = 9; i < 13; i++) {
			if(standsPos[i] == null) continue;
			Vector v2 = new Vector(standsPos[i].getX()- rotationPoint3.getX(),
					standsPos[i].getY()- rotationPoint3.getY(),
					standsPos[i].getZ()- rotationPoint3.getZ());
			standsPos[i] = v2;
		}
	}
	
	public void display(Player p) {
		for(ArmorStand s : stands) {
			if(s != null)PackageManager.SendSpawnPackage(s, p);
		}
	}
	
	public void update(Player p) {
		for(ArmorStand s: stands) {
			PackageManager.SendUpdate(s, p);
		}
	}
	
	public void move(Player p) {
		for(ArmorStand s: stands) {
			PackageManager.SendTeleport(s, p);
		}
	}
	
	public void vanish(Player p) {
		for(ArmorStand s: stands) {
			PackageManager.SendDespawnPackage(s, p);
		}
	}
		
	public void setHeadAngle(int i, double a) {
		headangels[i] = a;
	}
	
	public void setStand(int i, ArmorStand s) {
		stands[i] = s;
	}
	
	public void setStandPos(int i, Vector v) {
		standsPos[i] = v;
	}

	public ArmorStand[] getStands() {
		return stands;
	}

	public Vector getRotationVector() {
		return rotationVector;
	}

	public void setRotationVector(Vector rotationVector) {
		this.rotationVector = rotationVector;
	}

	public Vector getRotationPoint1() {
		return rotationPoint1;
	}

	public void setRotationPoint1(Vector rotationPoint1) {
		this.rotationPoint1 = rotationPoint1;
	}

	public Vector getRotationPoint2() {
		return rotationPoint2;
	}

	public void setRotationPoint2(Vector rotationPoint2) {
		this.rotationPoint2 = rotationPoint2;
	}

	public Vector getRotationPoint3() {
		return rotationPoint3;
	}

	public void setRotationPoint3(Vector rotationPoint3) {
		this.rotationPoint3 = rotationPoint3;
	}

	public double getRotationY() {
		return rotationYDEG;
	}

	public void setRotationY(double rotationY) {
		this.rotationYDEG = rotationY;
	}

	public Vector getRingCenter() {
		return ringCenter;
	}

	public void setRingCenter(Vector ringCenter) {
		this.ringCenter = ringCenter;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public Material getIrisMaterial() {
		return irisMaterial;
	}

	public void setIrisMaterial(Material irisMaterial) {
		for(ArmorStand s : this.stands) {
			s.setHeadMaterial(new ItemStack(irisMaterial));
		}
		this.irisMaterial = irisMaterial;
	}
	
}
