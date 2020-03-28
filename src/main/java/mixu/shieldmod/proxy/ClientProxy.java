package mixu.shieldmod.proxy;

import codechicken.lib.texture.TextureUtils;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.client.gui.DrawShieldStatus;
import mixu.shieldmod.client.keybind.Keybinds;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.textures.TextureRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        OBJLoader.INSTANCE.addDomain(ShieldMod.MODID);

        TextureUtils.addIconRegister(new TextureRegister());
        ShieldMod.logger.info("PreInit done!");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        registerEventHandlersClient();
        initNetwork();
        Keybinds.registerKeybinds();
        ShieldMod.logger.info("Init done!");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ShieldMod.logger.info("PostInit done!");
    }


    @Override
    public EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public void registerEventHandlersClient() {
        ShieldMod.logger.info("Registering client event handlers");
        MinecraftForge.EVENT_BUS.register(new ShieldRender());
        MinecraftForge.EVENT_BUS.register(new DrawShieldStatus());
        ShieldMod.logger.info("Registered client event handlers");
    }
}
