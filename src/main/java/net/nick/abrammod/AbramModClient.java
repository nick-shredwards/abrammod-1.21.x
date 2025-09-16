package net.nick.abrammod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.nick.abrammod.block.entity.ModBlockEntities;
import net.nick.abrammod.block.entity.client.AbramsGolemChestBlockEntityRenderer;
import net.nick.abrammod.entity.ModEntities;
import net.nick.abrammod.entity.client.AbramsGolemModel;
import net.nick.abrammod.entity.client.AbramsGolemRenderer;

public class AbramModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(AbramsGolemModel.ABRAMS_GOLEM, AbramsGolemModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ABRAMS_GOLEM, AbramsGolemRenderer::new);
        
        BlockEntityRendererFactories.register(ModBlockEntities.ABRAMS_GOLEM_CHEST_BE, AbramsGolemChestBlockEntityRenderer::new);
    }
}
