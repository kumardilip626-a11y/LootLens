package com.modmasterz.mixin;

import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestBlockMixin {
	@Redirect(method = "method_55773", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/GenericContainerScreenHandler;createGeneric9x3(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)Lnet/minecraft/screen/GenericContainerScreenHandler;"))
	private static GenericContainerScreenHandler modmasterz$createEnderChestHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
		return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, inventory);
	}
}
