package hw.zako.zakohealthindicator;

import hw.zako.zakohealthindicator.client.ClientEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("customhealthindicator")
public class ZakoHealthIndicatorMod {
    public static final String ID = "customhealthindicator";

    public ZakoHealthIndicatorMod() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }
}
