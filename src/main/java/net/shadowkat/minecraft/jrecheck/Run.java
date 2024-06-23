package net.shadowkat.minecraft.jrecheck;

public class Run {
    public static String[] keys = {"os.arch", "java.version", "java.home", "java.vendor", "java.runtime.name"};
    public static void main(String[] args) {
        for (String key : keys) {
            System.out.print(key+"=");
            System.out.println(System.getProperty(key));
        }
    }
}
