package me.alini.banitems.mixin;

import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import me.alini.banitems.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 通过 Accessor 获取 player 字段
// 注意：这里的 InscriptionTableMenuAccessor 是一个 Mixin 接口，用于访问 InscriptionTableMenu 中的 player 字段
@Mixin(InscriptionTableMenu.class)
public class InscriptionTableMenuMixin {

    @Inject(
            method = "doInscription(I)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false // ⚠️ 关键点：不要映射
    )
    private void interceptDoInscription(int selectedIndex, CallbackInfo ci) {
        InscriptionTableMenu menu = (InscriptionTableMenu) (Object) this;
        ItemStack scrollItemStack = menu.getScrollSlot().getItem();

        // 通过 Accessor 获取 player 字段
        var player = ((InscriptionTableMenuAccessor) menu).getPlayer();

        if (Config.isSoftBanned(scrollItemStack)) {
            // 判断是否为OP，如果是OP则不进行封禁
            if (player.isCreative() || player.hasPermissions(4)) {
                if (player instanceof ServerPlayer sp)
                    sp.sendSystemMessage(Component.literal("§c该法术已被软封禁，但你是OP玩家，可以使用。"));
                return;
            }
            if (player instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal("§c该法术已被软封禁，无法抄录！"));
            }
            ci.cancel();
        }
    }
}
// 通过 Mixin 注入到 InscriptionTableMenu 的 doInscription 方法中
