package com.possible_triangle.create_jetpack;

import com.possible_triangle.create_jetpack.config.Configs;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        Configs.register();
        CJContent.register();
    }

}
