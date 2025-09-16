package net.nick.abrammod.block.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.nick.abrammod.AbramMod;
import net.nick.abrammod.block.entity.ModBlockEntities;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;
import net.nick.abrammod.util.AbramsGolemViewerCountManager;

public class AbramsGolemChestBlockEntity extends ChestBlockEntity {

    private static final int VIEWER_COUNT_UPDATE_EVENT_TYPE = 1;
    public boolean isBeingInspectedByGolem = false;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private final AbramsGolemViewerCountManager stateManager = new AbramsGolemViewerCountManager() {
        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            AbramsGolemChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            AbramsGolemChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            AbramsGolemChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
        }

        @Override
        protected boolean isEntityViewing(Entity entity, BlockPos box) {
            if (entity instanceof PlayerEntity player) {
                if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                    return false;
                } else {
                    Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                    return inventory == AbramsGolemChestBlockEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory) inventory).isPart(AbramsGolemChestBlockEntity.this);
                }
            }
            return isBeingInspectedByGolem;
        }
    };
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();

    protected AbramsGolemChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public AbramsGolemChestBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.ABRAMS_GOLEM_CHEST_BE, pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.abrammod.chest");
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(view)) {
            Inventories.readData(view, this.inventory);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData(view, this.inventory);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, AbramsGolemChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        if (chestType != ChestType.LEFT) {
            double d = pos.getX() + 0.5;
            double e = pos.getY() + 0.5;
            double f = pos.getZ() + 0.5;
            if (chestType == ChestType.RIGHT) {
                Direction direction = ChestBlock.getFacing(state);
                d += direction.getOffsetX() * 0.5;
                f += direction.getOffsetZ() * 0.5;
            }

            world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
        AbramMod.LOGGER.info("Chest opened by " + player.getName().getString() + ". Viewer count: " + this.stateManager.getViewerCount());
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
        AbramMod.LOGGER.info("Chest closed by " + player.getName().getString() + ". Viewer count: " + this.stateManager.getViewerCount());
    }

    public void openChest(AbramsGolemEntity abrams_golem) {
        if (!this.removed) {
            this.stateManager.openContainer(abrams_golem, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void closeChest(AbramsGolemEntity abrams_golem) {
        if (!this.removed) {
            this.stateManager.closeContainer(abrams_golem, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    public float getAnimationProgress(float tickProgress) {
        return this.lidAnimator.getProgress(tickProgress);
    }

    public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbramsGolemChestBlockEntity) {
                return ((AbramsGolemChestBlockEntity)blockEntity).stateManager.getViewerCount();
            }
        }

        return 0;
    }

    public static void copyInventory(AbramsGolemChestBlockEntity from, AbramsGolemChestBlockEntity to) {
        DefaultedList<ItemStack> defaultedList = from.getHeldStacks();
        from.setHeldStacks(to.getHeldStacks());
        to.setHeldStacks(defaultedList);
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }
}
