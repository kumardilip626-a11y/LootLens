package com.modmasterz.client.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ChestSummaryButton extends ClickableWidget {
	private final Runnable onPress;

	public ChestSummaryButton(int x, int y, int width, int height, Runnable onPress) {
		super(x, y, width, height, Text.literal("..."));
		this.onPress = onPress;
	}

	@Override
	protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		int background = this.isMouseOver(mouseX, mouseY) ? 0xC0555555 : 0x80000000;
		context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), background);
		context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFF9A9A9A);

		int dotColor = 0xFFFFFFFF;
		int dotSize = 2;
		int gap = 1;
		int totalWidth = dotSize * 3 + gap * 2;
		int startX = this.getX() + (this.getWidth() - totalWidth) / 2;
		int startY = this.getY() + (this.getHeight() - dotSize) / 2;
		for (int i = 0; i < 3; i++) {
			int x = startX + i * (dotSize + gap);
			context.fill(x, startY, x + dotSize, startY + dotSize, dotColor);
		}
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		this.onPress.run();
	}
}
