package com.mcprohosting.plugins.dynamicbungee;

import java.io.File;

public abstract class DynamicPlugin {

    private final String NAME = this.getClass().getSimpleName();
    private final File FOLDER = new File(DynamicBungee.getPlugin().getDataFolder(), "plugins" + File.separator + NAME);

    public DynamicPlugin() {
        if (!FOLDER.exists()) {
            FOLDER.mkdirs();
        }
    }

    public abstract void onLoad();

    public String getName() {
        return NAME;
    }

    public File getDataFolder() {
        return FOLDER;
    }

}