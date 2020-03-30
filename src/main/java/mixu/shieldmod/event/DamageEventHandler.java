package mixu.shieldmod.event;

import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.config.SMConfig;
import mixu.shieldmod.handler.ShieldStateHandler;
import mixu.shieldmod.network.PacketShieldStateUpdated;
import mixu.shieldmod.network.PacketShieldTakenDamage;
import net.minecraft.client.particle.ParticleDrip;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class DamageEventHandler {

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) { //Attacked instead of hurt so the hurt animation doesn't play
        if (event.getAmount() == Float.MAX_VALUE) {return;} //This is kill command damage value, if dealt to entity then ignore shields

        if (event.getEntity() instanceof EntityPlayer) {
            if (!event.getEntityLiving().world.isRemote) {
                if (ShieldStateHandler.shieldStates.containsKey((EntityPlayer) event.getEntity())) {
                    EntityPlayer player = (EntityPlayer) event.getEntity();

                    //If player parried
                    if (ShieldStateHandler.parryTime.get(player) > 0) {
                        event.setCanceled(true);
                        //TODO: Fancy parrying effect
                        ShieldStateHandler.parryTime.replace(player, 0);
                        return;
                    }

                    //If player shield state isn't broken or enabled
                    if (ShieldStateHandler.shieldStates.get(player).getKey() != ShieldStateHandler.ShieldState.ENABLED) {
                        if (ShieldStateHandler.shieldStates.get(player).getKey() != ShieldStateHandler.ShieldState.BROKEN) {
                            return;
                        }
                    }

                    if (ShieldStateHandler.shieldStates.get(player).getKey() == ShieldStateHandler.ShieldState.BROKEN) {
                        ShieldMod.logger.info("Player "+player.getName()+" got hit while shield was broken");
                        //Player hit, repair their shield partially
                        float currHealth = ShieldStateHandler.shieldStates.get(player).getValue();
                        ShieldStateHandler.repairShieldPartial(player, currHealth);
                        return;
                    }

                    event.setCanceled(true);
                    ShieldMod.logger.info(event.isCanceled());

                    float currHealth = ShieldStateHandler.shieldStates.get(player).getValue();
                    float newHealth = currHealth - event.getAmount() / SMConfig.ShieldProperties.shieldHealthMax;

                    ShieldStateHandler.shieldStates.get(player).setValue(newHealth);
                    ShieldMod.network.sendTo(new PacketShieldTakenDamage(player, newHealth), (EntityPlayerMP) player);
                    ShieldMod.network.sendToAllTracking(new PacketShieldTakenDamage(player, newHealth),player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (!event.getEntityLiving().world.isRemote) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                if (ShieldStateHandler.shieldStates.containsKey(player)) {
                    ShieldStateHandler.repairShield(player);
                }
            }
        }
    }
}
