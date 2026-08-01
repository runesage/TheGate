package com.packageing.tools.packagetools;

import org.bukkit.entity.Player;

import com.packageing.tools.packagetools.entitys.ArmorStand;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.entity.PositionMoveRotation;

import java.util.Set;

import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;


public class PackageManager {

	public static void SendPacket(Player player, Packet<?> packet) {
		((CraftPlayer)player).getHandle().f.a(packet); // c has type player connection
	}
	
	public static void SendSpawnPackage(ArmorStand stand, Player p) {
		/*
		 * PacketPlayOutSpawnEntity(
		 * 	int EntityID, 
		 * 	UUID UUID, 
		 * 	double posX, 
		 * 	double posY, 
		 * 	double posZ, 
		 * 	float XRot, 
		 * 	float YRot, 
		 * 	EntityTypes<?> EntityType, 
		 * 	int updateRate (keep at 0), 
		 * 	Vec3D deltaMovement, 
		 * 	double YHeadRot)
		 * 
		 * */
		PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity
			(
				stand.getEntityID(), 
				stand.getEntity().cG(),
				stand.getLocationX(),
				stand.getLocationY(),
				stand.getLocationZ(),
				0,
				stand.getBodyRotation(),
				EntityTypes.f,
				0,
				new Vec3D(0.0f,0.0f,0.0f),
				stand.getHeadY()
			);
		//PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(stand.getEntity(), 0, stand.getBlockPosition());
//        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)));
		PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getEntityID(), stand.getEquipmentList());//net.minecraft.world.entity.decoration.EntityArmorStand
//		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getEntityID(), stand.getEntity().ai(), true);
		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getEntityID(), stand.getItemData());
        SendPacket(p, spawn); 
        SendPacket(p, equip);
        SendPacket(p, metadata);
        //SendTeleport(stand, p);
	}
	
	public static void SendDespawnPackage(ArmorStand stand, Player p) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(stand.getEntityID());
		SendPacket(p, destroy);
	}
	
	public static void SendDespawnPackage(int standID, Player p) {
		//net.minecraft.server.network.PlayerConnection
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(standID);
		SendPacket(p, destroy);
	}
	
	public static void SendUpdate(ArmorStand stand, Player p) {
//      PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(m)));
		PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getEntityID(), stand.getEquipmentList());
		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getEntityID(), stand.getItemData());
        SendPacket(p, equip);
		SendPacket(p, metadata);
	}
	
	public static void SendTeleport(ArmorStand stand, Player p) {
		PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(stand.getEntityID(), stand.getItemData());
        PacketPlayOutEntityTeleport telport = new PacketPlayOutEntityTeleport(stand.getEntityID(), PositionMoveRotation.a(stand.getEntity()), Set.of(), false);
		SendPacket(p, metadata);
		SendPacket(p, telport);
	}
	
//	public static void SendBlockChangePackage(Block block, Material m, Player p) {
//		IBlockData blockdata = blockData(m);
//		PacketPlayOutBlockChange changeEvent = new PacketPlayOutBlockChange(blockPosition(block), blockdata);
//		((CraftPlayer)p).getHandle().playerConnection.sendPacket(changeEvent);
//	}
	
//	private static IBlockData blockData(Block b) {
//	    net.minecraft.server.v1_16_R3.World nmsWorld = ((org.bukkit.craftbukkit.v1_16_R3.CraftWorld)b.getWorld()).getHandle().getMinecraftWorld();
//	    BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
//	    return nmsWorld.getType(bp);
//	}
	
//	private static IBlockData blockData(Material m) {
//	    net.minecraft.server.v1_16_R3.Block nmsBlock = CraftMagicNumbers.getBlock(m);
//	    return nmsBlock.getBlockData();
//	}
//	
//	private static BlockPosition blockPosition(Block b) {
//	    BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());
//	    return bp;
//	}
	
}