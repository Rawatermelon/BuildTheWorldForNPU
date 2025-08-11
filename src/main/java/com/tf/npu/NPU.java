package com.tf.npu;

import com.tf.npu.util.Reference;
import com.tf.npu.util.register.RegisterObjects;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NPU implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Mod {} is loading...", Reference.MOD_NAME);
        RegisterObjects.register();
    }
}
