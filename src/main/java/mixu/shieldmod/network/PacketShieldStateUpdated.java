package mixu.shieldmod.network;

import com.github.mixu78.mixulib.MixuLib;
import com.github.mixu78.mixulib.lib.KVPair;
import com.github.mixu78.mixulib.network.ExecMainThreadMessageHandler;
import io.netty.buffer.ByteBuf;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.handler.ShieldStateHandler;
import mixu.shieldmod.handler.ShieldStateHandler.ShieldStates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketShieldStateUpdated implements IMessage {

    private ShieldStates state;
    private int playerID;
    private boolean hasPlayer;
    private float health;

    public PacketShieldStateUpdated() {}

    public PacketShieldStateUpdated(ShieldStates state) {
        this.state = state;
        this.playerID = 0;
        this.hasPlayer = false;
        this.health = 100F;
    }

    public PacketShieldStateUpdated(ShieldStates state, EntityPlayer player, float health) {
        this.state = state;
        this.playerID = player.getEntityId();
        this.hasPlayer = true;
        this.health = health;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        state = ShieldStates.getByID(buf.readInt());
        playerID = buf.readInt();
        hasPlayer = buf.readBoolean();
        health = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(state.getLevelID());
        buf.writeInt(playerID);
        buf.writeBoolean(hasPlayer);
        buf.writeFloat(health);
    }

    public static class Handler extends ExecMainThreadMessageHandler<PacketShieldStateUpdated, IMessage> {
        @Override
        public IMessage handleMessage(PacketShieldStateUpdated message, MessageContext ctx) {
            //Packet to client
            if (ctx.side == Side.CLIENT) {
                if (message.hasPlayer) {
                    Entity entity = MixuLib.proxy.getClientWorld().getEntityByID(message.playerID);
                    if (entity instanceof EntityPlayer) {
                        //Entity was a player
                        EntityPlayer entityPlayer = (EntityPlayer) entity;
                        if (ShieldRender.playerShields.containsKey(entityPlayer)) {
                            //Player exists in playerShields, set client-sided state and health
                            ShieldMod.logger.info("Player "+entityPlayer.getName() + "'s shield updated, "+message.state.toString());
                            ShieldRender.playerShields.get(entityPlayer).setKey(message.state);
                            ShieldRender.playerShields.get(entityPlayer).setValue(message.health);
                        } else {
                            ShieldMod.logger.error("EntityPlayer "+entityPlayer.getName()+ " "+ entityPlayer.getClass() +" not found in player shields, adding them now");
                            //Player doesn't exist in playerShields, add them and set client-sided state and health
                            ShieldRender.playerShields.put(entityPlayer, new KVPair<>(message.state, message.health));
                        }
                    }
                }
                //Packet to server
            } else {
                //Check if shield broken, broken shield cannot be changed to enabled/disabled by client
                if (ShieldStateHandler.shieldStates.get(ctx.getServerHandler().player).getKey() == ShieldStates.BROKEN) {message.state = ShieldStates.BROKEN;}
                //Set server-sided state
                ShieldStateHandler.shieldStates.get(ctx.getServerHandler().player).setKey(message.state);
                EntityPlayer player = ctx.getServerHandler().player;
                //Send to all players rendering updated player a packet that state&health has been updated for sender player
                ShieldMod.network.sendTo(new PacketShieldStateUpdated(message.state, player, ShieldStateHandler.shieldStates.get(ctx.getServerHandler().player).getValue()), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(message.state, player, ShieldStateHandler.shieldStates.get(ctx.getServerHandler().player).getValue()), player);
            }
            return null;
        }
    }
}
