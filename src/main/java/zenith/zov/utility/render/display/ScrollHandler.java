package zenith.zov.utility.render.display;

import lombok.Getter;
import lombok.Setter;
import zenith.zov.utility.interfaces.IMinecraft;

@Getter @Setter
public class ScrollHandler implements IMinecraft {

    private double max;
    private double value;
    private double targetValue;
    private double speed;
    private static final double SCROLL_SMOOTHNESS = 0.4;
    public static final double SCROLLBAR_THICKNESS = 1;

    public ScrollHandler() {
        this.value = 0;
        this.targetValue = 0;
        this.speed = 8;
    }

    public void update() {
        targetValue = Math.max(Math.min(targetValue, 0), -max);

        double delta = targetValue - value;
        value += delta * SCROLL_SMOOTHNESS;
        value = Math.max(Math.min(value, 0), -max);

        if (Math.abs(delta) < 0.1) {
            value = targetValue;
        }
    }

    public void setMax(double max) {
        this.max = Math.max(0.0, max);
        this.targetValue = Math.max(Math.min(this.targetValue, 0), -this.max);
        this.value = Math.max(Math.min(this.value, 0), -this.max);
    }


    public double getValue() {
        return -value;
    }

    public void scroll(double amount) {
        targetValue += amount * speed;
    }
}
