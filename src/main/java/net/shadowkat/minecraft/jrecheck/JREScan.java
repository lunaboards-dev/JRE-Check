package net.shadowkat.minecraft.jrecheck;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JREScan {
    ArrayList<File> scandirs;
    String ex;
    public JREScan(String ext) {
        ex = ext;
        scandirs = new ArrayList<>();
    }
    public void addDirectory(File f) {
        if (f.isDirectory()) {
            for (File ent : f.listFiles()) {
                if (new File(ent.getPath()+"/bin/java"+ex).isFile()) {
                    add(ent);
                } else if (new File(ent.getPath()+"/jre/bin/java"+ex).isFile()) {
                    add(new File(ent.getPath()+"/jre"));
                }
            }
        }
    }

    public void addDirectory(String s) {
        addDirectory(new File(s));
    }

    public void add(File f) {
        try {
            File can = f.getCanonicalFile();
            for (File file : scandirs) {
                if (can.equals(file)) {
                    return;
                }
            }
            scandirs.add(can);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(String s) {
        add(new File(s));
    }

    public ArrayList<JREInstance> scan(String version, int min_patch) {
        ArrayList<JREInstance> jres = new ArrayList<>();
        for (File dir : scandirs) {
            try {
                JREInstance jre = new JREInstance(dir);
                //JRECheck.LOG.debug(String.format("%s == %s | %d >= %d", jre.version, version, jre.patch, min_patch));
                if (version == null || (jre.version.equals(version) && jre.patch >= min_patch)) {
                    jres.add(jre);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jres;
    }
}
