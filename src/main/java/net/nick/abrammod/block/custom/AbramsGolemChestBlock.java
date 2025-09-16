package net.nick.abrammod.block.custom;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import net.nick.abrammod.block.entity.ModBlockEntities;
import net.nick.abrammod.block.entity.custom.AbramsGolemChestBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AbramsGolemChestBlock extends AbstractChestBlock<AbramsGolemChestBlockEntity> {

    public AbramsGolemChestBlock(Settings settings, Supplier<BlockEntityType<? extends AbramsGolemChestBlockEntity>> entityTypeRetriever) {
        super(settings, entityTypeRetriever);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, false));
    }

    public static final MapCodec<AbramsGolemChestBlock> CODEC = createCodec(
            settings -> new AbramsGolemChestBlock(settings, () -> ModBlockEntities.ABRAMS_GOLEM_CHEST_BE)
    );
    
    @Override
    protected MapCodec<? extends AbstractChestBlock<AbramsGolemChestBlockEntity>> getCodec() {
        return CODEC;
    }

    @Override
    public DoubleBlockProperties.PropertySource<? extends AbramsGolemChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return DoubleBlockProperties.toPropertySource(
                this.entityTypeRetriever.get(),
                ChestBlock::getDoubleBlockType,
                ChestBlock::getFacing,
                ChestBlock.FACING,
                state,
                world,
                pos,
                (worldx, posx) -> isChestBlocked(worldx, posx)
        );
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AbramsGolemChestBlockEntity(pos, state);
    }

    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final int field_31057 = 1;
    private static final VoxelShape SINGLE_SHAPE = Block.createColumnShape(14.0, 0.0, 14.0);
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(
            Block.createCuboidZShape(14.0, 0.0, 14.0, 0.0, 15.0)
    );
    private static final DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Optional<Inventory>>() {
        public Optional<Inventory> getFromBoth(AbramsGolemChestBlockEntity chestBlockEntity, AbramsGolemChestBlockEntity chestBlockEntity2) {
            return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
        }

        public Optional<Inventory> getFrom(AbramsGolemChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        public Optional<Inventory> getFallback() {
            return Optional.empty();
        }
    };
    private static final DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Optional<NamedScreenHandlerFactory>>() {
        public Optional<NamedScreenHandlerFactory> getFromBoth(AbramsGolemChestBlockEntity chestBlockEntity, AbramsGolemChestBlockEntity chestBlockEntity2) {
            final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
            return Optional.of(new NamedScreenHandlerFactory() {
                @Nullable
                @Override
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                        chestBlockEntity.generateLoot(playerInventory.player);
                        chestBlockEntity2.generateLoot(playerInventory.player);
                        return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                    } else {
                        return null;
                    }
                }

                @Override
                public Text getDisplayName() {
                    if (chestBlockEntity.hasCustomName()) {
                        return chestBlockEntity.getDisplayName();
                    } else {
                        return (Text)(chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : Text.translatable("container.abrammod.large_chest"));
                    }
                }
            });
        }

        public Optional<NamedScreenHandlerFactory> getFrom(AbramsGolemChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };

    public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
        ChestType chestType = state.get(CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return DoubleBlockProperties.Type.SINGLE;
        } else {
            return chestType == ChestType.RIGHT ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        if ((Boolean)state.get(WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (neighborState.isOf(this) && direction.getAxis().isHorizontal()) {
            ChestType chestType = neighborState.get(CHEST_TYPE);
            if (state.get(CHEST_TYPE) == ChestType.SINGLE
                    && chestType != ChestType.SINGLE
                    && state.get(FACING) == neighborState.get(FACING)
                    && getFacing(neighborState) == direction.getOpposite()) {
                return state.with(CHEST_TYPE, chestType.getOpposite());
            }
        } else if (getFacing(state) == direction) {
            return state.with(CHEST_TYPE, ChestType.SINGLE);
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch ((ChestType)state.get(CHEST_TYPE)) {
            case SINGLE -> SINGLE_SHAPE;
            case LEFT, RIGHT -> (VoxelShape)DOUBLE_SHAPES_BY_DIRECTION.get(getFacing(state));
        };
    }

    public static Direction getFacing(BlockState state) {
        Direction direction = state.get(FACING);
        return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();
        Direction direction2 = ctx.getSide();
        if (direction2.getAxis().isHorizontal() && bl) {
            Direction direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite());
            if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
                direction = direction3;
                chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chestType == ChestType.SINGLE && !bl) {
            if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
                chestType = ChestType.RIGHT;
            }
        }

        return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        return blockState.isOf(this) && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld serverWorld) {
            NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (namedScreenHandlerFactory != null) {
                player.openHandledScreen(namedScreenHandlerFactory);
                player.incrementStat(this.getOpenStat());
                PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
            }
        }

        return ActionResult.SUCCESS;
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends AbramsGolemChestBlockEntity> getExpectedEntityType() {
        return (BlockEntityType<? extends AbramsGolemChestBlockEntity>)this.entityTypeRetriever.get();
    }

    @Nullable
    public static Inventory getInventory(AbramsGolemChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return (Inventory)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER).orElse(null);
    }

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return (NamedScreenHandlerFactory)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
    }

    public static DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(LidOpenable progress) {
        return new DoubleBlockProperties.PropertyRetriever<AbramsGolemChestBlockEntity, Float2FloatFunction>() {
            public Float2FloatFunction getFromBoth(AbramsGolemChestBlockEntity chestBlockEntity, AbramsGolemChestBlockEntity chestBlockEntity2) {
                return tickProgress -> Math.max(chestBlockEntity.getAnimationProgress(tickProgress), chestBlockEntity2.getAnimationProgress(tickProgress));
            }

            public Float2FloatFunction getFrom(AbramsGolemChestBlockEntity chestBlockEntity) {
                return chestBlockEntity::getAnimationProgress;
            }

            public Float2FloatFunction getFallback() {
                return progress::getAnimationProgress;
            }
        };
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? validateTicker(type, this.getExpectedEntityType(), AbramsGolemChestBlockEntity::clientTick) : null;
    }

    public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
        return hasBlockOnTop(world, pos) || hasCatOnTop(world, pos);
    }

    private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    }

    private static boolean hasCatOnTop(WorldAccess world, BlockPos pos) {
        List<CatEntity> list = world.getNonSpectatingEntities(
                CatEntity.class, new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)
        );
        if (!list.isEmpty()) {
            for (CatEntity catEntity : list) {
                if (catEntity.isInSittingPose()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbramsGolemChestBlockEntity) {
            ((AbramsGolemChestBlockEntity)blockEntity).onScheduledTick();
        }
    }
}
