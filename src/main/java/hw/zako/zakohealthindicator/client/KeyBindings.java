package hw.zako.zakohealthindicator.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyBinding OPEN_SETTINGS = new KeyBinding(
            "key.customhealthindicator.open_settings",
            KeyConflictContext.IN_GAME,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.customhealthindicator"
    );
}
