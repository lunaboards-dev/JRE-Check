package net.shadowkat.minecraft.jrecheck.confighandlers;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;

public class MultiMCConfigHandler implements IConfigHandler {
    String[] supportedLaunchers = {"multimc", "prismlauncher", "polymc", "devlauncher"};
    @Override
    public boolean matchesLauncher(String launcher) {
        for (String s : supportedLaunchers) {
            if (s.equals(launcher))
                return true;
        }
        return false;
    }

    @Override
    public File getLauncherConfig() {
        return new File(Minecraft.getMinecraft().gameDir.getParentFile(), "instance.cfg");
    }

    @Override
    public void setLauncherConfig(String jrePath) {
        File cfg = getLauncherConfig();
        ArrayList<String> strings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(cfg))){
            br.lines().forEach(line -> {
                if (line.startsWith("JavaPath="))
                    strings.add("JavaPath="+jrePath);
                else
                    strings.add(line);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cfg))) {
            for (String line : strings) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
