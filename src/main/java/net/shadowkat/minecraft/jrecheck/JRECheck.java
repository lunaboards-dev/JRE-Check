package net.shadowkat.minecraft.jrecheck;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.shadowkat.minecraft.jrecheck.confighandlers.IConfigHandler;
import net.shadowkat.minecraft.jrecheck.confighandlers.MicrosoftConfigHandler;
import net.shadowkat.minecraft.jrecheck.confighandlers.MultiMCConfigHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Mod(
        modid = "jre-check",
        name = "JRE Checker",
        version = "@VERSION@"
)
public class JRECheck {
    public static final Logger LOG = LogManager.getLogger("jre-check");
    @Mod.Instance("jre-check")
    public static JRECheck instance;
    public static File JAR_PATH;
    //public static Map<String, String> args;
    public static String launcher;

    @Mod.EventHandler
    public void onPreInit(final FMLPreInitializationEvent event) {
        LOG.debug("preInit");
        //args = (Map<String, String>) Launch.blackboard.get("launchArgs");
        String j_home = System.getProperty("java.home");
        String j_version = System.getProperty("java.version");
        String j_vendor = System.getProperty("java.vendor");
        String os_arch = System.getProperty("os.arch");
        String os_name = System.getProperty("os.name");
        launcher = System.getProperty("minecraft.launcher.brand");
        JAR_PATH = event.getSourceFile();
        /*args.forEach((key, value) -> {
            System.out.println(String.format("%s = %s", key, value));
        });*/
        LOG.info("Home: "+j_home);
        LOG.info("Version: "+j_version);
        LOG.info("Vendor: "+j_vendor);
        LOG.info("Arch: "+os_arch);
        LOG.info("OS: "+os_name);
        LOG.info("Source file: "+JAR_PATH.getPath());
        LOG.info("Launcher: "+launcher);
        /*List<JREInstance> il = Utils.getJREs(null, 0);
        LOG.info("JREs found:");
        for (JREInstance j : il) {
            LOG.info(j);
        }
        /*{
            JRESelect sel = new JRESelect(inst);
            LOG.info("Selected Java version: "+sel.value);
        }*/
        Pair<String, Integer> jver = Utils.javaVersion(System.getProperty("java.version"));
        LOG.info(String.format("%s p %d", jver.getLeft(), jver.getRight()));
        if (Settings.settings.always_show_selection || (!jver.getLeft().equals("1.8.0") || jver.getRight() < Settings.settings.minimum_patch)) {
            List<JREInstance> inst = Utils.getJREs("1.8.0", Settings.settings.minimum_patch);
            if (inst.isEmpty()) {
                showDownloadPrompt();
            } else {
                if (Settings.settings.silent) {
                    JREInstance j = null;
                    for (JREInstance jre : inst) {
                        if (j == null || jre.patch > j.patch)
                            j = jre;
                    }
                    setJRE(j.path.getPath()+"/bin/java");
                } else {
                    selectJRE(inst);
                }
            }
        }
    }

    private void showDownloadPrompt() {
        String link = Utils.getAdoptiumLink();
        if (link == null) {
            JOptionPane.showMessageDialog(null, "Your Java Runtime Environment is out of date.\nYour OS is unknown, so you're on your own!", "SHOW STOPPER: No Java installs found!", JOptionPane.ERROR_MESSAGE);
            runScreaming();
        }
        int n = JOptionPane.showConfirmDialog(null, "Would you like to open the Adoptium download page?", "SHOW STOPPER: No Java installs found!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (n == JOptionPane.YES_OPTION) {
            try {
                LOG.info("Open: "+link);
                Desktop.getDesktop().browse(new URI(link));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        runScreaming();
    }

    private void runScreaming() {
        //System.exit(1);
        LOG.error("Exiting Minecraft!");
        FMLCommonHandler.instance().exitJava(1, false);
    }

    private void exit() {
        JOptionPane.showMessageDialog(null, "Your Java Runtime Environment for this instance has been updated.\nRemember to restart your launcher!", "JRE Set", JOptionPane.WARNING_MESSAGE);
        FMLCommonHandler.instance().exitJava(0, false);
    }

    private void selectJRE(List<JREInstance> inst) {
        JRESelect sel = new JRESelect(inst);
        LOG.info("Selected Java version: "+sel.value);
        setJRE(sel.value);
        exit();
    }

    IConfigHandler[] handlers = {
            new MicrosoftConfigHandler(),
            new MultiMCConfigHandler()
    };

    private void setJRE(String command) {
        File game_dir = Minecraft.getMinecraft().gameDir;
        //JsonStreamParser parser = new JsonStreamParser()
        for (IConfigHandler h : handlers) {
            if (h.matchesLauncher(launcher)) {
                File f = h.getLauncherConfig();
                File bak = new File(f.getPath()+".bak");
                try {
                    FileUtils.copyFile(f, bak);
                    h.setLauncherConfig(command);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Mod.EventHandler
    public void onInit(final FMLInitializationEvent event) {

    }
}
