package net.nick.abrammod.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;

public class JumpGoal extends Goal {
    private final PathAwareEntity mob;
    private int jumpCooldown;
    private final int jumpInterval;

    public JumpGoal(PathAwareEntity mob) {
        this(mob, 20); // Default 20 ticks (1 second) between jumps
    }

    public JumpGoal(PathAwareEntity mob, int jumpInterval) {
        this.mob = mob;
        this.jumpInterval = jumpInterval;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return this.mob.isOnGround();
    }

    @Override
    public void start() {
        this.jumpCooldown = 0;
    }

    @Override
    public boolean shouldContinue() {
        return true; // Continue jumping indefinitely
    }

    @Override
    public void tick() {
        if (this.jumpCooldown <= 0 && this.mob.isOnGround()) {
            this.mob.jump();
            this.jumpCooldown = this.jumpInterval;
        }

        if (this.jumpCooldown > 0) {
            this.jumpCooldown--;
        }
    }
}
