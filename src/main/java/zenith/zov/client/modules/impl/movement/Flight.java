package zenith.zov.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.math.Vec3d;
import zenith.zov.base.events.impl.player.EventMove;
import zenith.zov.base.events.impl.player.EventUpdate;
import zenith.zov.client.modules.api.Category;
import zenith.zov.client.modules.api.Module;
import zenith.zov.client.modules.api.ModuleAnnotation;
import zenith.zov.client.modules.api.setting.impl.BooleanSetting;
import zenith.zov.client.modules.api.setting.impl.NumberSetting;
import zenith.zov.utility.game.player.MovingUtil;

@ModuleAnnotation(name = "Flight", category = Category.MOVEMENT, description = "Позволяет летать")
public final class Flight extends Module {
    public static final Flight INSTANCE = new Flight();

    private final NumberSetting speed = new NumberSetting("Скорость", 0.7f, 0.1f, 5.0f, 0.1f);
    private final NumberSetting verticalSpeed = new NumberSetting("Скорость Y", 0.5f, 0.1f, 5.0f, 0.1f);
    private final BooleanSetting antiKick = new BooleanSetting("АнтиКик", true);

    private Flight() {
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player == null) {
            return;
        }

        mc.player.fallDistance = 0.0f;
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (mc.player == null) {
            return;
        }

        double y = 0.0;
        if (mc.options.jumpKey.isPressed()) {
            y += verticalSpeed.getCurrent();
        }
        if (mc.options.sneakKey.isPressed()) {
            y -= verticalSpeed.getCurrent();
        }

        if (antiKick.isEnabled() && y == 0.0 && mc.player.age % 20 == 0) {
            y = -0.04;
        }

        double[] direction = MovingUtil.hasPlayerMovement()
                ? MovingUtil.calculateDirection(speed.getCurrent())
                : new double[]{0.0, 0.0};

        event.setMovePos(new Vec3d(direction[0], y, direction[1]));
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.fallDistance = 0.0f;
            mc.player.setVelocity(0.0, 0.0, 0.0);
        }

        super.onDisable();
    }
}
