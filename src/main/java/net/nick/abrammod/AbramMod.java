package net.nick.abrammod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.nick.abrammod.block.ModBlocks;
import net.nick.abrammod.block.entity.ModBlockEntities;
import net.nick.abrammod.entity.ModEntities;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbramMod implements ModInitializer {
	public static final String MOD_ID = "abrammod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        LOGGER.info("AbramMob Mod has started up.");
        ModBlocks.registerModBlocks();
        ModEntities.registerModEntities();
        ModBlockEntities.registerBlockEntities();
        FabricDefaultAttributeRegistry.register(ModEntities.ABRAMS_GOLEM, AbramsGolemEntity.createAbramGolemAttributes());
	}
}