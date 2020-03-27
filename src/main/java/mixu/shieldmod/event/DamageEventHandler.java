package mixu.shieldmod.event;

import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.network.PacketShieldTakenDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class DamageEventHandler {
    @SubscribeEvent
    public void onEntityDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (ShieldRender.playerShields.containsKey(event.getEntity())) {
                EntityPlayer player = (EntityPlayer) event.getEntity();
                event.setCanceled(true);
                ShieldRender.playerShields.get(player).setValue(ShieldRender.playerShields.get(player).getValue()-event.getAmount()/20);
                Shieldmod.network.sendToAllAround(new PacketShieldTakenDamage(player, event.getAmount()), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 60));
            }
        }
    }
}
