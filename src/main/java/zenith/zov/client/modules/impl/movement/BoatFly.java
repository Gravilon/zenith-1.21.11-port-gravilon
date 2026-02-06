package zenith.zov.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zenith.zov.base.events.impl.player.EventUpdate;
import zenith.zov.base.events.impl.server.EventPacket;
import zenith.zov.client.modules.api.Category;
import zenith.zov.client.modules.api.Module;
import zenith.zov.client.modules.api.ModuleAnnotation;
import zenith.zov.client.modules.api.setting.impl.BooleanSetting;
import zenith.zov.client.modules.api.setting.impl.NumberSetting;
import zenith.zov.utility.game.player.MovingUtil;
import zenith.zov.utility.game.player.PlayerIntersectionUtil;

import java.util.Arrays;

@ModuleAnnotation(name = "BoatFly", category = Category.MOVEMENT, description = "Флай на лодке с пакетным обходом")
public final class BoatFly extends Module {
    public static final BoatFly INSTANCE = new BoatFly();

    private final NumberSetting speed = new NumberSetting("Скорость XZ", 1.6f, 0.2f, 6.0f, 0.1f);
    private final NumberSetting verticalSpeed = new NumberSetting("Скорость Y", 0.6f, 0.1f, 3.0f, 0.1f);
    private final NumberSetting packetAmount = new NumberSetting("Пакеты", 2.0f, 1.0f, 5.0f, 1.0f);
    private final BooleanSetting antiKick = new BooleanSetting("АнтиКик", true);
    private final BooleanSetting packetBypass = new BooleanSetting("Пакетный обход", true);

    private static final double[] SPEED_PATTERN = {1.0, 0.94, 1.07, 0.97};
    private static final double[] Y_PATTERN = {0.0, 0.0625, -0.03125, 0.0390625, -0.015625, 0.0};
    private static final boolean[][] PADDLE_PATTERN = {
            {true, false},
            {true, true},
            {false, true},
            {false, false}
    };

    private final Vec3d[] spoofPositions = new Vec3d[12];

    private BoatEntity controlledBoat;
    private int speedPatternIndex;
    private int yPatternIndex;
    private int paddlePatternIndex;
    private int spoofIndex;
    private int antiKickTicks;

    private BoatFly() {
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (!(mc.player.getVehicle() instanceof BoatEntity boat)) {
            if (controlledBoat != null) {
                releaseBoat(false);
                resetState();
            }
            return;
        }

        controlBoat(boat);
        antiKickTicks++;

        double speedMultiplier = SPEED_PATTERN[speedPatternIndex++ % SPEED_PATTERN.length];
        double[] direction = MovingUtil.hasPlayerMovement()
                ? MovingUtil.calculateDirection(speed.getCurrent() * speedMultiplier)
                : new double[]{0.0, 0.0};

        double yMotion = getVerticalMotion();
        boat.setVelocity(direction[0], yMotion, direction[1]);
        boat.setYaw(mc.player.getYaw());
        boat.setPitch(0.0f);
        boat.fallDistance = 0.0f;
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (!event.isSent() || !packetBypass.isEnabled() || mc.player == null) {
            return;
        }

        if (!(mc.player.getVehicle() instanceof BoatEntity boat)) {
            return;
        }

        switch (event.getPacket()) {
            case VehicleMoveC2SPacket ignored -> {
                event.cancel();
                sendSpoofedVehiclePackets(boat);
            }
            case BoatPaddleStateC2SPacket ignored -> {
                event.cancel();
                sendSpoofedPaddlePacket();
            }
            default -> {
            }
        }
    }

    private void controlBoat(BoatEntity boat) {
        if (controlledBoat == boat) {
            if (!boat.hasNoGravity()) {
                boat.setNoGravity(true);
            }
            return;
        }

        releaseBoat(true);
        controlledBoat = boat;
        controlledBoat.setNoGravity(true);
    }

    private void releaseBoat(boolean keepVelocity) {
        if (controlledBoat == null) {
            return;
        }

        if (controlledBoat.isAlive()) {
            controlledBoat.setNoGravity(false);
            if (!keepVelocity) {
                Vec3d velocity = controlledBoat.getVelocity();
                controlledBoat.setVelocity(velocity.x * 0.4, velocity.y, velocity.z * 0.4);
            }
        }

        controlledBoat = null;
    }

    private double getVerticalMotion() {
        if (mc.options.jumpKey.isPressed()) {
            return verticalSpeed.getCurrent();
        }
        if (mc.options.sneakKey.isPressed()) {
            return -verticalSpeed.getCurrent();
        }

        if (antiKick.isEnabled() && antiKickTicks % 14 == 0) {
            double kick = Y_PATTERN[yPatternIndex++ % Y_PATTERN.length];
            return kick > 0.0 ? -kick * 0.5 : kick;
        }

        return 0.0;
    }

    private void sendSpoofedVehiclePackets(BoatEntity boat) {
        int amount = Math.max(1, Math.round(packetAmount.getCurrent()));
        Vec3d basePos = boat.getEntityPos();
        float yaw = boat.getYaw();
        float pitch = boat.getPitch();

        double correction = getSpoofCorrection(basePos);
        for (int i = 0; i < amount; i++) {
            double offset = Y_PATTERN[(yPatternIndex + i) % Y_PATTERN.length] + correction;
            Vec3d spoofedPos = basePos.add(0.0, offset, 0.0);

            spoofPositions[spoofIndex] = spoofedPos;
            spoofIndex = (spoofIndex + 1) % spoofPositions.length;

            PlayerIntersectionUtil.sendPacketWithOutEvent(new VehicleMoveC2SPacket(spoofedPos, yaw, pitch, false));
            PlayerIntersectionUtil.sendPacketWithOutEvent(new VehicleMoveC2SPacket(basePos, yaw, pitch, false));
        }

        yPatternIndex = (yPatternIndex + amount) % Y_PATTERN.length;
    }

    private double getSpoofCorrection(Vec3d basePos) {
        Vec3d lastSpoofed = spoofPositions[(spoofIndex - 1 + spoofPositions.length) % spoofPositions.length];
        if (lastSpoofed == null) {
            return 0.0;
        }

        double deltaY = basePos.getY() - lastSpoofed.getY();
        return MathHelper.clamp(deltaY * 0.25, -0.02, 0.02);
    }

    private void sendSpoofedPaddlePacket() {
        boolean[] state = MovingUtil.hasPlayerMovement()
                ? PADDLE_PATTERN[paddlePatternIndex++ % PADDLE_PATTERN.length]
                : PADDLE_PATTERN[PADDLE_PATTERN.length - 1];

        PlayerIntersectionUtil.sendPacketWithOutEvent(new BoatPaddleStateC2SPacket(state[0], state[1]));
    }

    private void resetState() {
        speedPatternIndex = 0;
        yPatternIndex = 0;
        paddlePatternIndex = 0;
        spoofIndex = 0;
        antiKickTicks = 0;
        Arrays.fill(spoofPositions, null);
    }

    @Override
    public void onEnable() {
        resetState();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        releaseBoat(false);
        resetState();
        super.onDisable();
    }
}
