package io.cpc.client.plugin;

public interface CPCPlugin {
    void onEnable();

    void onDisable();

    String getName();
}
