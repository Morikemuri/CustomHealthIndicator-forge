package hw.zako.zakohealthindicator;

import hw.zako.zakohealthindicator.client.ClientEvents;
import hw.zako.zakohealthindicator.client.KeyBindings;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("customhealthindicator")
public class ZakoHealthIndicatorMod {
    public static final String ID = "customhealthindicator";

    public ZakoHealthIndicatorMod() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KeyBindings.OPEN_SETTINGS);
    }
}
