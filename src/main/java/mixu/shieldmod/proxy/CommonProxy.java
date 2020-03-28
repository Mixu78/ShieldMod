package mixu.shieldmod.proxy;

import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.network.PacketShieldStateUpdated;
import mixu.shieldmod.network.PacketShieldTakenDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public abstract class CommonProxy {
    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);

    public void initNetwork() {
            ShieldMod.logger.info("Adding packet channel");
            ShieldMod.network = NetworkRegistry.INSTANCE.newSimpleChannel(ShieldMod.NETWORK_CHANNEL_NAME);
            ShieldMod.logger.info("Added channel, starting to register packets");
            ShieldMod.network.registerMessage(PacketShieldTakenDamage.Handler.class, PacketShieldTakenDamage.class, 0, Side.CLIENT);
            ShieldMod.network.registerMessage(PacketShieldStateUpdated.Handler.class, PacketShieldStateUpdated.class, 1, Side.CLIENT);
            ShieldMod.network.registerMessage(PacketShieldStateUpdated.Handler.class, PacketShieldStateUpdated.class, 2, Side.SERVER);
            ShieldMod.logger.info("Registered packets!");
    }

    public abstract EntityPlayer getLocalPlayer();
}
