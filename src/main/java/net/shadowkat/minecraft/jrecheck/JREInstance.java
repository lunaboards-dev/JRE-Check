package net.shadowkat.minecraft.jrecheck;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class JREInstance {
    public File path;
    public String version;
    public String arch;
    public int patch;
    public String version_full;
    public String name;
    public JREInstance(File p) throws IOException {
        path = p;
        /*ProcessBuilder procb = new ProcessBuilder(p+"/bin/java", "-jar", JRECheck.JAR_PATH.getPath());
        Process proc = procb.start();
        try {
            proc.waitFor();
            Properties prop = new Properties();
            prop.load(proc.getInputStream());
            version_full = prop.getProperty("java.version");
            arch = prop.getProperty("os.arch");
            int sep = version_full.indexOf('_');
            if (sep == -1) {
                sep = version_full.lastIndexOf('.');
                version = version_full;
                patch = Integer.parseInt(version_full.substring(sep+1), 10);
            } else {
                version = version_full.substring(0, sep - 1);
                patch = Integer.parseInt(version_full.substring(sep + 1), 10);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
        String[] jinfo = Utils.javaInfo(p+"/bin/java");
        Pair<String, Integer> jver = Utils.javaVersion(jinfo[0]);
        version_full = jinfo[0];
        version = jver.getLeft();
        patch = jver.getRight();
        arch = jinfo[1];
        path = new File(jinfo[2]);
        name = jinfo[3];
    }

    public String toString() {
        return String.format("%s %s (%s)", version_full, arch, path.getPath());
    }
}
