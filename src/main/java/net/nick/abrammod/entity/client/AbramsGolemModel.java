package net.nick.abrammod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.nick.abrammod.AbramMod;

public class AbramsGolemModel extends EntityModel<AbramsGolemEntityRenderState> {
    public static final EntityModelLayer ABRAMS_GOLEM = new EntityModelLayer(Identifier.of(AbramMod.MOD_ID, "abrams-golem"), "main");

    private final ModelPart AbramsGolem;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart legs;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart head;

    public AbramsGolemModel(ModelPart root) {
        super(root);
        this.AbramsGolem = root.getChild("AbramsGolem");
        this.body = this.AbramsGolem.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.legs = this.AbramsGolem.getChild("legs");
        this.right_leg = this.legs.getChild("right_leg");
        this.left_leg = this.legs.getChild("left_leg");
        this.head = this.AbramsGolem.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData AbramsGolem = modelPartData.addChild("AbramsGolem", ModelPartBuilder.create(), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData body = AbramsGolem.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -3.0F, -3.0F, 10.0F, 6.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -12.0F, 1.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData right_arm = body.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.of(7.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData arm_right_r1 = right_arm.addChild("arm_right_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-14.0F, -2.0F, -1.0F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData left_arm = body.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.of(-5.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData arm_left_r1 = left_arm.addChild("arm_left_r1", ModelPartBuilder.create().uv(0, 12).cuboid(-14.0F, -2.0F, -1.0F, 15.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        ModelPartData legs = AbramsGolem.addChild("legs", ModelPartBuilder.create(), ModelTransform.of(-2.0F, -9.0F, 1.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(0, 20).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(0, 20).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        ModelPartData head = AbramsGolem.addChild("head", ModelPartBuilder.create().uv(32, 0).cuboid(-3.0F, -0.1212F, -2.4869F, 6.0F, 3.0F, 4.0F, new Dilation(0.0F))
                .uv(34, 14).cuboid(-3.5F, -1.4212F, -2.8869F, 7.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 39).cuboid(-1.6458F, 1.1288F, 0.8131F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -17.877F, 1.4869F, 0.0F, 0.0F, 0.0F));

        ModelPartData nose_r1 = head.addChild("nose_r1", ModelPartBuilder.create().uv(34, 18).cuboid(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-0.6458F, 0.8788F, 1.0131F, -1.1781F, 0.0F, 0.0F));

        ModelPartData hair_bangs_r1 = head.addChild("hair_bangs_r1", ModelPartBuilder.create().uv(15, 36).cuboid(-4.9542F, -2.0F, -1.0F, 6.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(1.9542F, -1.1212F, 0.6131F, -2.618F, 0.0F, 0.0F));

        ModelPartData hair_right_r1 = head.addChild("hair_right_r1", ModelPartBuilder.create().uv(16, 28).cuboid(-0.9974F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(1.9974F, -0.7587F, -0.4869F, 0.0F, 0.0F, 1.7453F));

        ModelPartData hair_left_r1 = head.addChild("hair_left_r1", ModelPartBuilder.create().uv(16, 20).cuboid(-0.8582F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-2.1418F, -0.5891F, -0.4869F, 0.0F, 0.0F, -1.7453F));

        ModelPartData hair_top_r1 = head.addChild("hair_top_r1", ModelPartBuilder.create().uv(0, 35).cuboid(-0.5F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-0.1458F, -1.6212F, -0.4869F, 0.0F, 0.0F, 1.5708F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(AbramsGolemEntityRenderState renderState) {
        super.setAngles(renderState);
        float g = renderState.limbSwingAmplitude;
        float h = renderState.limbSwingAnimationProgress;

        this.head.yaw = renderState.relativeHeadYaw * (float) (Math.PI / 180.0);
        this.head.pitch = renderState.pitch * (float) (Math.PI / 180.0);
        this.right_leg.pitch = -1.5F * MathHelper.wrap(h, 13.0F) * g;
        this.left_leg.pitch = 1.5F * MathHelper.wrap(h, 13.0F) * g;
        this.right_leg.yaw = 0.0F;
        this.left_leg.yaw = 0.0F;
    }

}