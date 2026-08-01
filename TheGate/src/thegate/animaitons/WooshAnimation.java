package thegate.animaitons;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class WooshAnimation extends BukkitRunnable{

	
	GateObject gate;
	public int layerDone = 0;
	boolean horizonCreated = false;
	boolean wooshDone = false;
	
	private Map<Integer, Set<ArmorStand>> horizon = null;
	private Map<ArmorStand, Vector> horizonLocations = null;
	private Map<ArmorStand, Vector> horizonHeadPosition = null;
	
	float time = 0f;
	
	private Set<Block> blocks = new HashSet<Block>();
	
	TheGateMain mg;
	public WooshAnimation(GateObject obj, Plugin plugin){
		this.gate = obj;
		this.mg = (TheGateMain) plugin;
		if(obj.getPackages().getHorizon() == null || obj.getPackages().getHorizonLocations() == null || obj.getPackages().getHorizonHeadPosition() == null) {
			CreateGate.CreateEventHorizon(obj);
		}
		
		horizon = obj.getPackages().getHorizon();
		horizonLocations = obj.getPackages().getHorizonLocations();
		horizonHeadPosition = obj.getPackages().getHorizonHeadPosition();
		Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> 
		plugin.getServer().getWorld(gate.getWorldName()).playSound(gate.getGate(), Globals.DefaultGateActivateSound, Globals.DefaultGateActivateVolume, Globals.DefaultGateActivatePitch));

		this.getBlocks();
		
		this.runTaskTimerAsynchronously(plugin, 0, Globals.DialingAnimationTicks);
	}
	
	private void activate() {
		for(Player p : gate.getPlayerInRange()) {
			for(int i = 1; i < 6; i++) {
				for(ArmorStand s : horizon.get(i)) {
					PackageManager.SendSpawnPackage(s, p);
				}
			}
		}
		horizonCreated = true;
		wooshDone = true;
		layerDone = 5;
		gate.setActive(true);
	}
	
	
	int waitforsound = 0;
	int shutdown = Globals.GateTime;
	public int quickShutdown = Globals.GateTime;
	int ShutdownBecauseOfPhysics = 45600;
	int StopTime = 0;
	boolean stop = false;
	int till = 0;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		waitforsound--;
		shutdown--;
		quickShutdown--;
		ShutdownBecauseOfPhysics--;
		StopTime++;
		if(stop && StopTime < till) return;
		StopTime = 0;
		stop = false;
		
		if(shutdown <= 0 && quickShutdown <= 0) {
			gate.Deactivate();
			this.cancel();
		}else if(quickShutdown <= 0) {
			gate.Deactivate();
			this.cancel();
		}else if(ShutdownBecauseOfPhysics <= 0) {
			gate.Deactivate();
			this.cancel();
		}
		
		if(horizonCreated && wooshDone) {
			if(Globals.DoHorizonEffect) WableHorizon();
			return;
		}
		if(!Globals.DoVortex) {
			activate();
			return;
		}
		
		if(layerDone < 5 && Globals.DoVortex) {
			layerDone++;
			for(Player p : gate.getPlayerInRange()) {
				for(ArmorStand s : horizon.get(layerDone)) {
					PackageManager.SendSpawnPackage(s, p);
				}
			}
			stop = true;
			till = 4;
			horizonCreated = (layerDone <= 5);
			return;
		}
		
		if(!wooshDone && horizonCreated && Globals.DoVortex) {
			Wooooosh();
		}
		
		if(waitforsound < 0) {
			Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> 
			gate.getGate().getWorld().playSound(gate.getGate(), Globals.DefaultGateAmbientSound, Globals.DefaultGateAmbientVolume, Globals.DefaultGateAmbientePitch));
			waitforsound = 50;
		}
		
	}
	
	double woooshTime = 0;
	float distance = 0;
	public void Wooooosh() {
		if(woooshTime >= 180) {
			wooshDone = true;
			gate.setActive(true);
			return;
		}
		woooshTime += Globals.VortexSpeed;
		if(gate.isIrisClosed()) {
			return;
		}
		if(Globals.DeadlyVortex) deadlyVortex();
		if(Globals.GateCanBreakBlocks) TheGateMain.theGateMain.getServer().getScheduler().runTask(TheGateMain.theGateMain, () -> destroyBlocks());
		
		for(int i = 1; i < 6; i++) {
			distance = (float) (4f * Math.sin(Math.toRadians(woooshTime)));
			distance = distance * (i/(12.0f-i));
			for(ArmorStand s : horizon.get(i)) {
				Vector v = horizonLocations.get(s);
				Vector v3f = horizonHeadPosition.get(s);
				s.setHeadRotation((float)v3f.getX(), (float)v3f.getY() - (30.0f*distance),(float)v3f.getZ());
				
				s.setLocation(v.getX() + (distance * gate.getFacingVector().getX()),
						v.getY() + (distance * gate.getFacingVector().getY()),
						v.getZ() + (distance * gate.getFacingVector().getZ()));
				s.setBodyRotation(180 + gate.getFacing() * 90);
				for(Player p: gate.getPlayerInRange()) {
					PackageManager.SendTeleport(s, p);
				}
			}
		}
	}
	
	public void deadlyVortex() {
		for(Player p : gate.getPlayerInRange()) {
			if(p.getGameMode().equals(GameMode.SPECTATOR)) continue;
			Location l = p.getLocation();
			double dist = GateMath.DistancePointLine(new Vector(l.getX(), l.getY(), l.getZ()),
					new Vector(gate.getGate().getX() + 0.5, gate.getGate().getY() + 1.5, gate.getGate().getZ() + 0.5), gate.getFacingVector());
			double distPlaneGate = GateMath.DistancePointPlane(new Vector(l.getX(), l.getY(), l.getZ()), new Vector(gate.getGate().getX() + 0.5, gate.getGate().getY() + 1.5, gate.getGate().getZ() + 0.5),
					gate.getFacingVector());
			double distPlaneEnd = GateMath.DistancePointPlane(new Vector(l.getX(), l.getY(), l.getZ()),
					new Vector(gate.getGate().getX() + 0.5 + gate.getFacingVector().getX() * distance,
							gate.getGate().getY() + 1.5 + gate.getFacingVector().getY() * distance,
							gate.getGate().getZ() + 0.5 + gate.getFacingVector().getZ() * distance),
					gate.getFacingVector());
			if(dist < 3 && distPlaneGate > 0 && distPlaneEnd < 0 && (!gate.getJoinedPlayers().contains(p) || !mg.OnCooldown.contains(p.getName()))) {
				Bukkit.getScheduler().runTask(TheGateMain.theGateMain, () -> gate.KillPlayer(p));
			}
		}
		
	}
	
	public void destroyBlocks() {
		double x = gate.getGate().getX() + 0.5;
		double y = gate.getGate().getY() + 3;
		double z = gate.getGate().getZ() + 0.5;
		
		double fx = gate.getFacingVector().getX();
		double fz = gate.getFacingVector().getZ();
		
		double d = distance * Globals.DestructionDistanceMult;
		
		x = x + (fx * d);
		z = z + (fz * d);
				
		for(Block b : blocks) {
			double bx = b.getLocation().getX() + 0.5;
			double by = b.getLocation().getY() + 0.5;
			double bz = b.getLocation().getZ() + 0.5;
			if(!Globals.excludeList.contains(b.getType()) && GateMath.getDistance(bx, by, bz, x, y, z) < Globals.Radius) {
				b.setType(Material.AIR);
			}
		}
		
	}
	
	public void getBlocks() {
		double x = gate.getGate().getX() + 0.5;
		double y = gate.getGate().getY() + 2.5;
		double z = gate.getGate().getZ() + 0.5;
		
		org.bukkit.World w = gate.getGate().getWorld();
		
		Vector or = GateMath.CrossProduct(new Vector(0,1,0), gate.getFacingVector());
		for(int bx = Globals.Selection_X; bx < Globals.SelectionX; bx++) {
			for(int by = Globals.Selection_Y; by < Globals.SelectionY; by++) {
				for(double d = 0; d < Globals.DestructionDistance; d += 0.5) {
					this.blocks.add(getBlockAllongVector(w, new Vector(x + ((or.getX() * bx) * 0.25), y + by, z + ((or.getZ() * bx) * 0.25)), gate.getFacingVector(), d));
				}
			}	
		}
	}
	
	public Block getBlockAllongVector(org.bukkit.World w, Vector point, Vector dir, double k) {
		double x = point.getX() + dir.getX() * k;
		double y = point.getY() + dir.getY() * k;
		double z = point.getZ() + dir.getZ() * k;
//		w.spawnParticle(Particle.FLAME, x, y, z, 0);
		Location l = new Location(w, x, y, z);
		return w.getBlockAt(l);
	}
		
	public void WableHorizon() {
		if(this.horizon.keySet() == null) return;
		time = (time + 5) % 360;
		for(Set<ArmorStand> set : horizon.values()) {
			for(ArmorStand s : set) {
				s.setHeadRotation((float) (5*Math.sin(Math.toRadians(time))),
						(float) (4*Math.cos(Math.toRadians(time))),
						s.getHeadZ());
				for(Player p : gate.getPlayerInRange()) {
					PackageManager.SendUpdate(s, p);
				}
			}
		}
	}
	
	public void Remove() {
		for(Set<ArmorStand> set : horizon.values()) {
			for(ArmorStand s : set) {
				for(Player p : gate.getPlayerInRange()) {
					PackageManager.SendDespawnPackage(s.getEntityID(), p);
				}
			}
		}
		for(GateChevron c : gate.getPackages().getChevrons()) {
			if(c.On) {
				c.On = false;
				c.UpdateLight();
			}
		}
	}

	public boolean isWooshDone() {
		return wooshDone;
	}

}
