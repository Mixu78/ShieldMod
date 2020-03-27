package mixu.shieldmod.network;

import com.github.mixu78.mixulib.MixuLib;
import com.github.mixu78.mixulib.network.WrapperMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketShieldActivatedServer implements IMessage {
    public int playerID;
    public float shieldPowerF;

    public PacketShieldActivatedServer() {}

    public PacketShieldActivatedServer(EntityPlayer player, float shieldPower) {
        this.playerID = player.getEntityId();
        this.shieldPowerF = shieldPower;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = buf.readInt();
        shieldPowerF = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerID);
        buf.writeFloat(shieldPowerF);
    }

    public static class Handler extends WrapperMessageHandler<PacketShieldActivatedServer, IMessage> {

        @Override
        public IMessage handleMessage(PacketShieldActivatedServer message, MessageContext ctx) {
            Entity entity = FMLServerHandler.instance().getServer().getEntityWorld().getEntityByID(message.playerID);

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                Shieldmod.network.sendToAllAround(new PacketShieldActivatedClient(player, message.shieldPowerF), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 60));
            }

            return null;
        }
    }
}
