package thegate.gate.packages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.packageing.tools.packagetools.PackageManager;
import com.packageing.tools.packagetools.entitys.ArmorStand;

import thegate.gate.GateChevron;
import thegate.gate.GateObject;
import thegate.gate.IrisPart;
import thegate.main.Globals;
import thegate.main.Perms;


public class GatePackages {

	public ArmorStand[] Symbol = null;
	public ArrayList<GateChevron> Chevrons = null; // Integer = index
	private List<Integer> entityIDs = new ArrayList<>();
	public ArmorStand[] Ring = null; 
	public Set<ArmorStand> DisplayTextStands = null;
	public Vector[] SymbolPositionAfterRotation = null;
	
	public Map<Integer, Set<ArmorStand>> horizon = null;
	public Map<ArmorStand, Vector> horizonLocations = null;
	public Map<ArmorStand, Vector> horizonHeadPosition = null;
	

	private Set<IrisPart> iris = new HashSet<>();
	
	private GateObject gate = null;
	
	public GatePackages(GateObject g) {
		this.gate = g;
	}
	
	public void addEntityID(ArmorStand stand) {
		entityIDs.add(stand.getEntityID());
	}
	
	public void Display(Player p) {
		
		if(gate.isIrisClosed()) {
			for(IrisPart s : iris) {
	        	s.display(p);
			}
		}
		
		for(int i = 0; i < 16; i++) {
			ArmorStand stand = Symbol[i];
	        PackageManager.SendSpawnPackage(stand, p);
		}
		for(ArmorStand stand : Ring) {
	        PackageManager.SendSpawnPackage(stand, p);
		}
        for(GateChevron c : Chevrons) {
        	PackageManager.SendSpawnPackage(c.FrameBot, p);
        	PackageManager.SendSpawnPackage(c.LightBot, p);
        	PackageManager.SendSpawnPackage(c.LightTop, p);
        	PackageManager.SendSpawnPackage(c.LFrameBotLeft, p);
        	PackageManager.SendSpawnPackage(c.LFrameBotRight, p);
        	PackageManager.SendSpawnPackage(c.LFrameTopLeft, p);
        	PackageManager.SendSpawnPackage(c.LFrameTopRight, p);
        }
        
        if(this.gate.woosh != null) {
        	for(int i = 1; i <= gate.woosh.layerDone; i++) {
    	        for(ArmorStand stand : horizon.get(i)) {
    	        	PackageManager.SendSpawnPackage(stand, p);
    	        }
    		}
        }
        
        if(Globals.DisplayName && p.hasPermission(Perms.thegate_user_viewgatename.value())) {
    		for(ArmorStand stand : DisplayTextStands) {
        		PackageManager.SendSpawnPackage(stand, p);
	        }
    	}
	}
	
	public void Refresh() {
		for(Player p : this.gate.getPlayerInRange()) {
			for(int i = 0; i < 16; i++) {
				ArmorStand stand = Symbol[i];
		        PackageManager.SendUpdate(stand, p);
			}
			for(ArmorStand stand : Ring) {
		        PackageManager.SendSpawnPackage(stand, p);
			}
			for(GateChevron c : Chevrons) {
	        	PackageManager.SendSpawnPackage(c.FrameBot, p);
	        	PackageManager.SendSpawnPackage(c.LightBot, p);
	        	PackageManager.SendSpawnPackage(c.LightTop, p);
	        	PackageManager.SendSpawnPackage(c.LFrameBotLeft, p);
	        	PackageManager.SendSpawnPackage(c.LFrameBotRight, p);
	        	PackageManager.SendSpawnPackage(c.LFrameTopLeft, p);
	        	PackageManager.SendSpawnPackage(c.LFrameTopRight, p);
	        }
			if(Globals.DisplayName && p.hasPermission(Perms.thegate_user_viewgatename.value())) {
	    		for(ArmorStand stand : DisplayTextStands) {
	        		PackageManager.SendSpawnPackage(stand, p);
		        }
	    	}
		}
		
	}

	public ArmorStand[] getSymbol() {
		return Symbol;
	}

	public void setSymbol(ArmorStand[] symbol) {
		Symbol = symbol;
	}

	public ArrayList<GateChevron> getChevrons() {
		return Chevrons;
	}

	public void setChevrons(ArrayList<GateChevron> chevrons) {
		Chevrons = chevrons;
	}

	public ArmorStand[] getRing() {
		return Ring;
	}

	public void setRing(ArmorStand[] ring) {
		Ring = ring;
	}

	public Set<ArmorStand> getDisplayTextStands() {
		return DisplayTextStands;
	}

	public void setDisplayTextStands(Set<ArmorStand> displayTextStands) {
		DisplayTextStands = displayTextStands;
	}

	public Vector[] getSymbolPositionAfterRotation() {
		return SymbolPositionAfterRotation;
	}

	public void setSymbolPositionAfterRotation(Vector[] symbolPositionAfterRotation) {
		SymbolPositionAfterRotation = symbolPositionAfterRotation;
	}

	public Map<Integer, Set<ArmorStand>> getHorizon() {
		return horizon;
	}

	public void setHorizon(Map<Integer, Set<ArmorStand>> horizon) {
		this.horizon = horizon;
	}

	public Map<ArmorStand, Vector> getHorizonLocations() {
		return horizonLocations;
	}

	public void setHorizonLocations(Map<ArmorStand, Vector> horizonLocations) {
		this.horizonLocations = horizonLocations;
	}

	public Map<ArmorStand, Vector> getHorizonHeadPosition() {
		return horizonHeadPosition;
	}

	public void setHorizonHeadPosition(Map<ArmorStand, Vector> horizonHeadPosition) {
		this.horizonHeadPosition = horizonHeadPosition;
	}


	public Set<IrisPart> getIris() {
		return iris;
	}


	public void setIris(Set<IrisPart> iris) {
		this.iris = iris;
	}

	public List<Integer> getEntityIDs() {
		return entityIDs;
	}

	public void setEntityIDs(List<Integer> entityIDs) {
		this.entityIDs = entityIDs;
	}
}
