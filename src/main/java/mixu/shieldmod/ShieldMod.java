package mixu.shieldmod;

import mixu.shieldmod.client.keybind.KeybindHandler;
import mixu.shieldmod.event.DamageEventHandler;
import mixu.shieldmod.handler.PlayerJoinHandler;
import mixu.shieldmod.handler.ShieldStateHandler;
import mixu.shieldmod.potion.PotionRegisterer;
import mixu.shieldmod.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ShieldMod.MODID,
        name = ShieldMod.MODNAME,
        version = ShieldMod.VERSION,
        acceptableRemoteVersions = ShieldMod.VERSION
)
public class ShieldMod {

    public static final String MODID = "shieldmod";
    public static final String MODNAME = "ShieldMod";
    public static final String VERSION = "@VERSION@";
    public static final String NETWORK_CHANNEL_NAME = "ShieldMod";
    public static final String KEYBIND_CATEGORY_NAME = "ShieldMod";

    public static int ticksElapsedSinceStart = 0;

    @Mod.Instance(MODID)
    public static ShieldMod INSTANCE;

    @SidedProxy(clientSide = "mixu.shieldmod.proxy.ClientProxy", serverSide = "mixu.shieldmod.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODNAME);
        proxy.preInit(event);
        MinecraftForge.EVENT_BUS.register(new PotionRegisterer());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        registerEventHandlers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID))
        {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }

    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new DamageEventHandler());
        MinecraftForge.EVENT_BUS.register(new KeybindHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerJoinHandler());
        MinecraftForge.EVENT_BUS.register(new ShieldStateHandler());
    }
}
