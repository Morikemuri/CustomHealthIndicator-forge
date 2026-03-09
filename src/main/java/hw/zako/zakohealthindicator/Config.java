package hw.zako.zakohealthindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

public class Config {
    private static Config inst;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean crosshair   = true;
    private float   flyingScale = 1.0f;
    private float   bigScale    = 1.0f;

    public static Config get() {
        if (inst == null) inst = new Config();
        return inst;
    }

    private Config() {
        File dir = cfgDir();
        if (dir.exists()) load();
    }

    private static File cfgDir() {
        return new File(net.minecraft.client.Minecraft.getInstance().gameDirectory, "config");
    }

    private static File cfgFile() {
        return new File(cfgDir(), "customhealthindicator.json");
    }

    public boolean isCrosshair()    { return crosshair; }
    public float   getFlyingScale() { return flyingScale; }
    public float   getBigScale()    { return bigScale; }

    public void setCrosshair(boolean v) { crosshair = v; save(); }

    public void setFlyingScale(float v) {
        flyingScale = Math.max(0.25f, Math.min(5f, v));
        save();
    }

    public void setBigScale(float v) {
        bigScale = Math.max(0.25f, Math.min(5f, v));
        save();
    }

    public void load() {
        File f = cfgFile();
        if (!f.exists()) return;
        try (FileReader r = new FileReader(f)) {
            JsonObject o = GSON.fromJson(r, JsonObject.class);
            if (o.has("crosshair"))   crosshair   = o.get("crosshair").getAsBoolean();
            if (o.has("flyingScale")) flyingScale = o.get("flyingScale").getAsFloat();
            if (o.has("bigScale"))    bigScale    = o.get("bigScale").getAsFloat();
        } catch (Exception ignored) {}
    }

    public void save() {
        JsonObject o = new JsonObject();
        o.addProperty("crosshair",   crosshair);
        o.addProperty("flyingScale", flyingScale);
        o.addProperty("bigScale",    bigScale);
        try (FileWriter w = new FileWriter(cfgFile())) {
            GSON.toJson(o, w);
        } catch (Exception ignored) {}
    }
}
