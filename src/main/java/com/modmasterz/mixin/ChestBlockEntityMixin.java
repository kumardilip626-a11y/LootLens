package com.modmasterz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin {
	private static final int MODMASTERZ_CHEST_SIZE = 54;

	@Shadow
	@Mutable
	private DefaultedList<ItemStack> inventory;

	@Inject(method = "<init>(Lnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("TAIL"))
	private void modmasterz$expandInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
		this.inventory = DefaultedList.ofSize(MODMASTERZ_CHEST_SIZE, ItemStack.EMPTY);
	}

	@Inject(method = "size", at = @At("HEAD"), cancellable = true)
	private void modmasterz$size(CallbackInfoReturnable<Integer> cir) {
		if (this.modmasterz$isSingleChest()) {
			cir.setReturnValue(MODMASTERZ_CHEST_SIZE);
		}
	}

	@Inject(method = "getHeldStacks", at = @At("RETURN"), cancellable = true)
	private void modmasterz$ensureCapacity(CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
		DefaultedList<ItemStack> current = cir.getReturnValue();
		if (this.modmasterz$isSingleChest() && current.size() < MODMASTERZ_CHEST_SIZE) {
			DefaultedList<ItemStack> expanded = DefaultedList.ofSize(MODMASTERZ_CHEST_SIZE, ItemStack.EMPTY);
			for (int i = 0; i < current.size(); i++) {
				expanded.set(i, current.get(i));
			}
			this.inventory = expanded;
			cir.setReturnValue(expanded);
		}
	}

	@Inject(method = "createScreenHandler", at = @At("HEAD"), cancellable = true)
	private void modmasterz$createScreenHandler(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> cir) {
		if (this.modmasterz$isSingleChest()) {
			cir.setReturnValue(GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, (Inventory) (Object) this));
		}
	}

	private boolean modmasterz$isSingleChest() {
		BlockEntity self = (BlockEntity) (Object) this;
		World world = self.getWorld();
		BlockState state = world != null ? world.getBlockState(self.getPos()) : self.getCachedState();
		return state.contains(ChestBlock.CHEST_TYPE) && state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE;
	}
}
