package me.alini.banitems.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.alini.banitems.Config;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At("HEAD"))
    private void renderItemOverlay(ItemStack stack,
                                   ItemDisplayContext transformType,
                                   boolean leftHand,
                                   PoseStack poseStack,
                                   MultiBufferSource buffer,
                                   int light,
                                   int overlay,
                                   BakedModel model,
                                   CallbackInfo ci) {

        boolean banned = Config.softBanItems.values().stream()
                .anyMatch(ban -> ItemStack.isSameItemSameTags(stack, ban.toStack()));

        if (!banned) return;

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float a = 0.53f, r = 0f, g = 0f, b = 0f;
        buf.vertex(matrix, 0f, 16f, 0f).color(r, g, b, a).endVertex();
        buf.vertex(matrix, 16f, 16f, 0f).color(r, g, b, a).endVertex();
        buf.vertex(matrix, 16f, 0f, 0f).color(r, g, b, a).endVertex();
        buf.vertex(matrix, 0f, 0f, 0f).color(r, g, b, a).endVertex();

        tess.end();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}