package hw.zako.zakohealthindicator.client;

import hw.zako.zakohealthindicator.client.ui.HealthBarGUI;
import hw.zako.zakohealthindicator.client.ui.SettingsScreen;
import hw.zako.zakohealthindicator.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;

public class ClientEvents {

    private static final char             HEART_CHAR = '\uE001';
    private static final ResourceLocation FONT_ID    =
            new ResourceLocation("customhealthindicator", "icons");

    private static final HealthBarGUI hud = new HealthBarGUI();

    @SubscribeEvent
    public void onNameplate(RenderNameplateEvent e) {
        // Suppress integer-only nametags on non-player entities (server HP armor stands)
        if (e.getEntity().getType() != EntityType.PLAYER) {
            String text = e.getContent().getString().trim();
            try {
                int val = Integer.parseInt(text);
                if (val >= 0 && val <= 40) {
                    e.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            } catch (NumberFormatException ignored) {}
            return;
        }
        PlayerEntity p = (PlayerEntity) e.getEntity();

        float hp    = p.getHealth();
        float maxHp = p.getMaxHealth();
        float pct   = maxHp > 0 ? hp / maxHp : 0f;

        Color color = hp < 5f
                ? Color.fromRgb(ColorUtil.animatedColor(pct))
                : Color.fromLegacyFormat(ColorUtil.getColor(pct));

        ITextComponent numPart = new StringTextComponent(" " + String.format("%.1f", hp))
                .withStyle(style -> style.withColor(color));

        ITextComponent heartPart = new StringTextComponent(String.valueOf(HEART_CHAR))
                .withStyle(style -> style.withFont(FONT_ID).withColor(color));

        e.setContent(new StringTextComponent("")
                .append(e.getContent())
                .append(numPart)
                .append(heartPart));
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        if (!(e.getTarget() instanceof LivingEntity)) return;
        hud.onAttack((LivingEntity) e.getTarget());
    }

    @SubscribeEvent
    public void onHudRender(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        hud.render(e.getMatrixStack());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && KeyBindings.OPEN_SETTINGS.consumeClick()) {
            mc.setScreen(new SettingsScreen(null));
        }
    }

}
