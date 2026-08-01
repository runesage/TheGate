package thegate.main.save;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.Globals;
import thegate.main.TheGateMain;

public class AutoSyncDatabase extends BukkitRunnable{

	private TheGateMain mainGate;

	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	public AutoSyncDatabase(TheGateMain mainGate) {
		this.mainGate = mainGate;
		mainGate.AutoSyncTime = LocalDateTime.now();
		
	}
	
	@Override
	public void run() {
		Set<GateObject> newGateList = TheGateMain.SaveLoadInterface.GateListOtherServers();
		Set<GateObject> changedGates = GateManager.getChangedGatesAsSet();

		
		GateManager.setGatesOnOtherServer(newGateList);
		
		TheGateMain.SaveLoadInterface.SaveDataFromSet(changedGates);
		
		if(newGateList.size() != GateManager.getGatesOnOtherServer().size()) {
			for(GateObject obj : GateManager.getGates().values()) {
				Location Loc = obj.getDHD();
				if(Loc != null) {
					for(int i = 0; i < 100; i++) {
						obj.getDHD().getWorld().spawnParticle(Particle.ENCHANT, Loc.getX() + 0.5 + ((new Random().nextDouble() * 2) - 1), Loc.getY() + 1 + ((new Random().nextDouble() * 2) - 1) * 0.5, Loc.getZ() + 0.5 + ((new Random().nextDouble() * 2) - 1), 0);
					}
				}
			}
		}
		mainGate.AutoSyncTime = LocalDateTime.now();
	}

}
