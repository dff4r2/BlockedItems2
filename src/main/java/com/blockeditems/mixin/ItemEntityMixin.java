package com.blockeditems.mixin;

import com.blockeditems.BlockedConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    /** لما اللاعب يلمس الايتم الممنوع: ما ينلقط، ويختفي من الأرض */
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void blockeditems$block(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.level().isClientSide() && BlockedConfig.get().isBlocked(self.getItem())) {
            self.discard();
            ci.cancel();
        }
    }
}
