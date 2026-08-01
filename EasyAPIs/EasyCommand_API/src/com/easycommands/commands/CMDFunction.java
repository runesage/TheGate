package com.easycommands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CMDFunction {

	public boolean func(CommandSender sender, Command cmd, String str, String[] args);
	
}
