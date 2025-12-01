package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockTemplate;
import net.minecraft.block.AbstractBlock;

public interface Common {
    static AbstractBlock.Settings createSettings(BlockTemplate template){
        AbstractBlock.Settings settings = AbstractBlock.Settings.create();
        if (template.noLootTable) settings.dropsNothing();
        if (template.noCollision) settings.noCollision();
        if (template.noOcclusion) settings.dynamicBounds();
        if (template.noParticlesOnBreak) settings.noBlockBreakParticles();
        return NpuBlocks.Material.valueOf(template.Material).addToSettings(settings);
    }
}
