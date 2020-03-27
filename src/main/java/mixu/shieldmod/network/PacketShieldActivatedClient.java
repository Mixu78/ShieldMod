package mixu.shieldmod.network;

import com.github.mixu78.mixulib.MixuLib;
import com.github.mixu78.mixulib.network.WrapperMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketShieldActivatedClient implements IMessage {
    public int playerID;
    public float shieldPowerF;

    public PacketShieldActivatedClient() {}

    public PacketShieldActivatedClient(EntityPlayer player, float shieldPower) {
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

    public static class Handler extends WrapperMessageHandler<PacketShieldActivatedClient, IMessage> {

        @Override
        public IMessage handleMessage(PacketShieldActivatedClient message, MessageContext ctx) {
            Entity entity = MixuLib.proxy.getClientWorld().getEntityByID(message.playerID);

            if (entity instanceof EntityPlayer) {
                ShieldRender.playerShields.get(entity).setValue(message.shieldPowerF);
                ShieldRender.playerShields.get(entity).setKey(true);
            }
            return null;
        }
    }
}
