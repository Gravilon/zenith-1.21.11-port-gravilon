package zenith.zov.utility.render.display.shader;

import net.minecraft.client.gl.Framebuffer;
import zenith.zov.utility.interfaces.IWindow;

public class CustomRenderTarget extends Framebuffer implements IWindow {
    private float clearR;
    private float clearG;
    private float clearB;
    private float clearA;

    public CustomRenderTarget(boolean useDepth) {
        super("zenith_custom", useDepth);
    }

    public CustomRenderTarget(int width, int height, boolean useDepth) {
        super("zenith_custom", useDepth);
        this.resize(width, height);
    }

    public CustomRenderTarget setLinear() {
        return this;
    }

    public void setClearColor(float r, float g, float b, float a) {
        this.clearR = r;
        this.clearG = g;
        this.clearB = b;
        this.clearA = a;
    }

    private void resizeFramebuffer() {
        if (this.needsNewFramebuffer()) {
            this.initFbo(Math.max(mw.getWidth(), 1), Math.max(mw.getHeight(), 1));
        }
    }

    public void setup(boolean clear) {
        this.resizeFramebuffer();
    }

    public void setup() {
        setup(true);
    }

    public void stop() {
    }

    public void beginRead() {
    }

    public void endRead() {
    }

    private boolean needsNewFramebuffer() {
        return this.textureWidth != mw.getWidth() || this.textureHeight != mw.getHeight();
    }
}
