/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package kihira.tails.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kihira.tails.api.IRenderHelper;
import kihira.tails.client.model.ModelPartBase;
import kihira.tails.client.texture.TextureHelper;
import kihira.tails.common.PartInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderPart {

    private static HashMap<Class<? extends EntityLivingBase>, IRenderHelper> renderHelpers = new HashMap<Class<? extends EntityLivingBase>, IRenderHelper>();

    protected String name;
    public final ModelPartBase modelPart;

    public RenderPart(String name, ModelPartBase modelPart) {
        this.name = name;
        this.modelPart = modelPart;
    }

    public void render(EntityLivingBase entity, PartInfo info, double x, double y, double z, float partialTicks) {
        if (info.needsTextureCompile || info.getTexture() == null) {
            info.setTexture(TextureHelper.generateTexture(info));
            info.needsTextureCompile = false;
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        IRenderHelper helper = getRenderHelper(entity.getClass());
        if (helper != null) {
            helper.onPreRenderTail(entity, this, info, x, y, z);
        }
        this.doRender(entity, info, partialTicks);
        GL11.glPopMatrix();
    }

    protected void doRender(EntityLivingBase entity, PartInfo info, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(info.getTexture());
        this.modelPart.render(entity, info.subid, partialTicks);
    }

    /**
     * Gets the available textures for this tail subid
     * @return Available textures
     * @param subid The subid
     */
    public abstract String[] getTextureNames(int subid);

    /**
     * Gets the available subtypes for this tail
     * @return subtypes
     */
    public abstract int getAvailableSubTypes();

    public String getUnlocalisedName(int subType) {
        return "tail."+this.name+"."+subType+".name";
    }

    public static void registerRenderHelper(Class<? extends EntityLivingBase> clazz, IRenderHelper helper) {
        if (!renderHelpers.containsKey(clazz) && helper != null) {
            renderHelpers.put(clazz, helper);
        }
        else {
            throw new IllegalArgumentException("An invalid RenderHelper was registered!");
        }
    }

    public static IRenderHelper getRenderHelper(Class<? extends EntityLivingBase> clazz) {
        if (renderHelpers.containsKey(clazz)) {
            return renderHelpers.get(clazz);
        }
        else return null;
    }
}