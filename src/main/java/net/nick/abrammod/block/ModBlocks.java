package net.nick.abrammod.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.custom.AbramsGolemChestBlock;
import net.nick.abrammod.block.entity.ModBlockEntities;

import java.util.function.Function;


public class ModBlocks {
    public static final Block HYPER_COPPER_BLOCK = registerBlock("hyper_copper_block",
            AbstractBlock.Settings.create().strength(8.0f, 8.0f)
                    .requiresTool().sounds(BlockSoundGroup.COPPER)
                    .luminance(state -> 10).mapColor(MapColor.ORANGE));
    public static final Block ABRAMS_GOLEM_CHEST_BLOCK = registerBlock("abrams_golem_chest_block",
            properties -> new AbramsGolemChestBlock(properties.strength(4.0f).sounds(BlockSoundGroup.WOOD).nonOpaque(),
                    () -> ModBlockEntities.ABRAMS_GOLEM_CHEST_BE));

    public static void registerModBlocks() {
        AbramMod.LOGGER.info("Registering ModBlocks for " + AbramMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(HYPER_COPPER_BLOCK);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INVENTORY).register(entries -> {
            entries.add(ABRAMS_GOLEM_CHEST_BLOCK);
        });
    }

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(AbramMod.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(AbramMod.MOD_ID, name), toRegister);
    }

    private static Block registerBlock(String name, AbstractBlock.Settings blockSettings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(AbramMod.MOD_ID, name));
        Block block = new Block(blockSettings.registryKey(key));
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, key, block);
    }

    private static void registerBlockItem(String name, Block block) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(AbramMod.MOD_ID, name));
        BlockItem item = new BlockItem(block, new Item.Settings().registryKey(key));
        Registry.register(Registries.ITEM, key, item);
    }
}
