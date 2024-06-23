package net.shadowkat.minecraft.jrecheck.confighandlers;

import java.io.File;
import java.io.FileNotFoundException;

public interface IConfigHandler {
    boolean matchesLauncher(String launcher);
    File getLauncherConfig();
    void setLauncherConfig(String jrePath) throws FileNotFoundException;
}
