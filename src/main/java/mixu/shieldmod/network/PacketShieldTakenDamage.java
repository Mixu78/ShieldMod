package mixu.shieldmod.network;

import com.github.mixu78.mixulib.MixuLib;
import com.github.mixu78.mixulib.network.WrapperMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketShieldTakenDamage implements IMessage {
    public int playerID;
    public float damageTaken;

    public PacketShieldTakenDamage() {}

    public PacketShieldTakenDamage(EntityPlayer player, float damageTaken) {
        this.playerID = player.getEntityId();
        this.damageTaken = damageTaken;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = buf.readInt();
        damageTaken = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerID);
        buf.writeFloat(damageTaken);
    }

    public static class Handler extends WrapperMessageHandler<PacketShieldTakenDamage, IMessage> {
        @Override
        public IMessage handleMessage(PacketShieldTakenDamage message, MessageContext ctx) {
            Entity entity = MixuLib.proxy.getClientWorld().getEntityByID(message.playerID);
            if (entity instanceof EntityPlayer) {
                if (ShieldRender.playerShields.containsKey(entity)) {
                    float currDamage = ShieldRender.playerShields.get(entity).getValue();
                    ShieldRender.playerShields.get(entity).setValue(currDamage-message.damageTaken);
                }
            }
            return null;
        }
    }
}
