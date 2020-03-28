package mixu.shieldmod.event;

import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.handler.ShieldStateHandler;
import mixu.shieldmod.network.PacketShieldTakenDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class DamageEventHandler {

    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ShieldStateHandler.shieldStates.containsKey((EntityPlayer) event.getEntity())) {
                EntityPlayer player = (EntityPlayer) event.getEntity();
                if (ShieldStateHandler.shieldStates.get(player).getKey() != ShieldStateHandler.ShieldStates.ENABLED) return;
                event.setCanceled(true);
                float currHealth = ShieldStateHandler.shieldStates.get(player).getValue();
                float newHealth = currHealth - event.getAmount()/20;
                ShieldStateHandler.shieldStates.get(player).setValue(newHealth);
                ShieldMod.network.sendTo(new PacketShieldTakenDamage(player, newHealth), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllAround(new PacketShieldTakenDamage(player, newHealth), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 60));
            }
        }
    }
}
