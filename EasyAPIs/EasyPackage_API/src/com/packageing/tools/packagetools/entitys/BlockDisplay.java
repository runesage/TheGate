package com.packageing.tools.packagetools.entitys;

import org.bukkit.Material;

import com.mojang.math.Transformation;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.World;

public class BlockDisplay extends net.minecraft.world.entity.Display.BlockDisplay {

	public BlockDisplay(World var1) {
		super(EntityTypes.j, var1);
	}
	
	/*public void setTransform(Transformation transform) {
		this.a(transform);
	}
	
	public void setBlockState(Material m) {
		this.c((IBlockData)m.createBlockData());
	}*/
	
}
