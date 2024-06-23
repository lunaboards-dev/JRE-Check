package net.shadowkat.minecraft.jrecheck;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Utils {

    public static String getArch() {
        String arch = System.getProperty("os.arch");
        switch (arch) {
            case "x86":
            case "i686":
                return "x86";
            case "amd64":
            case "x86_64":
                return "x64";
            case "aarch64":
                return "aarch64";
            case "arm":
                return "arm";
        }
        if (arch.startsWith("arm"))
            return "arm";
        return null;
    }

    public static String getOS() {
        String os = System.getProperty("os.name");
        if (os == null) return null;
        String real_os = os;
        if (os.startsWith("Windows"))
            real_os = "windows";
        if (os.startsWith("Mac"))
            real_os = "mac";
        return real_os.toLowerCase();
    }

    public static String getAdoptiumLink() {
        String arch = getArch();
        String os = getOS();
        if (os == null || arch == null) {
            return null;
        }

        return String.format("https://adoptium.net/temurin/releases/?os=%s&arch=%s&version=8", os, arch);
    }

    private static String getProp(Properties prop, String key) {
        String val = prop.getProperty(key);
        if (val.startsWith("\"") && val.endsWith("\""))
            val = val.substring(1, val.length()-1);
        return val;
    }

    public static Pair<String, Integer> javaVersion(String str) {
        /*int sep = str.indexOf('_');
        String base_ver = str.substring(0, sep-1);
        int patch = Integer.parseInt(str.substring(sep+1), 10);
        return Pair.of(base_ver, patch);*/
        int sep = str.indexOf('_');
        String version;
        int patch;
        if (sep == -1) {
            sep = str.lastIndexOf('.');
            version = str;
            patch = Integer.parseInt(str.substring(sep+1), 10);
        } else {
            version = str.substring(0, sep);
            patch = Integer.parseInt(str.substring(sep + 1), 10);
        }
        return Pair.of(version, patch);
    }

    public static String[] javaInfo(String cmd) throws IOException {
        ProcessBuilder procb = new ProcessBuilder(cmd, "-jar", JRECheck.JAR_PATH.getPath());
        Process proc = procb.start();
        String version_full;
        String arch;
        String home;
        String runtimeOrVendor;
        try {
            proc.waitFor();
            Properties prop = new Properties();
            prop.load(proc.getInputStream());
            version_full = prop.getProperty("java.version");
            arch = prop.getProperty("os.arch");
            home = prop.getProperty("java.home");
            runtimeOrVendor = prop.getProperty("java.runtime.name");
            if (runtimeOrVendor == null)
                runtimeOrVendor = prop.getProperty("java.vendor");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ArrayUtils.toArray(version_full, arch, home, runtimeOrVendor);
    }

    private static ArrayList<JREInstance> getJREWindows(String version, int patch) {
        String path = System.getenv("PATH");
        JREScan scan = new JREScan(".exe");
        for (String str : path.split(";")) {
            try {
                File _path = new File(str + "/java");
                if (_path.isFile()) {
                    /*Properties props = new Properties();
                    props.load(Files.newInputStream(_path.toPath()));
                    String j_ver = getProp(props, "java.version");
                    String j_arch = getProp(props, "os.arch");
                    String j_home = getProp(props, "java.home");
                    Pair<String, Integer> version = javaVersion(j_ver);
                    if (!version.getLeft().equals("1.8.0")) {
                        continue;
                    }
                    if (version.getRight() >= Settings.settings.minimum_patch) {
                        list.add(Triple.of(new File(j_home), j_ver, j_arch));
                    }*/
                    scan.add(new File(str+"/.."));
                }
            } catch (Exception e) {
                JRECheck.LOG.error(e);
            }
        }
        return scan.scan(version, patch);
    }

    private static ArrayList<JREInstance> getJRELinux(String version, int patch) {
        JREScan scan = new JREScan("");
        scan.addDirectory("/usr/lib/jvm");
        scan.addDirectory("/usr/java");
        scan.addDirectory("/usr/lib64/jvm");
        scan.addDirectory("/usr/lib32/jvm");
        scan.addDirectory("/opt/jdk");
        scan.addDirectory("/opt/jdks");
        scan.addDirectory("/opt/ibm");
        return scan.scan(version, patch);
    }

    public static List<JREInstance> getJREs(String version, int patch) {
        String os = getOS();
        if (os == null) return Collections.emptyList();
        switch (os) {
            case "windows":
                return getJREWindows(version, patch);
            case "linux":
                return getJRELinux(version, patch);
            default:
                return Collections.emptyList();
        }
    }
}
