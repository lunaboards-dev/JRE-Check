package net.shadowkat.minecraft.jrecheck.confighandlers;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.Minecraft;
import net.shadowkat.minecraft.jrecheck.JRECheck;
import net.shadowkat.minecraft.jrecheck.Utils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class MicrosoftConfigHandler implements IConfigHandler {

    @Override
    public boolean matchesLauncher(String launcher) {
        return launcher.equals("minecraft-launcher");
    }

    @Override
    public File getLauncherConfig() {
        File gd = Minecraft.getMinecraft().gameDir;
        if (!gd.getName().equals(".minecraft") && !gd.getName().equals("minecraft")) {
            // Idk, assume we're at ~/.minecraft
            switch (Utils.getOS()) {
                case "windows":
                    gd = new File(System.getenv("APPDATA"), ".minecraft");
                    break;
                default:
                case "linux":
                    gd = new File(System.getenv("HOME"), ".minecraft");
                    break;
                case "osx":
                    gd = new File(System.getenv("HOME"), "Library/Application Support/minecraft"); // why is OSX like this
                    break;
            }
        }
        return new File(gd, "launcher_profiles.json");
    }

    // This fucking sucks
    @Override
    public void setLauncherConfig(String jrePath) throws FileNotFoundException {
        File config = getLauncherConfig();
        JsonStreamParser parser = new JsonStreamParser(new FileReader(config));
        JsonObject root = parser.next().getAsJsonObject();
        JsonObject profiles = root.get("profiles").getAsJsonObject();
        // Find which instance we're running from.
        //String version = Minecraft.getMinecraft().getVersion();
        /*File minecraft_path = new File(Minecraft.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String name = minecraft_path.getName();
        String version = name.substring(0, name.lastIndexOf('.'));
        String path = Minecraft.getMinecraft().gameDir.getPath();*/

        ArrayList<Map.Entry<String, JsonElement>> ents = new ArrayList<>();

        ents.addAll(profiles.entrySet());
        Comparator<Map.Entry<String, JsonElement>> comp = Comparator.comparing(o -> o.getValue().getAsJsonObject().get("lastUsed").getAsString());
        ents.sort(comp.reversed());
        Map.Entry<String, JsonElement> el = ents.get(0);
        el.getValue().getAsJsonObject().addProperty("javaDir", jrePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //JRECheck.LOG.info(gson.toJson(root));
        try (FileWriter fileWriter = new FileWriter(config)) {
            fileWriter.write(gson.toJson(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
