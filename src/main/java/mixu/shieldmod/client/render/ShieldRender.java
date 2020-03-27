package mixu.shieldmod.client.render;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.github.mixu78.mixulib.helpers.RenderHelper;
import com.github.mixu78.mixulib.helpers.ResourceHelper;
import com.github.mixu78.mixulib.lib.KVPair;
import com.github.mixu78.mixulib.render.ModelRenderer;
import com.github.mixu78.mixulib.util.Colors;
import com.google.common.collect.Maps;
import mixu.shieldmod.Shieldmod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public class ShieldRender {
    public static Map<EntityPlayer, KVPair<Boolean, Float>> playerShields = Maps.newHashMap();
    public static float localPlayerShieldSize;
    public static IBakedModel shieldModel;

    @SubscribeEvent
    public void onTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT) {
            return;
        }
        if (!playerShields.containsKey(Shieldmod.proxy.getLocalPlayer())) {
            playerShields.put(Shieldmod.proxy.getLocalPlayer(), new KVPair<>(false, 1F));
        }
        if (playerShields.get(Shieldmod.proxy.getLocalPlayer()).getKey()) {
            localPlayerShieldSize -= 0.005F;
            playerShields.get(Minecraft.getMinecraft().player).setValue(localPlayerShieldSize);
        }
        else localPlayerShieldSize += localPlayerShieldSize >= 1 ? 0F : 0.005F;

        playerShields.forEach((player, colorAndPower) -> colorAndPower.setValue(colorAndPower.getKey() ? player == Minecraft.getMinecraft().player ? 0F: colorAndPower.getValue() - 0.005F : 0F));

    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {
        if (playerShields.containsKey(event.getEntityPlayer())) {
            ResourceLocation shieldLocation = ResourceHelper.getResource(Shieldmod.MODID, "models/shield/shield_sphere.obj");
            if (shieldModel == null) {
                try {
                    OBJLoader.INSTANCE.loadModel(shieldLocation).bake(TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.BLOCK, TextureUtils.bakedTextureGetter);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();

            int argb = Colors.UUID2ARGB(event.getEntityPlayer().getUniqueID());
            float playerShieldSize = playerShields.get(event.getEntityPlayer()).getValue();

            RenderHelper.translateToPlayerCenter(event.getEntityPlayer(), Minecraft.getMinecraft().player, event.getPartialRenderTick());

            GlStateManager.scale(playerShieldSize, playerShieldSize, playerShieldSize);

            GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            ModelRenderer.renderBakedQuadsARGB(shieldModel.getQuads(null, null, 0), argb);

            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.popMatrix();
        }
    }
}
