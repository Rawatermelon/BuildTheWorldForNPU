package com.infinomat.minecraft.mod.npu.itemgroups;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.slf4j.Logger;

//用于向原版物品栏添加物品

public final class ItemGroups {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static void addCreative() {
        ItemGroupEvents.modifyEntriesEvent(net.minecraft.item.ItemGroups.BUILDING_BLOCKS).register(content -> {
            // content.add(NPUItems.NPU_ITEM); TODO(NpuItems做好之后来做这个)
        });
    }
}
