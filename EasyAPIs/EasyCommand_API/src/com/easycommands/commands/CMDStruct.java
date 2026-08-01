package com.easycommands.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CMDStruct {

	private final String wildCardPattern = "[<][a-zA-Z0-9]*[>]";
	
	private final String part;
	private final boolean isWildCard;
	private CMDFunction func;
	private Map<String, CMDStruct> next = new HashMap<>();
	private CMDTabLookup lookup;
	private PermissionCheck permissionCheck;
	private MissingPermissionHandle missingPermissinHandle = null;
	
	public CMDStruct(String part) {
		this(part, null);
	}
	
	public CMDStruct(String part, CMDFunction func) {
		this.isWildCard = Pattern.matches(wildCardPattern, part);
		this.part = part;
		this.func = func;
	}
	
	public CMDStruct search(String[] cmd) throws EasyCommandError {
		return this.search(cmd, 0);
	}
	
	private CMDStruct search(String[] parts, int c) throws EasyCommandError {
		if(!this.isWildCard && !parts[c].equalsIgnoreCase(this.part)) 
			throw new EasyCommandError("Pattern mismatch!\nExpected: " + this.part + " got " + parts[c]);
	
		if(c < parts.length-1) {
			if(hasNext(parts[c+1])) {
				return getNext(parts[c+1]).search(parts, ++c);
			}else {
				int wcount = 0;
				CMDStruct wcardNext = null;
				for(CMDStruct next : this.next.values()) {
					if(next.isWildCard) {
						++wcount;
						wcardNext = next;	
					}
				}
				if(wcount > 1)
					throw new EasyCommandError("Could not distinguish between arguments. Too many wild cards:" + this.next.keySet().stream().reduce("", (x, y) -> x + ", " + y));
				return wcardNext != null ? wcardNext.search(parts, ++c) : null;
			}
		}
		return this;
	}
	
	public ArrayList<String> getTabList(String[] parts){
		try {
			CMDStruct struct = search(parts);
			if(struct != null) {
				ArrayList<String> output = new ArrayList<>();
				for(CMDStruct element : struct.next.values()) {
					if(element.isWildCard) {
						if(element.lookup != null) {
							for(String item : element.lookup.get()) {
								output.add(item);
							}
						}
					}else {						
						output.add(element.part);
					}
				}
				return output;
			}
		} catch (EasyCommandError e) {
			return new ArrayList<>();
		}
		return new ArrayList<>();
	}
	
	public boolean addCMD(String[] parts, CMDFunction func) throws EasyCommandError {
		if(!parts[0].equalsIgnoreCase(this.part)) throw new EasyCommandError("Root was not the same: " + parts[0] + " != " + this.part);
		return addCMD(parts, func, 1);
	}
	
	private boolean addCMD(String[] parts, CMDFunction func, int c) {
		if(c > parts.length - 1) {
			this.func = func;
			return true;
		}
		if(!this.hasNext(parts[c])) {
			this.next.put(parts[c], new CMDStruct(parts[c], null));
		}
		return this.getNext(parts[c]).addCMD(parts, func, ++c);
	}
	
	public boolean addCMDLookup(String[] parts, CMDTabLookup tablookup) throws EasyCommandError {
		if(!parts[0].equalsIgnoreCase(this.part)) throw new EasyCommandError("Root was not the same: " + parts[0] + " != " + this.part);
		return addCMDLookup(parts, tablookup, 1);
	}
	
	private boolean addCMDLookup(String[] parts, CMDTabLookup tablookup, int c) {
		if(c > parts.length - 1) {
			this.lookup = tablookup;
			return true;
		}
		if(!this.hasNext(parts[c])) {
			this.next.put(parts[c], new CMDStruct(parts[c], null));
		}
		return this.getNext(parts[c]).addCMDLookup(parts, tablookup, ++c);
	}
	
	public boolean addPermissionCheck(String[] parts, PermissionCheck permissionCheck, MissingPermissionHandle handle) throws EasyCommandError {
		if(!parts[0].equalsIgnoreCase(this.part)) throw new EasyCommandError("Root was not the same: " + parts[0] + " != " + this.part);
		return addPermissionCheck(parts, permissionCheck, handle, 1);
	}
	
	private boolean addPermissionCheck(String[] parts, PermissionCheck permissionCheck, MissingPermissionHandle handle, int c) {
		if(c > parts.length - 1) {
			this.permissionCheck = permissionCheck;
			this.missingPermissinHandle = handle;
			return true;
		}
		if(!this.hasNext(parts[c])) {
			this.next.put(parts[c], new CMDStruct(parts[c], null));
		}
		return this.getNext(parts[c]).addPermissionCheck(parts, permissionCheck, handle, ++c);
	}
	
	public String getPart() {
		return this.part;
	}
	
	public boolean hasFunc() {
		return func != null;
	}
	
	public boolean hasPermissionCheck() {
		return this.permissionCheck != null;
	}
	
	public boolean hasNext(String part) {
		return next.containsKey(part);
	}
	
	public CMDStruct getNext(String part) {
		return next.get(part);
	}
	
	public CMDFunction getFunc() {
		return this.func;
	}

	public PermissionCheck getPermissionCheck() {
		return permissionCheck;
	}

	public void setPermissionCheck(PermissionCheck permissionCheck) {
		this.permissionCheck = permissionCheck;
	}

	public MissingPermissionHandle getMissingPermissinHandle() {
		return missingPermissinHandle;
	}

	public void setMissingPermissinHandle(MissingPermissionHandle missingPermissinHandle) {
		this.missingPermissinHandle = missingPermissinHandle;
	}

		
}
