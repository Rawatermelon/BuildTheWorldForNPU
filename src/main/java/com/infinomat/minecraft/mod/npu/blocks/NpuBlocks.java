package com.infinomat.minecraft.mod.npu.blocks;

import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.*;
import com.mojang.logging.LogUtils;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.*;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.Common;
import com.infinomat.minecraft.mod.npu.util.FileDataGetter;
import com.infinomat.minecraft.mod.npu.util.FolderDataGetter;
import com.infinomat.minecraft.mod.npu.util.PathTools;
import com.infinomat.minecraft.mod.npu.util.Reference;
import com.infinomat.minecraft.mod.npu.util.register.data.RegisterList;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockTemplate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class NpuBlocks {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String templateFolder = "template";
    public static final String registerFoder = "register";
    public static final String baseFolderPath = PathTools.linkPath(Reference.PATH.get(Reference.PathType.LOADER), Reference.PATH.get(Reference.PathType.BLOCK));

    public static final HashMap<String, RegisterList> RegisterListMap = new HashMap<>();// 待注册方块列表
    private static final HashMap<String, BlockRegister> TemplateMap = new HashMap<>();   //  模版映射
    public static final HashMap<String, List<Block>> ItemGroupMap = new HashMap<>();    // Id-方块映射
    public static final List<Block> CutoutBlocks = new ArrayList<>();   // 透明方块
    public static final List<Block> TransparentBlocks = new ArrayList<>();   // 半透明方块

    static {
        loadRegisterList();
        loadTemplate();
    }
    private static void loadRegisterList() {
        String folderPath = PathTools.linkPath(baseFolderPath, registerFoder);

        for (RegisterList registerList : new FolderDataGetter<>(folderPath, RegisterList.class).getList()) {
            RegisterListMap.put(registerList.getId(), registerList);
        }
    }
    private static void loadTemplate() {
        String folderPath = PathTools.linkPath(baseFolderPath, templateFolder);

        for (BlockTemplate blockTemplate : new FolderDataGetter<>(folderPath, BlockTemplate.class).getList()) {
            TemplateMap.put(blockTemplate.getId(), BlockRegister.create(blockTemplate));
        }
    }

    public static void register() {
        for (var itemGroup : RegisterListMap.keySet()){
            ItemGroupMap.put(itemGroup, new ArrayList<>());
            for (var group: RegisterListMap.get(itemGroup).getGroups()){
                ItemGroupMap.get(itemGroup).addAll(TemplateMap.get(group.template).register(group.items));
            }
        }
    }

    private record BlockRegister(BlockTemplate template) {

        public static BlockRegister create(BlockTemplate template) {
            return new BlockRegister(template);
        }

        public List<Block> register(List<String> ids) {
            ArrayList<Block> blockList = new ArrayList<>();
            ids.forEach(id -> {
                switch (StructureType.valueOf(template.StructureType)){
                    case NORMAL_STRUCTURE -> {
                        BlockShapeData shapeData =
                                new FileDataGetter<>(template.getModelPath(id), BlockShapeData.class).getData();
                        blockList.add(registerBlock(id, new NormalStructure.Factory().setShape(shapeData, template.loadMethod).build(), Common.createSettings(template)));
                    }
                    case NORMAL_HALF_SLAB -> {
                        blockList.add(registerBlock(id, new NormalHalfSlab.Factory().setCanBeDouble(template.double_enable).build(), Common.createSettings(template)));
                    }
                    case DOOR_AND_WINDOW -> {
                        BlockShapeData shapeData_open =
                                new FileDataGetter<>(template.getModelPath(id, "open"), BlockShapeData.class).getData();
                        BlockShapeData shapeData_close =
                                new FileDataGetter<>(template.getModelPath(id, "close"), BlockShapeData.class).getData();
                        blockList.add(registerBlock(id, new DoorAndWindow.Factory().setShape(shapeData_open, shapeData_close, template.loadMethod).build(), Common.createSettings(template)));
                    }
                    case HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE -> {
                        ArrayList<BlockShapeData> shapeDatas = new ArrayList<>(0);
                        for (int i = 0; i < 6; i++) {
                            shapeDatas.add(new FileDataGetter<>(template.getModelPath(id, String.valueOf(i)), BlockShapeData.class).getData());
                        }
                        blockList.add(registerBlock(id, new HorizontalMultipleDirectionalStructure.Factory().setShape(shapeDatas, template.loadMethod).build(), Common.createSettings(template)));
                    }
                    case HORIZONTAL_DIRECTIONAL_HALF_SLAB -> {
                        blockList.add(registerBlock(id, new HorizontalDirectionalHalfSlab.Factory().setCanBeDouble(template.double_enable).build(), Common.createSettings(template)));
                    }
                    case HORIZONTAL_DIRECTIONAL_STRUCTURE -> {
                        BlockShapeData shapeData =
                                new FileDataGetter<>(template.getModelPath(id), BlockShapeData.class).getData();
                        blockList.add(registerBlock(id, new HorizontalDirectionalStructure.Factory().setShape(shapeData, template.loadMethod).build(), Common.createSettings(template)));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + template.StructureType);
                }
            });
            if (Objects.equals(template.render, "cutout")) CutoutBlocks.addAll(blockList);
            if (Objects.equals(template.render, "transparent")) TransparentBlocks.addAll(blockList);
            return blockList;
        }

        private static Block registerBlock(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
            final Identifier identifier = Identifier.of(Reference.MOD_ID, path);
            final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

            return Blocks.register(registryKey, factory, settings);
        }
    }

    public enum LoadMethod {ROUGH, METICULOUS}
    enum StructureType {
        NORMAL_STRUCTURE,
        DOOR_AND_WINDOW,
        HORIZONTAL_DIRECTIONAL_STRUCTURE,
        HORIZONTAL_DIRECTIONAL_HALF_SLAB,
        NORMAL_HALF_SLAB,
        HORIZONTAL_MULTIPLE_DIRECTIONAL_STRUCTURE
    }
    public enum Material {
        //EXAMPLE("example", 硬度, 音效包, (BlockState state) ->{根据不同的blockstate返回不同的亮度值}),
        IRON("iron", 5.0F, BlockSoundGroup.IRON, (state, world, pos) -> false),
        ROCK("rock", 2.5F, BlockSoundGroup.STONE, (state, world, pos) -> false);

        private final String name;
        private final float strength;
        private final BlockSoundGroup sound;
        private final AbstractBlock.ContextPredicate lightLevel;

        Material(String name, float strength, BlockSoundGroup sound, AbstractBlock.ContextPredicate lightLevel) {
            this.name = name;
            this.strength = strength;
            this.sound = sound;
            this.lightLevel = lightLevel;
        }

        public String getName() {
            return this.name;
        }

        public AbstractBlock.Settings addToSettings(AbstractBlock.Settings settings) {
            return settings.strength(this.strength).sounds(this.sound).emissiveLighting(this.lightLevel);
        }
    }
}
