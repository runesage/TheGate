package com.items.tools.itemtools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.common.returnsreceiver.qual.This;

public class ItemHandler implements Listener {
	
	private Set<EasyItem> easyItems = new HashSet<EasyItem>();
	private HashMap<ItemStack, EasyItem> itemToEasyItem = new HashMap<>();
	
	private final String itemCode;
	private final String itemCodeName;
	
	public ItemHandler(String itemCode, String itemCodeName) {
		this.itemCode = itemCode;
		this.itemCodeName = itemCodeName;
	}
	
	public void add(EasyItem... items) {
		easyItems.addAll(Arrays.asList(items));
		for(EasyItem item : items) {
			itemToEasyItem.put(item.getItem(), item);
		}
	}
	
	public EasyItem getEasyItemWithItem(ItemStack item) {
		return itemToEasyItem.get(item);
	}
	
	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent e) {
		EasyItem i = this.getEasyItemWithItem(e.getItem());
		if(i == null) return;
		e.setCancelled(true);
		
		if(i.getAction() != null && i.hasAcces(e.getPlayer()) && isValidItem(this, i)) i.getAction().apply(e);
	}
	
	public static boolean isValidItem(ItemHandler handler, EasyItem item) {
		if(item.getItem().hasItemMeta() && EasyItem.getNbtTag(item.pluginHandle, item.getItem(), handler.getItemCodeName()).equals(handler.getItemCode())) {
			return true;
		}
		return false;
	}

	public Set<EasyItem> getEasyItems() {
		return easyItems;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemCodeName() {
		return itemCodeName;
	}
	
}
