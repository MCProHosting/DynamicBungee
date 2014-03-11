package com.mcprohosting.plugins.dynamicbungee;

import com.google.common.io.Files;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DynamicPluginLoader {

    Map<String, DynamicPlugin> plugins;

    public DynamicPluginLoader() {
        plugins = new HashMap<>();

        loadAll();
    }

    public void loadAll() {
        File directory = new File(DynamicBungee.getPlugin().getDataFolder(), "plugins");

        if (directory.exists() == false) {
            directory.mkdir();
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (Files.getFileExtension(file.getAbsolutePath()).equals("jar") == false) {
                continue;
            }

            URLClassLoader classLoader;
            try {
                classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                continue;
            }

            Reflections reflections = new ConfigurationBuilder().setUrls(
                    ClasspathHelper.forClassLoader(classLoader)
            ).addClassLoader(classLoader).build();

            Set<Class<? extends DynamicPlugin>> plugins = reflections.getSubTypesOf(DynamicPlugin.class);

            if (plugins.size() != 1) {
                continue;
            }

            Class clazz = null;
            Object instance = null;
            boolean instantiated = false;
            for (Class<? extends DynamicPlugin> plugin : plugins) {
                clazz = plugin;

                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

                instantiated = true;
            }

            if (instantiated == false) {
                continue;
            }

            Method method = null;
            boolean loaded = false;
            try {
                method = clazz.getMethod("onLoad");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                continue;
            }

            try {
                method.invoke(instance);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                continue;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

            DynamicPlugin plugin = (DynamicPlugin) instance;
            this.plugins.put(
                    plugin.getName(),
                    plugin);
            DynamicBungee.getPlugin().getLogger().info(plugin.getName() + " has been loaded!");
        }
    }

}
