package com.packageing.tools.packagetools.entitys;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * A client-side armor stand. It only exists as packets sent to players
 * through {@link com.packageing.tools.packagetools.PackageManager}.
 */
public class ArmorStand {

	// Entity data indices for Minecraft 26.1.2, taken from the server's
	// Entity, LivingEntity and ArmorStand classes.
	private static final int DATA_SHARED_FLAGS = 0;
	private static final int DATA_CUSTOM_NAME = 2;
	private static final int DATA_CUSTOM_NAME_VISIBLE = 3;
	private static final int DATA_NO_GRAVITY = 5;
	private static final int DATA_ARMOR_STAND_FLAGS = 15;
	private static final int DATA_HEAD_POSE = 16;

	private static final byte FLAG_INVISIBLE = 0x20;
	private static final byte FLAG_SMALL = 0x01;
	private static final byte FLAG_SHOW_ARMS = 0x04;
	private static final byte FLAG_NO_BASE_PLATE = 0x08;

	private final int entityID;
	private final UUID uuid = UUID.randomUUID();

	private float bodyRotation = 0f;
	private double locationX = 0, locationY = 0, locationZ = 0;
	private float headX = 0, headY = 0, headZ = 0;
	private String customName = "";
	private boolean customNameVisible = false;
	private boolean small = false;
	private boolean hasBasePlate = true;
	private boolean showArms = false;
	private boolean noGravity = false;
	private boolean invisible = false;
	private ItemStack headMaterial = null;
	private ItemStack mainHand = null;
	private ItemStack offHand = null;
	private ItemStack bodyArmor = null;
	private ItemStack legArmor = null;
	private ItemStack feetArmor = null;

	public ArmorStand() {
		this.entityID = Bukkit.getUnsafe().nextEntityId();
	}

	public static ArmorStand CreateArmorStand(Location location, float headRotationX, float headRotationY, float headRotationZ, float rotBody) {
		ArmorStand astand = new ArmorStand();
		astand.bodyRotation = rotBody;
		astand.setLocation(location.getX(), location.getY(), location.getZ());
		astand.setCustomNameVisible(false);
		astand.setHeadRotation(headRotationX, headRotationY, headRotationZ);
		astand.setSmall(false);
		astand.setInvisible(true);
		astand.setHasBasePlate(false);
		return astand;
	}

	public List<Equipment> getEquipmentList() {
		List<Equipment> output = new ArrayList<>();

		if(mainHand     != null) output.add(equipment(EquipmentSlot.MAIN_HAND, mainHand));
		if(offHand      != null) output.add(equipment(EquipmentSlot.OFF_HAND, offHand));
		if(feetArmor    != null) output.add(equipment(EquipmentSlot.BOOTS, feetArmor));
		if(legArmor     != null) output.add(equipment(EquipmentSlot.LEGGINGS, legArmor));
		if(headMaterial != null) output.add(equipment(EquipmentSlot.HELMET, headMaterial));
		if(bodyArmor    != null) output.add(equipment(EquipmentSlot.BODY, bodyArmor));

		if(output.isEmpty())
			output.add(equipment(EquipmentSlot.HELMET, new ItemStack(Material.AIR)));

		return output;
	}

	private static Equipment equipment(EquipmentSlot slot, ItemStack item) {
		return new Equipment(slot, SpigotConversionUtil.fromBukkitItemStack(item));
	}

	public List<EntityData<?>> getItemData() {
		List<EntityData<?>> data = new ArrayList<>();

		byte sharedFlags = invisible ? FLAG_INVISIBLE : 0;
		byte standFlags = 0;
		if(small) standFlags |= FLAG_SMALL;
		if(showArms) standFlags |= FLAG_SHOW_ARMS;
		if(!hasBasePlate) standFlags |= FLAG_NO_BASE_PLATE;

		Optional<Component> name = customName.isEmpty()
				? Optional.empty()
				: Optional.of(LegacyComponentSerializer.legacySection().deserialize(customName));

		data.add(new EntityData<>(DATA_SHARED_FLAGS, EntityDataTypes.BYTE, sharedFlags));
		data.add(new EntityData<>(DATA_CUSTOM_NAME, EntityDataTypes.OPTIONAL_ADV_COMPONENT, name));
		data.add(new EntityData<>(DATA_CUSTOM_NAME_VISIBLE, EntityDataTypes.BOOLEAN, customNameVisible));
		data.add(new EntityData<>(DATA_NO_GRAVITY, EntityDataTypes.BOOLEAN, noGravity));
		data.add(new EntityData<>(DATA_ARMOR_STAND_FLAGS, EntityDataTypes.BYTE, standFlags));
		data.add(new EntityData<>(DATA_HEAD_POSE, EntityDataTypes.ROTATION, new Vector3f(headX, headY, headZ)));
		return data;
	}

	public void setHeadRotation(float x, float y, float z) {
		this.headX = x;
		this.headY = y;
		this.headZ = z;
	}

	public void setLocation(double x, double y, double z) {
		this.locationX = x;
		this.locationY = y;
		this.locationZ = z;
	}

	public void setCustomName(String text) {
		this.customName = text == null ? "" : text;
	}

	public void setCustomNameVisible(boolean visible) {
		this.customNameVisible = visible;
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
		this.small = isSmall;
	}

	public void setHasBasePlate(boolean hasBasePlate) {
		this.hasBasePlate = hasBasePlate;
	}

	public void SetShowArms(boolean showarms) {
		this.showArms = showarms;
	}

	public void setNoGravity(boolean noGravity) {
		this.noGravity = noGravity;
	}

	public void setInvisible(boolean isInvisible) {
		this.invisible = isInvisible;
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

	public String getCustomName() {
		return customName;
	}

	public float getBodyRotation() {
		return bodyRotation;
	}

	public void setBodyRotation(float bodyRotation) {
		this.bodyRotation = bodyRotation;
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

	public UUID getUUID() {
		return uuid;
	}

}
