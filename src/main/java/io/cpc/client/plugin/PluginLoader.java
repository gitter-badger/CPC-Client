package io.cpc.client.plugin;

import org.yaml.snakeyaml.Yaml;

import java.io.DataInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PluginLoader {
    private static Yaml yaml = new Yaml();
    private static Set<CPCPlugin> loadedPlugins = new HashSet<>();

    /**
     * Load plugin from the specified file
     *
     * @param pluginJar jar plugin file
     * @throws MalformedPluginFileException if config file does not exists or it's malformed
     */
    public static CPCPlugin loadPlugin(File pluginJar) throws MalformedPluginFileException {
        ClassLoader classLoader = null;
        try {
            classLoader = new URLClassLoader(new URL[]{pluginJar.toURI().toURL()});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        DataInputStream pluginFile = new DataInputStream(classLoader.getResourceAsStream("plugin.yml"));
        Map<String, String> config;
        try {
            config = (Map<String, String>) yaml.load(pluginFile);
        } catch (ClassCastException e) {
            throw new MalformedPluginFileException();

        }
        String mainClassLocation = config.get("mainClass");

        try {
            return (CPCPlugin) classLoader.loadClass(mainClassLocation).newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Load a plugin and enable it invoking {@link CPCPlugin#onEnable()}
     *
     * @param pluginJar jar plugin file
     * @throws MalformedPluginFileException if config file does not exists or it's malformed
     * @see PluginLoader#loadPlugin(File)
     */
    public static void loadAndEnable(File pluginJar) throws MalformedPluginFileException {

        CPCPlugin plugin = loadPlugin(pluginJar);
        plugin.onEnable();
        loadedPlugins.add(plugin);
    }
}

