package com.example.addon;

import java.lang.invoke.MethodHandles;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraMods extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger(ExtraMods.class);

    public void onInitialize() {
        LOG.info("Initializing ExtraMods");
        MeteorClient.EVENT_BUS.registerLambdaFactory("com.example.addon.modules.AutoWither", (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke((Object)null, klass, MethodHandles.lookup()));
        Modules.get().add(new com.example.addon.modules.AutoWither("AutoWither", "Automatic building of wither + auto name tag."));
        MeteorClient.EVENT_BUS.registerLambdaFactory("com.example.addon.modules.FrogBuild", (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke((Object)null, klass, MethodHandles.lookup()));
        Modules.get().add(new com.example.addon.modules.FrogBuild("FrogBuild", "AutoBuild for logo of the coolest clan on the server karasique.com."));
    }

    public String getPackage() {
        return "com.example.addon";
    }
}
