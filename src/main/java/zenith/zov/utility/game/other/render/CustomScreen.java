package zenith.zov.utility.game.other.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import zenith.zov.utility.game.other.MouseButton;
import zenith.zov.utility.interfaces.IMinecraft;
import zenith.zov.utility.render.display.base.UIContext;
import zenith.zov.utility.render.display.shader.DrawUtil;

public abstract class CustomScreen extends Screen implements IMinecraft {

    protected CustomScreen() {
        super(Text.empty());
    }

    public abstract void render(UIContext context,float mouseX, float mouseY);

    @Override
    public final void render(DrawContext context, int mouseX, int mouseY, float delta) {

       // super.render(context, mouseX, mouseY, delta);

        UIContext uiContext = UIContext.of(context, mouseX, mouseY, delta);

        DrawUtil.beginGui();
        try {
            this.render(uiContext, mouseX, mouseY);
        } finally {
            DrawUtil.endGui();
        }
        super.render(context, mouseX, mouseY, delta);

    }

    @Override
    public final boolean mouseClicked(Click click, boolean playSound) {
        MouseButton mouseButton = MouseButton.fromButtonIndex(click.button());
        this.onMouseClicked(click.x(), click.y(), mouseButton);
        return super.mouseClicked(click, playSound);
    }

    @Override
    public void tick() {}

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public final boolean mouseReleased(Click click) {
        MouseButton mouseButton = MouseButton.fromButtonIndex(click.button());
        this.onMouseReleased(click.x(), click.y(), mouseButton);
        return super.mouseReleased(click);
    }

    @Override
    public final boolean mouseDragged(Click click, double deltaX, double deltaY) {
        MouseButton mouseButton = MouseButton.fromButtonIndex(click.button());
        this.onMouseDragged(click.x(), click.y(), mouseButton, deltaX, deltaY);
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        return this.keyPressed(keyInput.key(), keyInput.scancode(), keyInput.modifiers());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(new KeyInput(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(CharInput charInput) {
        return this.charTyped((char) charInput.codepoint(), charInput.modifiers());
    }

    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(new CharInput(chr, modifiers));
    }

    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.mouseClicked(new Click(mouseX, mouseY, new MouseInput(button, 0)), false);
    }

    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.mouseReleased(new Click(mouseX, mouseY, new MouseInput(button, 0)));
    }

    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.mouseDragged(new Click(mouseX, mouseY, new MouseInput(button, 0)), deltaX, deltaY);
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {}

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {}

    public void onMouseDragged(double mouseX, double mouseY, MouseButton button, double deltaX, double deltaY) {}
}
