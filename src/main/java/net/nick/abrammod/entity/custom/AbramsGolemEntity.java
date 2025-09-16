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
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.ModBlocks;
import net.nick.abrammod.goal.InspectChestGoal;
import net.nick.abrammod.goal.RunToRecentlyClosedChestGoal;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;

public class AbramsGolemEntity extends GolemEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private boolean isInspectingChest = false;

    // Chest opening tracking
    private final Queue<ChestCloseEvent> recentChestClosings = new LinkedList<>();
    private static final int MAX_TRACKED_EVENTS = 10;

    private final Map<BlockPos, ItemStack> chestItemMap = new HashMap<>();

    public AbramsGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAbramGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 15)
                .add(EntityAttributes.ATTACK_DAMAGE, 0.2f)
                .add(EntityAttributes.MOVEMENT_SPEED, .5)
                .add(EntityAttributes.BLOCK_INTERACTION_RANGE, 4.5)
                .add(EntityAttributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initGoals() {
        //this.goalSelector.add(0, new JumpGoal(this, 10));
        this.goalSelector.add(0, new InspectChestGoal(this));
        this.goalSelector.add(1, new RunToRecentlyClosedChestGoal(this, 0.7));
        this.goalSelector.add(2, new SwimGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, .2, true));
        this.goalSelector.add(4, new WanderNearTargetGoal(this, 0.1, 32.0F));
        this.goalSelector.add(5, new WanderAroundPointOfInterestGoal(this, 0.1, false));
        this.goalSelector.add(6, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    public void onChestClosed(BlockPos chestPos) {
        // Add new chest opening event
        recentChestClosings.offer(new ChestCloseEvent(chestPos, this.age));

        AbramMod.LOGGER.info("Chest closed at " + chestPos + ". Total tracked events: " + recentChestClosings.size());

        // Remove oldest events if queue is full
        while (recentChestClosings.size() > MAX_TRACKED_EVENTS) {
            recentChestClosings.poll();
        }
    }

    public void removeChestClosingEvent(BlockPos chestPos) {
        recentChestClosings.removeIf(event -> event.position.equals(chestPos));
        AbramMod.LOGGER.info("Removed chest closing event at " + chestPos + ". Remaining events: " + recentChestClosings.size());
    }

    public BlockPos getRecentChestClosing() {
        // Remove events older than 2 minutes (2400 ticks)
        int currentAge = this.age;
        recentChestClosings.removeIf(event -> currentAge - event.timestamp > 2400);
        recentChestClosings.removeIf(event -> {
            if (!this.getWorld().getBlockState(event.position).isOf(ModBlocks.ABRAMS_GOLEM_CHEST_BLOCK)) {
                AbramMod.LOGGER.info("Chest at " + event.position + " is no longer valid. Removing event.");
                return true;
            }
            return false;
        });

        BlockPos closestChest = null;
        double closestDistance = Double.MAX_VALUE;

        for (ChestCloseEvent event : recentChestClosings) {
            double distance = this.getBlockPos().getSquaredDistance(event.position);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestChest = event.position;
            }
        }

        return closestChest;
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

    public double getBlockInteractionRange() {
        return this.getAttributeValue(EntityAttributes.BLOCK_INTERACTION_RANGE);
    }

    public boolean isInspectingChest() {
        return isInspectingChest;
    }

    public void setInspectingChest(boolean inspectingChest) {
        isInspectingChest = inspectingChest;
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