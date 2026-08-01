package thegate.gate;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GateCommandInfo {
	private final String command;
	private final CommandUser user;
	private final GateObject gate;
	
	public GateCommandInfo(String command, CommandUser user, GateObject gate) {
		this.command = command;
		this.user = user;
		this.gate = gate;
	}
	
	public void dispach(CommandSender sender) {
		String[] args = command.split(" ");
		String out = "";
		int count = 0;
		for(String s: args) {
			if(s.charAt(0) == '~') {
				count++;
				if(s.length() == 1) {
					switch(count) {
						case 1:
							s = String.valueOf(gate.getGate().getX());
							break;
						case 2:
							s = String.valueOf(gate.getGate().getY());
							break;
						case 3:
							s = String.valueOf(gate.getGate().getZ());
							break;
						default:
							break;
					}
				} else if(s.length() > 1) {
					double x = 0;
					try {
						x = Double.valueOf(s.replace("~", ""));
					} catch(Exception e) {
						e.printStackTrace();
					}
					switch(count) {
						case 1:
							s = String.valueOf(gate.getGate().getX() + x);
							break;
						case 2:
							s = String.valueOf(gate.getGate().getY() + x);
							break;
						case 3:
							s = String.valueOf(gate.getGate().getZ() + x);
							break;
						default:
							break;
					}
				}
			}
			out += s + " ";
		}
		out = out.substring(0, out.length()-1);
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			out = out.replace("{PLAYER}", player.getName());
		}
		this.user.dispach(out, sender);
	}

	public String getCommand() {
		return command;
	}

	public CommandUser getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return "Command: " + command + " -> for user: " + user.name();
	}
	
}
