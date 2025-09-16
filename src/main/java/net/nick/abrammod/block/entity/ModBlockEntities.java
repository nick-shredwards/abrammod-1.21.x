package net.nick.abrammod.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.ModBlocks;
import net.nick.abrammod.block.entity.custom.AbramsGolemChestBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<AbramsGolemChestBlockEntity> ABRAMS_GOLEM_CHEST_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(AbramMod.MOD_ID, "abrams_golem_chest_be"),
                    FabricBlockEntityTypeBuilder.create(AbramsGolemChestBlockEntity::new, ModBlocks.ABRAMS_GOLEM_CHEST_BLOCK).build());

    public static void registerBlockEntities() {
        AbramMod.LOGGER.info("Registering Block Entities for " + AbramMod.MOD_ID);
    }
}
