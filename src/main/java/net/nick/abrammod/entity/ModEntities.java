package net.nick.abrammod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;

public class ModEntities {
    public static final EntityType<AbramsGolemEntity> ABRAMS_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AbramMod.MOD_ID, "abrams-golem"),
            EntityType.Builder.create(AbramsGolemEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1f, 2.5f)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(AbramMod.MOD_ID, "abrams-golem")))
    );

    public static void registerModEntities() {
        AbramMod.LOGGER.info("Registering Mod Entities for " + AbramMod.MOD_ID);
    }
}