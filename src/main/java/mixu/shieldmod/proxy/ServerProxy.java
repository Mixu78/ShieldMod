package mixu.shieldmod.proxy;

import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.network.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ServerProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void initNetwork() {
        Shieldmod.network = NetworkRegistry.INSTANCE.newSimpleChannel(Shieldmod.NETWORK_CHANNEL_NAME);
        Shieldmod.network.registerMessage(PacketShieldActivatedClient.Handler.class, PacketShieldActivatedClient.class, 0, Side.CLIENT);
        Shieldmod.network.registerMessage(PacketShieldDeactivatedClient.Handler.class, PacketShieldDeactivatedClient.class, 1, Side.CLIENT);
        Shieldmod.network.registerMessage(PacketShieldTakenDamage.Handler.class, PacketShieldTakenDamage.class, 2, Side.CLIENT);
        Shieldmod.network.registerMessage(PacketShieldActivatedServer.Handler.class, PacketShieldActivatedServer.class, 3, Side.SERVER);
        Shieldmod.network.registerMessage(PacketShieldDeactivatedServer.Handler.class, PacketShieldDeactivatedServer.class, 4, Side.SERVER);
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return null;
    }
}
