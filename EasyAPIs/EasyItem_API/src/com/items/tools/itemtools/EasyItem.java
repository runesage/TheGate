package com.items.tools.itemtools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Warning;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.google.errorprone.annotations.DoNotCall;

public class EasyItem {

	private final ItemStack item;
	private final ItemAction action;
	private Set<String> Permissions = new HashSet<String>();

	private final String code;
	private final String codeName;
	public final Plugin pluginHandle;
	
	public EasyItem(Plugin plugin, ItemHandler handler, String name, Material m, List<String> lore, ItemAction action) {
		item = setNbtTag(plugin, getItem(name, lore, m), handler.getItemCodeName(), handler.getItemCode());
		//item = getItem(name, lore, m);
		this.action = action;
		this.code = handler.getItemCode();
		this.codeName = handler.getItemCodeName();
		this.pluginHandle = plugin;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ItemStack) && !(obj instanceof EasyItem)) return false;
		ItemStack other;

		if(obj instanceof ItemStack) {
			other = (ItemStack)obj;
		}else {
			other = ((EasyItem)obj).getItem(); 
		}

		if(!other.hasItemMeta()) return false;

		ItemMeta metaOther = other.getItemMeta();
		ItemMeta myMeta = item.getItemMeta();
		return (item.getType().equals(other.getType())
				&& myMeta.getDisplayName().equals(metaOther.getDisplayName()));
	}
	
	public static String getNbtTag(Plugin plugin, ItemStack item, String tag) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, PersistentDataType.STRING, "");
	}
	
	public static ItemStack setNbtTag(Plugin plugin, ItemStack item, String tag, String value) {
		NamespacedKey key = new NamespacedKey(plugin, tag);
		ItemMeta meta = item.getItemMeta();
		meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getItem(String name, List<String> lore, Material m) {
		ItemStack out = new ItemStack(m);
		ItemMeta meta = out.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName(name);
		out.setItemMeta(meta);
		return out;
	}
	
	public void addPermissions(String... permissions) {
		for(String s : permissions) {
			this.Permissions.add(s);
		}
	}
	
	public boolean hasAcces(Player p) {
		if(Permissions.size() == 0) return true;
		for(String s : Permissions) {
			if(p.hasPermission(s)) return true;
		}
		return false;
	}
	
	public ItemStack getItem() {
		return item;
	}

	public ItemAction getAction() {
		return action;
	}

	public Set<String> getPermissions() {
		return Permissions;
	}
	
	
	
}
