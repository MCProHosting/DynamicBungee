package com.mcprohosting.plugins.dynamicbungee;

public abstract class DynamicPlugin {

    private final String NAME = this.getClass().getSimpleName();

    public abstract void onLoad();

    public String getName() {
        return NAME;
    }

}