package hw.zako.zakohealthindicator.client.ui;

import hw.zako.zakohealthindicator.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class SettingsScreen extends Screen {

    private static final float MIN   = 0.25f;
    private static final float MAX   = 5.0f;
    private static final int   W     = 220;
    private static final int   H     = 20;

    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(new StringTextComponent("Health Indicator Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width  / 2;
        int cy = height / 2;

        addButton(makeSlider(cx - W / 2, cy - 40, "Flying number size",
                Config.get().getFlyingScale(),
                v -> Config.get().setFlyingScale(v)));

        addButton(makeSlider(cx - W / 2, cy - 10, "Low HP number size",
                Config.get().getBigScale(),
                v -> Config.get().setBigScale(v)));

        addButton(new Button(cx - 50, cy + 30, 100, 20,
                new StringTextComponent("Done"),
                btn -> onClose()));
    }

    private AbstractSlider makeSlider(int x, int y, String label,
                                      float init, java.util.function.Consumer<Float> onChange) {
        double norm = (init - MIN) / (MAX - MIN);
        return new AbstractSlider(x, y, W, H, StringTextComponent.EMPTY, norm) {
            @Override
            protected void updateMessage() {
                float v = value();
                setMessage(new StringTextComponent(label + ": " + String.format("%.2f", v) + "x"));
            }

            @Override
            protected void applyValue() {
                onChange.accept(value());
            }

            private float value() {
                return Math.round((MIN + (float) (value * (MAX - MIN))) * 100f) / 100f;
            }
        };
    }

    @Override
    public void render(com.mojang.blaze3d.matrix.MatrixStack ms, int mx, int my, float pt) {
        renderBackground(ms);
        drawCenteredString(ms, font, title, width / 2, height / 2 - 65, 0xFFFFFF);
        super.render(ms, mx, my, pt);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
