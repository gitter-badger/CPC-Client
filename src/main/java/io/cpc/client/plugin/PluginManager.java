package io.cpc.client.plugin;

import java.io.File;
import java.io.FileFilter;

public class PluginManager {
    private static final File DEFAULT_PLUGIN_FOLDER = new File("./plugins");
    private static boolean scanned = false;

    public static void scanForPlugin(File folder) throws IllegalArgumentException {
        if (!folder.isDirectory()) throw new IllegalArgumentException("Folder not found!");
        for (File probablePlugin : folder.listFiles()) {
            FileFilter filter = pathname -> pathname.getAbsolutePath().endsWith(".jar");
            if (filter.accept(probablePlugin)) {
                try {
                    PluginLoader.loadAndEnable(probablePlugin);
                } catch (MalformedPluginFileException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void scanForPlugin() {
        if (scanned) return;
        if (!DEFAULT_PLUGIN_FOLDER.isDirectory()) DEFAULT_PLUGIN_FOLDER.mkdirs();
        scanForPlugin(DEFAULT_PLUGIN_FOLDER);
        scanned = true;
    }
}
