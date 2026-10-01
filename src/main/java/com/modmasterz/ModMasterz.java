package com.modmasterz;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModMasterz implements ModInitializer {
	public static final String MOD_ID = "modmasterz"; // kept for compatibility
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("LootLens initialized");
	}
}
