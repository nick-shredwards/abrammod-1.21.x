package net.nick.abrammod.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;

@Environment(EnvType.CLIENT)
public class AbramsGolemRenderer extends MobEntityRenderer<AbramsGolemEntity, AbramsGolemEntityRenderState, AbramsGolemModel> {

    public AbramsGolemRenderer(EntityRendererFactory.Context context) {
        super(context, new AbramsGolemModel(context.getPart(AbramsGolemModel.ABRAMS_GOLEM)), 0.75f);
    }

    @Override
    public AbramsGolemEntityRenderState createRenderState() {
        return new AbramsGolemEntityRenderState();
    }

    @Override
    public Identifier getTexture(AbramsGolemEntityRenderState state) {
        return Identifier.of(AbramMod.MOD_ID, "textures/entity/abrams-golem/abrams-golem.png");
    }
}
