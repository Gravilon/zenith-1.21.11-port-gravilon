package zenith.zov.utility.mixin.minecraft.render;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zenith.zov.client.modules.impl.render.EntityESP;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "renderLabelIfPresent", at = @At(value = "HEAD"), cancellable = true)
    public void render(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue commandQueue, CameraRenderState cameraRenderState, CallbackInfo ci){
        if(EntityESP.INSTANCE.isRenderName()){
            ci.cancel();
        }
    }
}
