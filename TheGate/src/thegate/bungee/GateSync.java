package thegate.bungee;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import thegate.gate.GateManager;
import thegate.gate.GateObject;
import thegate.main.Globals;
import thegate.main.TheGateMain;

/**
 * Tells the other servers in the network to reload their list of gates when
 * gates change here, so new gates can be dialed without waiting for the
 * periodic database sync.
 *
 * Plugin messages travel over player connections, so a server with nobody
 * online can neither send nor receive them. Those servers catch up when their
 * first player joins and on the periodic sync.
 */
public class GateSync {

	private static final String SUBCHANNEL = "TheGate";
	private static final String GATES_CHANGED = "gates-changed";

	/** Safe to call from any thread, after the change is written to the database. */
	public static void gatesChanged() {
		Plugin plugin = TheGateMain.theGateMain;
		if(!Globals.UseBungee || !plugin.isEnabled()) return;
		Bukkit.getScheduler().runTask(plugin, () -> {
			Player carrier = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
			if(carrier == null) return;

			ByteArrayDataOutput data = ByteStreams.newDataOutput();
			data.writeUTF(GATES_CHANGED);
			data.writeUTF(Globals.ServerName);
			byte[] payload = data.toByteArray();

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF(SUBCHANNEL);
			out.writeShort(payload.length);
			out.write(payload);
			carrier.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		});
	}

	/** Handles a message on the BungeeCord channel, ignoring other plugins' subchannels. */
	public static void onMessage(byte[] message) {
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		if(!in.readUTF().equals(SUBCHANNEL)) return;
		byte[] payload = new byte[in.readShort()];
		in.readFully(payload);

		ByteArrayDataInput data = ByteStreams.newDataInput(payload);
		if(!data.readUTF().equals(GATES_CHANGED)) return;
		String from = data.readUTF();
		if(from.equals(Globals.ServerName)) return;

		TheGateMain.theGateMain.getLogger().info("Gates changed on " + from + ", reloading gates on other servers.");
		reloadGatesOnOtherServers();
	}

	/** Must run on the main thread, like the periodic sync. */
	public static void reloadGatesOnOtherServers() {
		Set<GateObject> gates = TheGateMain.SaveLoadInterface.GateListOtherServers();
		if(gates != null) GateManager.setGatesOnOtherServer(gates);
	}

}
