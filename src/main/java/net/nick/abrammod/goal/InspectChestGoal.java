package net.nick.abrammod.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.entity.custom.AbramsGolemChestBlockEntity;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;

import java.util.EnumSet;

public class InspectChestGoal extends Goal {
    private final AbramsGolemEntity golem;
    private BlockPos targetChest;
    private int inspectionTimer;
    private static final int INSPECTION_DURATION = 60; // 3 seconds

    public InspectChestGoal(AbramsGolemEntity golem) {
        this.golem = golem;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (golem.isInspectingChest()) {
            return false;
        }

        targetChest = golem.getRecentChestClosing();
        if (targetChest == null) {
            return false;
        }

        // Only start if we're close enough to the chest
        double distance = golem.getBlockPos().getSquaredDistance(targetChest);
        return distance <= golem.getBlockInteractionRange();
    }

    @Override
    public boolean shouldContinue() {
        return golem.isInspectingChest() && targetChest != null;
    }

    @Override
    public void start() {
        AbramMod.LOGGER.info("Golem started inspecting chest at " + targetChest);

        golem.setInspectingChest(true);
        inspectionTimer = INSPECTION_DURATION;

        // Open the chest
        World world = golem.getWorld();

        if (world.getBlockEntity(targetChest) instanceof AbramsGolemChestBlockEntity abramsChest) {
            abramsChest.isBeingInspectedByGolem = true;
            abramsChest.openChest(golem);
        }

    }

    @Override
    public void tick() {
        if (targetChest != null) {
            golem.getLookControl().lookAt(targetChest.getX() + 0.5, targetChest.getY() + 0.5, targetChest.getZ() + 0.5);
        }

        inspectionTimer--;
        if (inspectionTimer <= 0) {
            stop();
        }
    }

    @Override
    public void stop() {
        if (targetChest != null) {
            // Analyze chest contents before closing
            golem.analyzeAndUpdateChestContents(targetChest);
            golem.setInspectingChest(false);
            // Close the chest
            World world = golem.getWorld();
            if (world.getBlockEntity(targetChest) instanceof AbramsGolemChestBlockEntity abramsChest) {
                abramsChest.closeChest(golem);
                abramsChest.isBeingInspectedByGolem = false;
            }

            golem.removeChestClosingEvent(targetChest);
            AbramMod.LOGGER.info("Golem finished inspecting chest at " + targetChest);
        }

        targetChest = null;
        inspectionTimer = 0;
    }
}