package com.tf.npu.util.register;

import com.mojang.logging.LogUtils;
import com.tf.npu.blocks.NpuBlocks;
import com.tf.npu.entities.NpuEntities;
import com.tf.npu.itemgroups.NpuItemGroups;
import com.tf.npu.items.NpuItems;
import org.slf4j.Logger;

public final class RegisterObjects {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void register() {
        // 注册
        LOGGER.info("Register mod things to mod event bus");
        LOGGER.info("Starting dealing entities");
        NpuEntities.register();
        LOGGER.info("Starting dealing blocks");
        NpuBlocks.register();
        LOGGER.info("Starting dealing items");
        NpuItems.register();
        LOGGER.info("Starting dealing creative mode tabs");
        NpuItemGroups.register();
    }
}
