package com.tf.npu.items;

import com.mojang.logging.LogUtils;
import com.tf.npu.blocks.NpuBlocks;
import com.tf.npu.entities.NpuEntities;
import com.tf.npu.items.npuitemnewclasses.VehicleItem;
import com.tf.npu.util.FolderDataGetter;
import com.tf.npu.util.PathTools;
import com.tf.npu.util.Reference;
import com.tf.npu.util.register.data.RegisterList;
import com.tf.npu.util.register.data.template.ItemTemplate;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public final class NpuItems {
    public static final Logger LOGGER = LogUtils.getLogger();

    private NpuItems() {
    }

    public static final String templateFolder = "template";
    public static final String registerFoder = "register";
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.ITEM));

    public static final HashMap<String, RegisterList> RegisterListMap = new HashMap<>();// 待注册物品列表
    private static final HashMap<String, ItemRegister> TemplateMap = new HashMap<>();   //  模版映射
    private static final HashMap<String, List<Item>> ItemGroupMap = new HashMap<>();    // 物品栏-物品映射

    static {
        loadRegisterList();
        loadTemplate();
    }

    private static void loadRegisterList() {
        String folderPath = PathTools.linkPath(baseFolderPath, registerFoder);

        for (RegisterList registerList : new FolderDataGetter<>(folderPath, RegisterList.class).getList()) {
            RegisterListMap.put(registerList.getId(), registerList);
        }
        LOGGER.info("Got Item Register Lists");
    }

    private static void loadTemplate() {
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (ItemTemplate itemTemplate : new FolderDataGetter<>(folderPath, ItemTemplate.class).getList()) {
            TemplateMap.put(itemTemplate.getId(), ItemRegister.create(itemTemplate));
        }
        LOGGER.info("Got Item Register Templates");
    }

    public static void register() {
        LOGGER.info("Register mod items for " + Reference.MOD_ID);

        for (var itemGroup : RegisterListMap.keySet()) {
            ItemGroupMap.put(itemGroup, new ArrayList<>());
            for (var group : RegisterListMap.get(itemGroup).getGroups()) {
                ItemGroupMap.get(itemGroup).addAll(TemplateMap.get(group.template).register(group.items));
            }
        }
        for (var itemGroup : NpuBlocks.ItemGroupMap.keySet()) {
            ItemGroupMap.computeIfAbsent(itemGroup, k -> new ArrayList<>());
            NpuBlocks.ItemGroupMap.get(itemGroup).forEach(block -> ItemGroupMap.get(itemGroup).add(Items.register(block)));
        }

        LOGGER.info("Succeed to register Items for " + Reference.MOD_ID);
    }

    public static List<Item> getItemList(String itemGroup) {
        return ItemGroupMap.get(itemGroup);
    }

    private record ItemRegister(boolean isSpawnEgg, boolean isVehicle) {

        public static ItemRegister create(ItemTemplate template_data) {
            return new ItemRegister(template_data.isSpawnEgg(), template_data.isVehicle());
        }

        public List<Item> register(List<String> ids) {
            ArrayList<Item> itemList = new ArrayList<>();
            ids.forEach(id -> {
                if (isSpawnEgg) {
                    itemList.add(registerItem(id, (settings) ->
                            new SpawnEggItem(NpuEntities.MobEntityMap.get(id), settings), new SpawnEggItem.Settings()));
                    LOGGER.info("Registered spawn egg item: {}", id);
                } else if (isVehicle) {
                    itemList.add(registerItem(id, (settings) ->
                            new VehicleItem(NpuEntities.VehicleMap.get(id), settings), new VehicleItem.Settings()));
                    LOGGER.info("Registered vehicle item: {}", id);
                } else {
                    itemList.add(registerItem(id, Item::new, new Item.Settings()));
                    LOGGER.info("Registered normal item: {}", id);
                }
            });
            return itemList;
        }

        public static Item registerItem(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
            final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Reference.MOD_ID, path));
            return Items.register(registryKey, factory, settings);
        }

    }
}
