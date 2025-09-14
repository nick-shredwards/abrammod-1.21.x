package net.nick.abrammod.entity.custom;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nick.abrammod.goal.RunToRecentlyClosedChestGoal;

import java.util.LinkedList;
import java.util.Queue;

public class AbramsGolemEntity extends GolemEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    // Chest opening tracking
    private final Queue<ChestCloseEvent> recentChestOpenings = new LinkedList<>();
    private static final int MAX_TRACKED_EVENTS = 10;

    public AbramsGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAbramGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 15)
                .add(EntityAttributes.ATTACK_DAMAGE, 0.2f)
                .add(EntityAttributes.MOVEMENT_SPEED, .5)
                .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(0, new JumpGoal(this, 10));
        this.goalSelector.add(0, new RunToRecentlyClosedChestGoal(this, 0.7, 16));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, .2, true));
        this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.1, 32.0F));
        this.goalSelector.add(4, new WanderAroundPointOfInterestGoal(this, 0.1, false));
        this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    public void onChestClosed(BlockPos chestPos) {
        // Add new chest opening event
        recentChestOpenings.offer(new ChestCloseEvent(chestPos, this.age));

        // Remove oldest events if queue is full
        while (recentChestOpenings.size() > MAX_TRACKED_EVENTS) {
            recentChestOpenings.poll();
        }
    }

    public BlockPos getRecentChestClosing(int hearingRange) {
        // Find the most recent chest opening within hearing range
        for (ChestCloseEvent event : recentChestOpenings) {
            double distance = this.getBlockPos().getSquaredDistance(event.position);
            if (distance <= hearingRange * hearingRange) {
                return event.position;
            }
        }
        return null;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    // Inner class to store chest opening events
    public static class ChestCloseEvent {
        public final BlockPos position;
        public final int timestamp;

        public ChestCloseEvent(BlockPos position, int timestamp) {
            this.position = position;
            this.timestamp = timestamp;
        }
    }
}