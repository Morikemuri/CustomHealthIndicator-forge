package hw.zako.zakohealthindicator.mixin;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    /**
     * Return null from getDisplayObjective so the scoreboard block in
     * PlayerRenderer.renderNameTag is skipped entirely — no text under the nameplate.
     */
    @Redirect(
        method = "renderNameTag",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/scoreboard/Scoreboard;getDisplayObjective(I)Lnet/minecraft/scoreboard/ScoreObjective;"
        )
    )
    private ScoreObjective suppressScoreboardBelowName(Scoreboard scoreboard, int slot) {
        return null;
    }
}
