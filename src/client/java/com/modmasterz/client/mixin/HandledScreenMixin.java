package com.modmasterz.client.mixin;

import com.modmasterz.client.widget.ChestSummaryButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
	@Shadow
	@Final
	protected ScreenHandler handler;

	@Shadow
	protected int x;

	@Shadow
	protected int y;

	@Shadow
	protected int backgroundWidth;

	@Unique
	private TextFieldWidget modmasterz$searchField;

	@Unique
	private ChestSummaryButton modmasterz$summaryButton;

	@Unique
	private String modmasterz$filter = "";

	@Unique
	private boolean modmasterz$summaryOpen = false;

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void modmasterz$initWidgets(CallbackInfo ci) {
		this.modmasterz$searchField = null;
		this.modmasterz$summaryButton = null;
		this.modmasterz$filter = "";
		this.modmasterz$summaryOpen = false;

		if (!(this.handler instanceof GenericContainerScreenHandler)) {
			return;
		}

		int searchWidth = 100;
		int searchX = this.x + Math.max(8, (this.backgroundWidth - searchWidth) / 2);
		TextFieldWidget field = new TextFieldWidget(this.textRenderer, searchX, this.y - 18, searchWidth, 12, Text.literal("Search"));
		field.setMaxLength(50);
		field.setPlaceholder(Text.literal("Search items..."));
		field.setChangedListener(text -> this.modmasterz$filter = text.trim().toLowerCase(Locale.ROOT));
		this.modmasterz$searchField = this.addDrawableChild(field);

		this.modmasterz$summaryButton = this.addDrawableChild(new ChestSummaryButton(
			this.x + this.backgroundWidth - 16,
			this.y + 2,
			12,
			12,
			() -> this.modmasterz$summaryOpen = !this.modmasterz$summaryOpen
		));
	}

	@Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
	private void modmasterz$hideSlot(DrawContext context, Slot slot, CallbackInfo ci) {
		if (this.modmasterz$isHidden(slot)) {
			ci.cancel();
		}
	}

	@Inject(method = "getSlotAt", at = @At("RETURN"), cancellable = true)
	private void modmasterz$filterSlotAt(double mouseX, double mouseY, CallbackInfoReturnable<Slot> cir) {
		Slot slot = cir.getReturnValue();
		if (slot != null && this.modmasterz$isHidden(slot)) {
			cir.setReturnValue(null);
		}
	}

	@Inject(method = "isPointWithinBounds", at = @At("HEAD"), cancellable = true)
	private void modmasterz$blockHover(int boundX, int boundY, int width, int height, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
		if (this.modmasterz$filter.isEmpty()) {
			return;
		}
		for (Slot slot : this.handler.slots) {
			if (slot.x == boundX && slot.y == boundY && this.modmasterz$isHidden(slot)) {
				cir.setReturnValue(false);
				return;
			}
		}
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void modmasterz$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if (this.modmasterz$summaryOpen && keyCode == 256) {
			this.modmasterz$summaryOpen = false;
			cir.setReturnValue(true);
			return;
		}
		TextFieldWidget field = this.modmasterz$searchField;
		if (field != null && field.isFocused() && keyCode != 256) {
			field.keyPressed(keyCode, scanCode, modifiers);
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void modmasterz$renderSummary(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		this.modmasterz$drawGoldSearchOutline(context);
		if (this.modmasterz$summaryOpen) {
			this.modmasterz$drawSummary(context);
		}
	}

	@Unique
	private void modmasterz$drawGoldSearchOutline(DrawContext context) {
		TextFieldWidget field = this.modmasterz$searchField;
		if (field == null) return;
		context.drawBorder(field.getX() - 1, field.getY() - 1, field.getWidth() + 2, field.getHeight() + 2, 0xFFFFD700);
	}

	@Unique
	private boolean modmasterz$isHidden(Slot slot) {
		if (this.modmasterz$filter.isEmpty()) {
			return false;
		}
		if (!(this.handler instanceof GenericContainerScreenHandler container)) {
			return false;
		}
		if (slot.inventory != container.getInventory()) {
			return false;
		}
		ItemStack stack = slot.getStack();
		if (stack.isEmpty()) {
			return true;
		}
		return !stack.getName().getString().toLowerCase(Locale.ROOT).contains(this.modmasterz$filter);
	}

	@Unique
	private void modmasterz$drawSummary(DrawContext context) {
		if (!(this.handler instanceof GenericContainerScreenHandler container)) {
			return;
		}
		Inventory inventory = container.getInventory();

		LinkedHashMap<String, int[]> counts = new LinkedHashMap<>();
		LinkedHashMap<String, String> names = new LinkedHashMap<>();
		for (Slot slot : this.handler.slots) {
			if (slot.inventory != inventory) {
				continue;
			}
			ItemStack stack = slot.getStack();
			if (stack.isEmpty()) {
				continue;
			}
			String name = stack.getName().getString();
			String key = Registries.ITEM.getId(stack.getItem()) + "|" + name;
			counts.computeIfAbsent(key, k -> new int[1])[0] += stack.getCount();
			names.putIfAbsent(key, name);
		}

		List<String> itemLines = new ArrayList<>();
		int total = 0;
		for (Map.Entry<String, int[]> entry : counts.entrySet()) {
			int amount = entry.getValue()[0];
			total += amount;
			itemLines.add(names.get(entry.getKey()) + " x" + amount);
		}
		if (itemLines.isEmpty()) {
			itemLines.add("Empty");
		}

		int panelWidth = 190;
		int headerHeight = 14;
		int lineHeight = 10;
		int maxItemLines = Math.max(1, (this.height - 90) / lineHeight);
		boolean truncated = itemLines.size() > maxItemLines;
		int shown = Math.min(itemLines.size(), maxItemLines);
		int footerLines = 2;
		int panelHeight = headerHeight + (shown + (truncated ? 1 : 0) + footerLines) * lineHeight + 6;

		int left = this.x + this.backgroundWidth + 8;
		if (left + panelWidth > this.width - 6) {
			left = Math.max(6, this.x - panelWidth - 8);
		}
		int top = this.y;
		if (top + panelHeight > this.height - 6) {
			top = Math.max(6, this.height - panelHeight - 6);
		}

		context.fill(left - 2, top - 2, left + panelWidth + 2, top + panelHeight + 2, 0xFF000000);
		context.fill(left, top, left + panelWidth, top + panelHeight, 0xF0101010);
		context.drawBorder(left, top, panelWidth, panelHeight, 0xFFFFD700);
		context.drawTextWithShadow(this.textRenderer, "Chest Contents", left + 6, top + 4, 0xFFFFD700);

		int textY = top + headerHeight;
		for (int i = 0; i < shown; i++) {
			context.drawTextWithShadow(this.textRenderer, itemLines.get(i), left + 6, textY, 0xFFFFFFFF);
			textY += lineHeight;
		}
		if (truncated) {
			context.drawTextWithShadow(this.textRenderer, "... " + (itemLines.size() - shown) + " more", left + 6, textY, 0xFFAAAAAA);
			textY += lineHeight;
		}
		context.drawTextWithShadow(this.textRenderer, "Total " + total + " items", left + 6, textY, 0xFF80FF80);
		context.drawTextWithShadow(this.textRenderer, "ESC to close", left + 6, textY + lineHeight, 0xFF888888);
	}
}
