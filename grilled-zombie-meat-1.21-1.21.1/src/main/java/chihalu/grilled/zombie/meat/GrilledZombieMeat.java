package chihalu.grilled.zombie.meat;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chihalu.grilled.zombie.meat.item.ModItems;

public class GrilledZombieMeat implements ModInitializer {
	public static final String MOD_ID = "grilled-zombie-meat";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting grilled zombie meat mod initialization.");
		ModItems.initialize();
		LOGGER.info("Initialization finished.");
	}
}
