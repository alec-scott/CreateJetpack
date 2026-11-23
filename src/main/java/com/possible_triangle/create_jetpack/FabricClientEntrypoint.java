package com.possible_triangle.create_jetpack;

import com.possible_triangle.create_jetpack.client.CJClient;
import net.fabricmc.api.ClientModInitializer;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CJClient.register();
    }

}
