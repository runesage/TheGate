package com.packageing.tools.packagetools.entitys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.Vector3f;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher.c;
import net.minecraft.server.level.EntityTrackerEntry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.level.World;

public class ArmorStand extends PackageEntity<EntityArmorStand> {

	private float bodyRotation = 0f;
	private double locationX = 0, locationY = 0, locationZ = 0;
	private float headX = 0, headY = 0, headZ = 0;
	private String customName = "";
	private ItemStack headMaterial = null;
	private ItemStack mainHand = null;
	private ItemStack offHand = null;
	private ItemStack bodyArmor = null;
	private ItemStack legArmor = null;
	private ItemStack feetArmor = null;
	private final int entityID;
	
	public ArmorStand(EntityArmorStand entity) {
		super(entity);
		// from net.minecraft.world.entity.Entity 
		// public int getId() [426]
		// public int an() [429]
		this.entityID = entity.ar(); 
	}
	
	public static ArmorStand CreateArmorStand(Location location, float headRotationX, float headRotationY, float headRotationZ, float rotBody) {
		World s = ((CraftWorld)location.getWorld()).getHandle();
		EntityArmorStand stand = new EntityArmorStand(EntityTypes.f ,s);
		
        ArmorStand astand = new ArmorStand(stand);
        astand.bodyRotation = rotBody;
        astand.setLocation(location.getX(), location.getY(), location.getZ());
        astand.setCustomNameVisible(false);
        astand.setHeadRotation(headRotationX, headRotationY, headRotationZ);
        astand.setSmall(false);
        astand.setInvisible(true);
        astand.setHasBasePlate(false);
        return astand;
	}
	
	public List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> getEquipmentList() {
		List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> output = new ArrayList<>();
		
		// net.minecraft.world.entity.EnumItemSlot
		// Beginning of file
		
		if(mainHand     != null) output.add(new Pair<>(EnumItemSlot.a, CraftItemStack.asNMSCopy(mainHand)));
		if(offHand      != null) output.add(new Pair<>(EnumItemSlot.b, CraftItemStack.asNMSCopy(offHand)));
		if(feetArmor    != null) output.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(feetArmor)));
		if(legArmor     != null) output.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(legArmor)));
		// [chest missing]
		if(headMaterial != null) output.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(headMaterial)));
		if(bodyArmor    != null) output.add(new Pair<>(EnumItemSlot.g, CraftItemStack.asNMSCopy(bodyArmor)));
		
		if(output.isEmpty())
			output.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR))));

		return output;
	}
	
	public void setHeadRotation(float x, float y, float z) {
		this.headX = x;
		this.headY = y;
		this.headZ = z;
		this.getEntity().a(new Vector3f(x,y,z));
	}
	
	public void setLocation(double x, double y, double z) {
		this.locationX = x;
		this.locationY = y;
		this.locationZ = z;
		this.getEntity().a(x, y, z, this.bodyRotation, 0);
	}
	
	public BlockPosition getBlockPosition() 
	{
		return new BlockPosition((int)locationX, (int)locationY, (int)locationZ);
	}
	
	public EntityTrackerEntry getEntityTrackerEntry() 
	{
		//EntityTrackerEntry ete = new EntityTrackerEntry();
		
		return null;
	}
	
	public void setCustomName(String text) {
//		NBTTagCompound nbt = new NBTTagCompound();
//		nbt.a("\"CustomName\"", "\"{\"text\":\"" + text + "\"}\"");
        this.getEntity().b(IChatBaseComponent.a(text));
	}
	
	public void setCustomNameVisible(boolean visible) {
		this.getEntity().p(visible);
	}

	public double getLocationX() {
		return locationX;
	}

	public double getLocationY() {
		return locationY;
	}

	public double getLocationZ() {
		return locationZ;
	}
	
	public Vector getLocationVector() {
		return new Vector(locationX, locationY, locationZ);
	}

	public void setSmall(boolean isSmall) { 
		// In net.minecraft.world.entity.decoration.EntityArmorStand
		this.getEntity().u(isSmall);
	}

	public void setHasBasePlate(boolean hasBasePlate) {
		// In net.minecraft.world.entity.decoration.EntityArmorStand
		// isNoBasePlate
		this.getEntity().b(!hasBasePlate);
	}
	
	public void SetShowArms(boolean showarms) {
		// In net.minecraft.world.entity.decoration.EntityArmorStand
		this.getEntity().a(showarms);
	}

	public void setNoGravity(boolean hasGravity) {
		// In net.minecraft.world.entity.Entity
		this.getEntity().f(hasGravity);
	}

	public void setInvisible(boolean isInvisible) {
		// In net.minecraft.world.entity.Entity
		this.getEntity().k(isInvisible);
	}

	public float getHeadX() {
		return headX;
	}

	public float getHeadY() {
		return headY;
	}

	public float getHeadZ() {
		return headZ;
	}
	
	public Vector getHeadVector() {
		return new Vector(headX, headY, headZ);
	}
	
	public Vector3f getHeadVector3f() {
		return new Vector3f(headX, headY, headZ);
	}
	
	public List<c<?>> getItemData() {
		//In net.minecraft.network.syncher.DataWatcher 
		//List<DataWatcher.c<?>> getNonDefaultValues() [104]
		//List<DataWatcher.c<?>> c() [104]
		return this.getEntity().au().c();
	}

	public String getCustomName() {
		return customName;
	}

	public float getBodyRotation() {
		return bodyRotation;
	}

	public void setBodyRotation(float bodyRotation) {
		this.bodyRotation = bodyRotation;
		this.setLocation(this.locationX, this.locationY, this.locationZ);
	}

	public ItemStack getHeadMaterial() {
		return headMaterial;
	}

	public void setHeadMaterial(ItemStack headMaterial) {
		this.headMaterial = headMaterial;
	}

	public ItemStack getMainHand() {
		return mainHand;
	}

	public void setMainHand(ItemStack mainHand) {
		this.mainHand = mainHand;
	}

	public ItemStack getOffHand() {
		return offHand;
	}

	public void setOffHand(ItemStack offHand) {
		this.offHand = offHand;
	}

	public ItemStack getBodyArmor() {
		return bodyArmor;
	}

	public void setBodyArmor(ItemStack bodyArmor) {
		this.bodyArmor = bodyArmor;
	}

	public ItemStack getLegArmor() {
		return legArmor;
	}

	public void setLegArmor(ItemStack legArmor) {
		this.legArmor = legArmor;
	}

	public ItemStack getFeetArmor() {
		return feetArmor;
	}

	public void setFeetArmor(ItemStack feetArmor) {
		this.feetArmor = feetArmor;
	}

	public int getEntityID() {
		return entityID;
	}

}
