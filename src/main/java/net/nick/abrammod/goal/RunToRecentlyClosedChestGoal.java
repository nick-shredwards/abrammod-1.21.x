package net.nick.abrammod.goal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.nick.abrammod.block.ModBlocks;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;

import java.util.EnumSet;

public class RunToRecentlyClosedChestGoal extends Goal{
    private final AbramsGolemEntity mob;
    private final double speed;
    private BlockPos targetChestPos;
    private Path path;
    private int soundCooldown;

    public RunToRecentlyClosedChestGoal(PathAwareEntity mob, double speed) {
        this.mob = (AbramsGolemEntity) mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (soundCooldown > 0) {
            soundCooldown--;
            return false;
        }

        BlockPos recentChest = mob.getRecentChestClosing();
        if (recentChest != null && mob.getWorld().getBlockState(recentChest).isOf(ModBlocks.ABRAMS_GOLEM_CHEST_BLOCK)) {
            this.targetChestPos = recentChest;
            this.path = this.mob.getNavigation().findPathTo(recentChest, 1);
            return this.path != null;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetChestPos != null &&
                !this.mob.isInspectingChest() && // Stop if already inspecting
                this.mob.getBlockPos().getSquaredDistance(this.targetChestPos) > this.mob.getBlockInteractionRange() &&
                this.mob.getWorld().getBlockState(this.targetChestPos).isOf(ModBlocks.ABRAMS_GOLEM_CHEST_BLOCK);
    }

    @Override
    public void start() {
        if (this.path != null) {
            this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        }
        this.soundCooldown = 20; // 1 second cooldown
    }

    @Override
    public void stop() {
        this.targetChestPos = null;
        this.path = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetChestPos != null) {
            this.mob.getLookControl().lookAt(this.targetChestPos.getX(), this.targetChestPos.getY(), this.targetChestPos.getZ());

            // Check if we've reached the chest using the same range as interaction
            if (this.mob.getBlockPos().getSquaredDistance(this.targetChestPos) <= this.mob.getBlockInteractionRange()) {
                this.stop();
                return;
            }

            // Recalculate path if navigation is idle
            if (this.mob.getNavigation().isIdle()) {
                this.path = this.mob.getNavigation().findPathTo(this.targetChestPos, 1);
                if (this.path != null) {
                    this.mob.getNavigation().startMovingAlong(this.path, this.speed);
                }
            }
        }
    }
}
