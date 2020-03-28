package mixu.shieldmod.client.gui;

import com.github.mixu78.mixulib.helpers.MathHelper;
import com.github.mixu78.mixulib.lib.KVPair;
import com.github.mixu78.mixulib.util.Colors;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.handler.ShieldStateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;

public class DrawShieldStatus extends Gui{

    private static ResourceLocation shieldHealthbar = new ResourceLocation(ShieldMod.MODID, "textures/gui/shield_health_bar.png");
    int ticksElapsed = 0;

    @SubscribeEvent
    public void onRenderExpBar(RenderGameOverlayEvent.Post event) {
        EntityPlayer localPlayer = Minecraft.getMinecraft().player;
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

        Color uuidRGB = Colors.UUID2RGB(Minecraft.getMinecraft().player.getUniqueID());

        float r = ((float) uuidRGB.getRed())/255F;
        float g = ((float) uuidRGB.getGreen())/255F;
        float b = ((float) uuidRGB.getBlue())/255F;
        float a = 1F;

        if (!ShieldRender.playerShields.containsKey((EntityPlayer) localPlayer)) {
            ShieldRender.playerShields.put((EntityPlayer) localPlayer, new KVPair<>(ShieldStateHandler.ShieldStates.DISABLED, 1F));
        }

        if (ShieldRender.playerShields.get(localPlayer).getKey() == ShieldStateHandler.ShieldStates.BROKEN) {
            r = 0.5F;
            g = 0f;
            b = 0F;
            a = 1F;
        }

        ScaledResolution scaled = new ScaledResolution(Minecraft.getMinecraft());

        int xPos = 239/2560*scaled.getScaledWidth();
        int yPos = 10/2560*scaled.getScaledHeight()+scaled.getScaledHeight()-10;

        Minecraft mc = Minecraft.getMinecraft();

        mc.getTextureManager().bindTexture(shieldHealthbar);

        GlStateManager.pushAttrib();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        //Draw "container" of shield health bar
        drawTexturedModalRect(xPos, yPos, 0, 0, 56, 9);

        int shieldbarWidth = (int)((ShieldRender.localPlayerShieldSize / 1F) * 49);
        //Color into player shield color
        GlStateManager.color(r, g, b, a);
        //Draw the actual bar
        drawTexturedModalRect(xPos + 3, yPos + 3, 0, 9, shieldbarWidth, 3);
        //Back to normal colors
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (ticksElapsed >= 10 && (int)((ShieldRender.localPlayerShieldSize / 1F)*100) <= 50) {
            drawTexturedModalRect(xPos, yPos - 30, 62, 0, 7, 19);
        }

        if (ticksElapsed >= 20) {
            ticksElapsed = 0;
        }

        String s = "Shield health: " + Math.round((double) ShieldRender.localPlayerShieldSize*20D) + "/" + "20";
        yPos -= 10;
        mc.fontRenderer.drawString(s, xPos, yPos, 0);
        GlStateManager.popAttrib();
    }

    @SubscribeEvent
    public void onTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.CLIENT || event.side != Side.CLIENT) {
            return;
        }

        ticksElapsed++;
    }
}
