package zenith.zov.utility.render.display;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import zenith.zov.utility.render.display.shader.GlProgram;

public final class RenderLayerUtil {
    private static final ThreadLocal<RenderLayer> CURRENT_LAYER = new ThreadLocal<>();
    private static final ThreadLocal<Identifier> CURRENT_TEXTURE = new ThreadLocal<>();

    private RenderLayerUtil() {
    }

    public static RenderLayer positionColor() {
        return RenderLayers.debugQuads();
    }

    public static RenderLayer positionColorDepth() {
        return RenderLayers.debugQuads();
    }

    public static RenderLayer lines() {
        return RenderLayers.lines();
    }

    public static RenderLayer linesDepthNoWrite() {
        return RenderLayers.linesTranslucent();
    }

    public static RenderLayer guiTextured(Identifier texture) {
        CURRENT_TEXTURE.set(texture);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.getTextureManager().getTexture(texture);
        }
        return RenderLayers.text(texture);
    }

    public static RenderLayer guiTexturedNoTexture() {
        CURRENT_TEXTURE.remove();
        return RenderLayers.debugQuads();
    }

    public static Identifier getCurrentTexture() {
        return CURRENT_TEXTURE.get();
    }

    public static BufferBuilder begin(RenderLayer layer) {
        return Tessellator.getInstance().begin(layer.getDrawMode(), layer.getVertexFormat());
    }

    public static void setCurrentLayer(RenderLayer layer) {
        CURRENT_LAYER.set(layer);
    }

    public static void drawCurrent(BufferBuilder buffer) {
        RenderLayer layer = CURRENT_LAYER.get();
        if (layer == null) {
            layer = positionColor();
        }
        draw(layer, buffer);
    }

    public static void draw(RenderLayer layer, BufferBuilder buffer) {
        BuiltBuffer builtBuffer = buffer.endNullable();
        if (builtBuffer == null) {
            return;
        }
        GlProgram activeProgram = GlProgram.getActive();
        if (activeProgram != null) {
            activeProgram.draw(layer, builtBuffer);
            GlProgram.clearActive();
            return;
        }
        layer.draw(builtBuffer);
    }
}
