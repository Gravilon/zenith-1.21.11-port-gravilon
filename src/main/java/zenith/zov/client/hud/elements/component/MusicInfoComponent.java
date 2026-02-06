package zenith.zov.client.hud.elements.component;

import zenith.zov.client.hud.elements.draggable.DraggableHudElement;
import zenith.zov.utility.render.display.base.CustomDrawContext;

public class MusicInfoComponent extends DraggableHudElement {

    public MusicInfoComponent(String name, float initialX, float initialY, float windowWidth, float windowHeight, float offsetX, float offsetY, Align align) {
        super(name, initialX, initialY, windowWidth, windowHeight, offsetX, offsetY, align);
    }

    @Override
    public void render(CustomDrawContext ctx) {
        // Temporary stub: the external media player integration is disabled.
        this.width = 0.0f;
        this.height = 0.0f;
    }
}
