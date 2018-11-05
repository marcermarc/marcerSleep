package de.marcermarc.sleep;

import de.marcermarc.sleep.controller.ConfigController;
import de.marcermarc.sleep.controller.PluginController;
import de.marcermarc.sleep.listener.Sleep;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private PluginController controller = new PluginController();

    @Override
    public void onEnable() {
        controller.setMain(this);

        PluginManager pM = getServer().getPluginManager();

        registerEvents(pM);

        controller.setConfig(new ConfigController(controller));
    }

    private void registerEvents(PluginManager in_PM) {
        in_PM.registerEvents(new Sleep(controller), this);
    }

    @Override
    public void onDisable() {

    }
}

