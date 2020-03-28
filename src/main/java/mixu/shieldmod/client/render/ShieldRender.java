package mixu.shieldmod.client.render;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.github.mixu78.mixulib.helpers.RenderHelper;
import com.github.mixu78.mixulib.helpers.ResourceHelper;
import com.github.mixu78.mixulib.lib.KVPair;
import com.github.mixu78.mixulib.render.ModelRenderer;
import com.github.mixu78.mixulib.util.Colors;
import com.google.common.collect.Maps;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.handler.ShieldStateHandler;
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
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ShieldRender {
    public static Map<EntityPlayer, KVPair<ShieldStateHandler.ShieldStates, Float>> playerShields = Maps.newHashMap();
    public static float localPlayerShieldSize;
    public static IBakedModel shieldModel;
    private static EntityPlayer localPlayer = null;

    @SubscribeEvent
    public void onTickEnd(TickEvent.ClientTickEvent event) {
        if (localPlayer == null) {
            localPlayer = Minecraft.getMinecraft().player;
        }

        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT) {
            return;
        }

        //If client is not in shields list
        if (!playerShields.containsKey((EntityPlayer) localPlayer)) {
            playerShields.put((EntityPlayer) localPlayer, new KVPair<>(ShieldStateHandler.ShieldStates.DISABLED, 1F));
        }
    if (Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().isIntegratedServerRunning()) return;
        //For each enabled shield, decrease by 0.005F. For each disabled shield, increase by 0.005F until hitting 1F
        playerShields.forEach((player, stateAndHealth) -> {
            if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.ENABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() - 0.005F);
            }
            else if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.DISABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            } else if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.BROKEN) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            }
        });
        localPlayerShieldSize = playerShields.get(localPlayer).getValue();
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {

        if (!playerShields.containsKey((EntityPlayer) event.getEntityPlayer())) {
            playerShields.put((EntityPlayer) event.getEntityPlayer(), new KVPair<>(ShieldStateHandler.ShieldStates.DISABLED, 1F));
        }

        //Load the shield object
        ResourceLocation shieldLocation = ResourceHelper.getResource(ShieldMod.MODID, "models/shield/shield_sphere.obj");
        if (shieldModel == null) {
            try {
                shieldModel = OBJLoader.INSTANCE.loadModel(shieldLocation).bake(TransformUtils.DEFAULT_BLOCK, DefaultVertexFormats.BLOCK, TextureUtils.bakedTextureGetter);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        //If shield is broken/disabled/unknown, stop
        if (!playerShields.get((EntityPlayer) event.getEntityPlayer()).getKey().equals(ShieldStateHandler.ShieldStates.ENABLED)) {
            return;
        }
        //Just in case shieldModel is null for some wack reason
        if (shieldModel == null) {
            return;
        }


        GlStateManager.pushMatrix();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();

        //Get color from player UUID, get shield size
        int argb = Colors.UUID2ARGB(event.getEntityPlayer().getUniqueID());
        float playerShieldSize = playerShields.get((EntityPlayer) event.getEntityPlayer()).getValue();

        if (localPlayer == null) {
            localPlayer = Minecraft.getMinecraft().player;
        }

        //Translate to the center of player being rendered
        RenderHelper.translateToPlayerCenter((EntityPlayer) event.getEntityPlayer(), (EntityPlayer) localPlayer, event.getPartialRenderTick());

        GlStateManager.scale(playerShieldSize, playerShieldSize, playerShieldSize);

        GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        //Draw shield
        ModelRenderer.renderBakedQuadsARGB(shieldModel.getQuads(null, null, 0), argb);

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }
}
