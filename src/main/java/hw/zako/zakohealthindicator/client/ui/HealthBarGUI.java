package hw.zako.zakohealthindicator.client.ui;

import hw.zako.zakohealthindicator.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class HealthBarGUI {

    private static final Random RNG = new Random();

    // Written by game thread
    private final AtomicReference<LivingEntity> pendingTarget = new AtomicReference<>(null);
    private volatile float pendingHp         = 0f;
    private volatile long  pendingAttackTime = -1L;

    // Used only by render thread
    private LivingEntity target      = null;
    private float        hpBeforeHit = 0f;
    private long         attackTime  = -1L;
    private boolean      spawned     = false;

    private String flyText  = "";
    private float  flyX, flyY, flyVx, flyVy;
    private long   flyBirth = -1L;

    // Called from game thread
    public void onAttack(LivingEntity p) {
        pendingHp         = p.getHealth();
        pendingAttackTime = System.currentTimeMillis();
        pendingTarget.set(p);
    }

    // Called from render thread only
    public void render(MatrixStack ms) {
        long now = System.currentTimeMillis();

        // Consume pending attack (handoff from game thread)
        LivingEntity pending = pendingTarget.getAndSet(null);
        if (pending != null) {
            target      = pending;
            hpBeforeHit = pendingHp;
            attackTime  = pendingAttackTime;
            spawned     = false;
            flyBirth    = -1L;
        }

        if (target == null) return;

        Minecraft    mc   = Minecraft.getInstance();
        FontRenderer font = mc.font;
        int cx = mc.getWindow().getGuiScaledWidth()  / 2;
        int cy = mc.getWindow().getGuiScaledHeight() / 2;

        // Poll every frame from 50ms until HP changes, give up at 600ms
        if (!spawned) {
            long since = now - attackTime;
            if (since >= 50L) {
                float dmg = hpBeforeHit - target.getHealth();
                if (dmg > 0f) {
                    flyText = String.format("%.1f", dmg);
                    double angle = RNG.nextDouble() * 2 * Math.PI;
                    float  speed = 100f + RNG.nextFloat() * 80f;
                    flyX    = (RNG.nextFloat() - 0.5f) * 10f;
                    flyY    = (RNG.nextFloat() - 0.5f) * 10f;
                    flyVx   = (float) Math.cos(angle) * speed;
                    flyVy   = (float) Math.sin(angle) * speed;
                    flyBirth = now;
                    spawned  = true;
                } else if (since >= 600L) {
                    spawned = true; // give up
                }
            }
        }

        // Big HP number below crosshair
        {
            long el = now - attackTime;
            if (el >= 0L && el <= 10000L) {
                float hp = target.getHealth();
                if (hp > 0f && hp <= 5f) {
                    String txt   = String.valueOf((int) Math.ceil(hp));
                    float  alpha = el > 9000L ? 1f - (el - 9000f) / 1000f : 1f;
                    int    a     = (int)(alpha * 255) & 0xFF;
                    int    color = (a << 24) | 0xFF3030;
                    float  scale = 4f * Config.get().getBigScale();
                    int    tw    = font.width(txt);

                    ms.pushPose();
                    ms.translate(cx, cy + 35, 0);
                    ms.scale(scale, scale, 1f);
                    font.drawShadow(ms, txt, -tw / 2f, 0, color);
                    ms.popPose();
                }
            }
        }

        // Render flying damage number
        if (flyBirth >= 0L) {
            long age = now - flyBirth;
            if (age <= 1500L) {
                float t     = age / 1000f;
                float alpha = age > 1000L ? 1f - (age - 1000f) / 500f : 1f;
                int   a     = (int)(alpha * 255) & 0xFF;
                int   color = (a << 24) | 0xFF3030;

                float fx    = flyX + flyVx * t;
                float fy    = flyY + flyVy * t;
                float scale = Config.get().getFlyingScale();
                int   tw    = font.width(flyText);

                ms.pushPose();
                ms.translate(cx + fx, cy + fy, 0);
                ms.scale(scale, scale, 1f);
                font.drawShadow(ms, flyText, -tw / 2f, 0, color);
                ms.popPose();
            } else {
                flyBirth = -1L;
            }
        }

    }
}
