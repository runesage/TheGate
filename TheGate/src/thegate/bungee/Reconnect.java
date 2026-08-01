package thegate.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Reconnect{
	
	public Reconnect(){
		
	}

	public void ReconnectPlayer(String Server, Player player, Plugin p) {
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("ConnectOther");
		out.writeUTF(player.getName());
		out.writeUTF(Server);
		
		player.sendPluginMessage(p, "BungeeCord", out.toByteArray());
		
//		new BukkitRunnable() {
//			
//			@Override
//			public void run() {
//				System.out.println("Testing if data is written...");
//				if(new DatabaseAccess("jdbc:mysql:" + Globals.MySQLPath, Globals.MySQLUserName, Globals.MySQLUserPassword).hasPlayerInPlayersTable("jdbc:mysql:" + Globals.MySQLPath, Globals.MySQLUserName, Globals.MySQLUserPassword, player)) {
//					
//					this.cancel();
//				}
//			}
//		}.runTaskTimer(p, 4, 60);
	}
	
}
