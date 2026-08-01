package thegate.gate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import thegate.main.Globals;
import thegate.main.Perms;
import thegate.main.TheGateMain;
import thegate.math.GateMath;

public class GateManager{

	private static HashMap<String, GateObject> Gates = new HashMap<String,GateObject>();
	private static HashMap<String, Location> GatesLocation = new HashMap<String, Location>();
	private static HashMap<String, Set<GateObject>> GatesByWorldName = new HashMap<String,Set<GateObject>>();
	
    private static Set<GateObject> GatesOnOtherServer = new HashSet<GateObject>();
		
	public static void addGate(GateObject go) {
		if(go.getServer().equals(Globals.ServerName)) {
			Gates.put(go.getAddress(), go);
			GatesLocation.put(go.getAddress(), go.getGate());
			Set<GateObject> set = GatesByWorldName.get(go.getWorldName());
			if(GatesByWorldName.get(go.getWorldName()) != null) {
				set.add(go);
			}else {
				set = new HashSet<GateObject>();
				GatesByWorldName.put(go.getWorldName(), set);
				set.add(go);
			}
		}else {
			GatesOnOtherServer.add(go);
		}
	}
	
	public static void addGate(List<GateObject> gates) {
		gates.forEach(x -> addGate(x));
	}
	
	public static void RemoveGate(GateObject go) {
		Gates.remove(go.getAddress());
		GatesLocation.remove(go.getAddress());
		if(GatesByWorldName.containsKey(go.getWorldName())) {
			Set<GateObject> gates = GatesByWorldName.get(go.getWorldName());
			gates.remove(go);
			GatesByWorldName.put(go.getWorldName(), gates);
		}
		for(String p : go.getCoOwner().keySet()) {
			TheGateMain.SaveLoadInterface.DeleatePlayerFromCoowner(p, go.getAddress());
		}
	}

	public static void ClearLists() {
		Gates.clear();
		GatesLocation.clear();
		GatesByWorldName.clear();
	}
	
	public static boolean GateInRadius(Location l) {
		Set<GateObject> gates = GatesByWorldName.get(l.getWorld().getName());
		if(gates == null) return false;
		for(GateObject obj : gates) {
			double x = obj.getGate().getX() - l.getX();
			double y = obj.getGate().getY() - l.getY();
			double z = obj.getGate().getZ() - l.getZ();
			
			if(Math.sqrt((x*x) + (y*y) + (z*z)) < Globals.GateExclusionRadius) {
				return true;
			}
		}
		return false;
	}
	
	public static void GateInRadius(Player p, float r) {
		Location l = p.getLocation();

		Set<GateObject> gates = GatesByWorldName.get(l.getWorld().getName());
		if(gates == null) return;
		for(GateObject g : gates) {
			Location loc = g.getGate();
			if(l.getWorld().equals(loc.getWorld())) {
				double x = loc.getX() - l.getX();
				double y = loc.getY() - l.getY();
				double z = loc.getZ() - l.getZ();
				
				if(Math.sqrt((x*x) + (y*y) + (z*z)) < r) {
					g.addPlayerInRange(p);
				}else {
					g.removePlayerInRange(p);
				}
			}
		}
	}
	
	public static Set<GateObject> getGatesInWorld(String world){
		return GatesByWorldName.get(world);
	}
	
	public static int getPlayerGateAmmount(Player p) {
		int ammount = 0;
		for(GateObject gate : Gates.values()) {
			if(gate.getOwnerUUID().equals(p.getUniqueId().toString())) {
				ammount++;
			}
		}
		return ammount;
	}
	
	public static int getPlayerGateAmmount(String uuid) {
		int ammount = 0;
		for(GateObject gate : Gates.values()) {
			if(gate.getOwnerUUID().equals(uuid)) {
				ammount++;
			}
		}
		return ammount;
	}
	
	public static GateObject getGateWithAddress(String Address) {
		return Gates.get(Address);
	}
	
	public static GateObject getGateOnOtherServerWithAddress(String Address) {
		for(GateObject gate : GatesOnOtherServer) {
			if(gate.getAddress().equals(Address)) {
				return gate;
			}
		}
		return null;
	}
	
	public static boolean hasGateWithAddress(String Address) {
		return Gates.get(Address) != null;
	}
	
	public static boolean hasGateOnOtherServerWithAddress(String Address) {
		for(GateObject gate : GatesOnOtherServer) {
			if(gate.getAddress().equals(Address)) {
				return true;
			}
		}
		return false;
	}
	
	public static GateObject getGateByDHD(Location DHDLoc) {
		Location l = DHDLoc;

		Set<GateObject> gates = GatesByWorldName.get(l.getWorld().getName());
		if(gates == null) return null;
		for(GateObject gate : gates) {
			if(gate.hasDHD()) {
				if(gate.getDHD().equals(DHDLoc)) {
					return gate;
				}
			}
		}
		return null;
	}
			
	public static boolean hasDHDatLocation(Location DHDLocation) {
		Location l = DHDLocation;

		Set<GateObject> gates = GatesByWorldName.get(l.getWorld().getName());
		if(gates == null) return false;
		for(GateObject gate : gates) {
			if(gate.hasDHD()) {
				if(gate.getDHD().equals(DHDLocation)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static GateObject getClosestGateTo(Location loc) {
		GateObject closest = null;
		double last_d = Integer.MAX_VALUE;
		Set<GateObject> set = GatesByWorldName.get(loc.getWorld().getName());
		if(set == null) return null;
		for(GateObject obj : set) {
			double d = GateMath.getDistance(obj.getGate().getX(),
					obj.getGate().getY(),
					obj.getGate().getZ(),
					loc.getX(),
					loc.getY(),
					loc.getZ());
			if(d < last_d) {
				last_d = d;
				closest = obj;
			}
		}
		return closest;
	}
	
	public static List<GateObject> getAvailableGatesForEdit(Player p) {
		return Gates.values().stream().filter(x -> {
			return (x.hasOwningRelation(p) && p.hasPermission(Perms.thegate_owner_editgate.value())) ||
					p.hasPermission(Perms.thegate_admin_editgate.value());
		}).collect(Collectors.toList());
	}
	
	public static void repairAll() {
		for(GateObject o : Gates.values()) {
			o.Repair();
		}
	}
	
	public static HashMap<String, GateObject> getGates() {
		return Gates;
	}
	
	public static Set<GateObject> getGatesAsSet(){
		return Sets.newHashSet(Gates.values());
	}
	
	public static Set<GateObject> getChangedGatesAsSet(){
		Set<GateObject> gates = new HashSet<>();
		for(GateObject obj : Gates.values()) {
			if(obj.isUpdated()) {
				gates.add(obj);
				obj.setUpdated(false);
			}
		}
		return gates;
	}

	public static void setGates(HashMap<String, GateObject> gates) {
		Gates = gates;
	}

	public static Set<GateObject> getGatesOnOtherServer() {
		return GatesOnOtherServer;
	}

	public static void setGatesOnOtherServer(Set<GateObject> gatesOnOtherServer) {
		GatesOnOtherServer = gatesOnOtherServer;
	}
	
}
