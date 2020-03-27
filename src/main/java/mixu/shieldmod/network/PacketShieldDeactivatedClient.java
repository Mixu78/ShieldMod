package mixu.shieldmod.network;

import com.github.mixu78.mixulib.MixuLib;
import com.github.mixu78.mixulib.network.WrapperMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketShieldDeactivatedClient implements IMessage {
    public int playerID;

    public PacketShieldDeactivatedClient() {}

    public PacketShieldDeactivatedClient(EntityPlayer player) {
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

    public static class Handler extends WrapperMessageHandler<PacketShieldDeactivatedClient, IMessage> {

        @Override
        public IMessage handleMessage(PacketShieldDeactivatedClient message, MessageContext ctx) {
            Entity entity = MixuLib.proxy.getClientWorld().getEntityByID(message.playerID);

            if (entity instanceof EntityPlayer) {
                ShieldRender.playerShields.get(entity).setKey(false);
            }
            return null;
        }
    }
}
