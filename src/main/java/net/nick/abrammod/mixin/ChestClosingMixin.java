package net.nick.abrammod.mixin;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.nick.abrammod.entity.custom.AbramsGolemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public class ChestClosingMixin {
    @Inject(at = @At("HEAD"), method = "onClose")
    private void onChestOpen(PlayerEntity player, CallbackInfo ci) {
        ChestBlockEntity chest = (ChestBlockEntity) (Object) this;

        // Only run on server side
        if (chest.getWorld() != null && !chest.getWorld().isClient()) {
            // Find all Abrams Golems within hearing range (16 blocks)
            Box searchBox = Box.of(Vec3d.of(chest.getPos()), 32, 32, 32); // 16 block radius

            chest.getWorld()
                    .getEntitiesByClass(AbramsGolemEntity.class, searchBox, golem -> true)
                    .stream()
                    .min((golem1, golem2) -> Double.compare(
                            golem1.squaredDistanceTo(Vec3d.of(chest.getPos())),
                            golem2.squaredDistanceTo(Vec3d.of(chest.getPos()))
                    )).ifPresent(closestGolem -> closestGolem.onChestClosed(chest.getPos()));

        }
    }
}
