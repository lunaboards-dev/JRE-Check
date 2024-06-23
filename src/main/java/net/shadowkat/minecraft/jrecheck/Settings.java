package net.shadowkat.minecraft.jrecheck;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = "jre-check")
public class Settings {
    public static _Settings settings = new _Settings();
    public static class _Settings {
        @Comment({"Minimum patch level required."})
        @Name("Minimum Patch")
        public int minimum_patch = 400;

        @Comment({"Silently choose highest patch version."})
        @Name("Silent mode")
        public boolean silent = false;

        @Comment({"Merely warn user if their Java is outdated"})
        @Name("Only warn")
        public boolean warn_only = false;

        // Debug
        @Name("DEBUG: Always show selection")
        public boolean always_show_selection = false;
    }
}
