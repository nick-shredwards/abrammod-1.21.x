package net.nick.abrammod.block.entity.client;

import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.entity.custom.AbramsGolemChestBlockEntity;

public class AbramsGolemChestBlockEntityRenderer extends ChestBlockEntityRenderer<AbramsGolemChestBlockEntity> {

    public AbramsGolemChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
}
