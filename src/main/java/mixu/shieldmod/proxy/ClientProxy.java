package mixu.shieldmod.proxy;

import codechicken.lib.texture.TextureUtils;
import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.client.keybind.Keybinds;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.textures.ShieldTextureRegister;
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
        OBJLoader.INSTANCE.addDomain(Shieldmod.MODID);

        TextureUtils.addIconRegister(new ShieldTextureRegister());
        Shieldmod.logger.info("PreInit done!");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        registerEventHandlersClient();
        Keybinds.registerKeybinds();
        Shieldmod.logger.info("Init done!");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        Shieldmod.logger.info("PostInit done!");
    }

    @Override
    public void initNetwork() {
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public void registerEventHandlersClient() {
        MinecraftForge.EVENT_BUS.register(new ShieldRender());
    }
}
