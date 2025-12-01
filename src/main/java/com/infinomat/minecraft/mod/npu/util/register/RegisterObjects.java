package com.infinomat.minecraft.mod.npu.util.register;

import com.mojang.logging.LogUtils;
import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.entities.NpuEntities;
import com.infinomat.minecraft.mod.npu.itemgroups.NpuItemGroups;
import com.infinomat.minecraft.mod.npu.items.NpuItems;
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
