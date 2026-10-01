package com.modmasterz.mixin;

import net.minecraft.inventory.EnderChestInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnderChestInventory.class)
public abstract class EnderChestInventoryMixin {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 27))
	private int modmasterz$expandEnderChest(int original) {
		return 54;
	}
}
