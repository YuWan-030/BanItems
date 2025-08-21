package me.alini.banitems.mixin;

import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InscriptionTableMenu.class)
public interface InscriptionTableMenuAccessor {
    @Accessor("player")
    Player getPlayer();
}