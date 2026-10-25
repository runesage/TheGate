package com.packageing.tools.packagetools;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.packageing.tools.packagetools.entitys.ArmorStand;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

/**
 * Sends client-side entities to players using PacketEvents. Packets can be
 * sent from any thread.
 */
public class PackageManager {

	/** Call from the plugin's onLoad. */
	public static void load(Plugin plugin) {
		PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
		PacketEvents.getAPI().getSettings().checkForUpdates(false).bStats(false);
		PacketEvents.getAPI().load();
	}

	/** Call from the plugin's onEnable. */
	public static void enable() {
		PacketEvents.getAPI().init();
	}

	/** Call from the plugin's onDisable. */
	public static void disable() {
		if(PacketEvents.getAPI() != null) {
			PacketEvents.getAPI().terminate();
		}
	}

	public static void SendPacket(Player player, PacketWrapper<?> packet) {
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

	public static void SendSpawnPackage(ArmorStand stand, Player p) {
		WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
				stand.getEntityID(),
				Optional.of(stand.getUUID()),
				EntityTypes.ARMOR_STAND,
				position(stand),
				0f,
				stand.getBodyRotation(),
				stand.getHeadY(),
				0,
				Optional.of(Vector3d.zero()));
		SendPacket(p, spawn);
		SendPacket(p, new WrapperPlayServerEntityEquipment(stand.getEntityID(), stand.getEquipmentList()));
		SendPacket(p, new WrapperPlayServerEntityMetadata(stand.getEntityID(), stand.getItemData()));
	}

	public static void SendDespawnPackage(ArmorStand stand, Player p) {
		SendDespawnPackage(stand.getEntityID(), p);
	}

	public static void SendDespawnPackage(int standID, Player p) {
		SendPacket(p, new WrapperPlayServerDestroyEntities(standID));
	}

	public static void SendUpdate(ArmorStand stand, Player p) {
		SendPacket(p, new WrapperPlayServerEntityEquipment(stand.getEntityID(), stand.getEquipmentList()));
		SendPacket(p, new WrapperPlayServerEntityMetadata(stand.getEntityID(), stand.getItemData()));
	}

	public static void SendTeleport(ArmorStand stand, Player p) {
		SendPacket(p, new WrapperPlayServerEntityMetadata(stand.getEntityID(), stand.getItemData()));
		SendPacket(p, new WrapperPlayServerEntityTeleport(stand.getEntityID(), position(stand), stand.getBodyRotation(), 0f, false));
	}

	private static Vector3d position(ArmorStand stand) {
		return new Vector3d(stand.getLocationX(), stand.getLocationY(), stand.getLocationZ());
	}

}
