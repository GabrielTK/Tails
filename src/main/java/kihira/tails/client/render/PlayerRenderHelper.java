/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import kihira.tails.api.IRenderHelper;
import kihira.tails.client.model.tail.ModelCatTail;
import kihira.tails.client.model.tail.ModelDevilTail;
import kihira.tails.client.model.tail.ModelDragonTail;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class PlayerRenderHelper implements IRenderHelper {

    @Override
    public void onPreRenderTail(EntityLivingBase entity, RenderPart tail, PartInfo info, double x, double y, double z) {
        if (info.partType != PartsData.PartType.TAIL) return;

        if (tail.modelPart instanceof ModelDragonTail) {
            GlStateManager.translate(0F, 0.68F, 0.1F);
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
        }
        else if (tail.modelPart instanceof ModelCatTail || tail.modelPart instanceof ModelDevilTail) {
            GlStateManager.translate(0F, 0.65F, 0.1F);
            GlStateManager.scale(0.9F, 0.9F, 0.9F);
        }
        else {
            if (entity.isSneaking()) GlStateManager.translate(0f, 0.82f, 0f);
            else GlStateManager.translate(0F, 0.65F, 0.1F);
            GlStateManager.scale(0.8F, 0.8F, 0.8F);
        }
    }
}
