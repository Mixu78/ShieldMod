package mixu.shieldmod.network;

import com.github.mixu78.mixulib.network.WrapperMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.Shieldmod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketShieldDeactivatedServer implements IMessage {
    public int playerID;

    public PacketShieldDeactivatedServer() {}

    public PacketShieldDeactivatedServer(EntityPlayer player) {
        this.playerID = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerID);
    }

    public static class Handler extends WrapperMessageHandler<PacketShieldDeactivatedServer, IMessage> {
        @Override
        public IMessage handleMessage(PacketShieldDeactivatedServer message, MessageContext ctx) {
            Entity entity = FMLServerHandler.instance().getServer().getEntityWorld().getEntityByID(message.playerID);

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                Shieldmod.network.sendToAllAround(new PacketShieldDeactivatedClient(player), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 60));
            }
            return null;
        }
    }
}
