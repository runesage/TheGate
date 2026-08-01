package com.easycommands.commands;

import org.bukkit.entity.Player;

public class MissingPermissions extends Exception{

	private final String label;
	private final String[] args;
	private String message;
	private final Player player;
	private final MissingPermissionHandle handle;
	private boolean returnStatus = true;
	
	public MissingPermissions(Player player, MissingPermissionHandle handle, String message, String label, String[] args) {
		super(message);
		this.label = label;
		this.args = args;
		this.player = player;
		this.handle = handle;
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

	public String getLabel() {
		return label;
	}

	public String[] getArgs() {
		return args;
	}

	public Player getPlayer() {
		return player;
	}

	public MissingPermissionHandle getHandle() {
		return handle;
	}

	public boolean returnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(boolean returnStatus) {
		this.returnStatus = returnStatus;
	}
	
}
