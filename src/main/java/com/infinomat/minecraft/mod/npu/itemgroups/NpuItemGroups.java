package com.infinomat.minecraft.mod.npu.itemgroups;

import com.mojang.logging.LogUtils;
import com.infinomat.minecraft.mod.npu.items.NpuItems;
import com.infinomat.minecraft.mod.npu.util.FileDataGetter;
import com.infinomat.minecraft.mod.npu.util.FolderDataGetter;
import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.RegisterList;
import com.infinomat.minecraft.mod.npu.util.register.data.template.ItemGroupTemplate;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;


//用于注册新物品栏并向其中添加物品

public final class NpuItemGroups {
    public static final Logger LOGGER = LogUtils.getLogger();

    private NpuItemGroups() {
    }

    public static final String templateFolder = "template";
    public static final String registerFileName = "register.json";
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.ITEM_GROUP));

    public static RegisterList registerList;
    private static final HashMap<String, ItemGroupRegister> TemplateMap = new HashMap<>();

    static {
        loadRegisterList();
        loadTemplate();
    }

    private static void loadRegisterList() {
        String filePath = PathTools.linkPath(baseFolderPath, registerFileName);
        URL url = FolderDataGetter.class.getClassLoader().getResource(filePath);
        try {
            if (url == null) throw new RuntimeException("Miss register.json");

            // 修改：使用类加载器和相对路径方式加载资源，避免直接使用Paths.get(url.toURI())
            registerList = new FileDataGetter<>(filePath, RegisterList.class).getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadTemplate() {
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (ItemGroupTemplate itemGroupTemplate : new FolderDataGetter<>(folderPath, ItemGroupTemplate.class).getList()) {
            TemplateMap.put(itemGroupTemplate.getId(), ItemGroupRegister.create(itemGroupTemplate));
        }
    }

    public static void register() {
        for (var group : registerList.getGroups()) {
            TemplateMap.get(group.template).register(group.items);
        }
    }

    private record ItemGroupRegister(int iconIndex) {
        public static ItemGroupRegister create(ItemGroupTemplate template_data) {
            return new ItemGroupRegister(template_data.getIconIndex());
        }

        public void register(List<String> ids) {
            for (var id : ids) {
                registerItemGroup(id, NpuItems.getItemList(id.substring(0, id.length() - 4)), iconIndex);
            }
        }
        private static void registerItemGroup(String id, List<Item> itemList, int iconIndex) {
            ItemGroup GROUP = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(itemList.get(iconIndex)))
                    .displayName(Text.translatable("itemgroup.npu." + id))
                    .entries((context, entries) ->
                            {
                                for (Item item : itemList) {
                                    entries.add(item);
                                }
                            }
                    )
                    .build();
            Registry.register(Registries.ITEM_GROUP, Identifier.of(Reference.MOD_ID, id), GROUP);
        }
    }
}