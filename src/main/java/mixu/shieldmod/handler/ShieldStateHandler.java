package mixu.shieldmod.handler;

import com.github.mixu78.mixulib.lib.KVPair;
import com.google.common.collect.Maps;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.network.PacketShieldStateUpdated;
import mixu.shieldmod.potion.PotionShieldBroken;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.UUID;

public class ShieldStateHandler {

    public enum ShieldState {
        //Shield state enums
        ENABLED (1),
        DISABLED (0),
        BROKEN (2),
        UNKNOWN (-1);

        private final int levelID;

        ShieldState(int levelID) {
            this.levelID = levelID;
        }

        public int getLevelID() {
            return this.levelID;
        }

        public static ShieldState getByID(int id) {
            for(ShieldState e : values()) {
                if(e.levelID == id) return e;
            }
            return UNKNOWN;
        }
    }

    public static Map<EntityPlayer, KVPair<ShieldState, Float>> shieldStates = Maps.newHashMap();
    public static Map<EntityPlayer, Integer> parryTime = Maps.newHashMap();

    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(UUID.fromString("57d535ea-dd8e-43de-b1cb-49e05622bc1b"), "Shieldmod Speed Modifier", -5D, 0);

    //SubscribeEvent
    public void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            if (event.getEntityLiving().getActivePotionEffects().contains(new PotionShieldBroken())) {
                event.getEntityLiving().setVelocity(0,0,0);
            }
        }
    }

    @SubscribeEvent
    public void onTickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.SERVER || event.side != Side.SERVER) {
            return;
        }
        if (shieldStates.size() <= 0) {
            return;
        }

        //For each parryTime, decrease by 1 until 0
        parryTime.forEach((player, timeLeft) -> {
            if (timeLeft > 0) {
                timeLeft--;
                parryTime.replace(player, timeLeft);
            }
        });

        //For each enabled shield, decrease by 0.005F. For each disabled shield, increase by 0.005F until hitting 1F
        shieldStates.forEach((player, stateAndHealth) -> {
            if (stateAndHealth.getKey() == ShieldState.ENABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() - 0.005F);
            }
            else if (stateAndHealth.getKey() == ShieldState.DISABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            } else if (stateAndHealth.getKey() == ShieldState.BROKEN) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            }
        });

        //For each shield
        shieldStates.forEach((player, stateAndHealth) -> {
            if (stateAndHealth.getValue() <= 0F) {
                //If health < 0, set shield to broken
                stateAndHealth.setKey(ShieldState.BROKEN);

                ShieldMod.logger.info("Player "+player.getName()+"'s shield broke!");

                ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldState.BROKEN, player, 0F), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldState.BROKEN, player, 0F), player);

                player.addPotionEffect(new PotionEffect(new PotionShieldBroken(), 2));

                stateAndHealth.setKey(ShieldState.BROKEN);
                stateAndHealth.setValue(0F);
            }
            if (stateAndHealth.getKey() == ShieldState.BROKEN && stateAndHealth.getValue() <= 1F) {
                player.addPotionEffect(new PotionEffect(new PotionShieldBroken(), 2));
            }
            //If shield is broken but now has full health
            if (stateAndHealth.getKey() == ShieldState.BROKEN && stateAndHealth.getValue() == 1F) {

                ShieldMod.logger.info("Player "+player.getName()+"'s shield is now usable again");

                player.removePotionEffect(new PotionShieldBroken());

                stateAndHealth.setKey(ShieldState.DISABLED);

                ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldState.DISABLED, player, 1F), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldState.DISABLED, player, 1F), player);
            }
        });
    }

    public static void repairShield(EntityPlayer player) {
        player.setGlowing(false);
        player.removePotionEffect(new PotionShieldBroken());
        shieldStates.get(player).setKey(ShieldState.DISABLED);
        shieldStates.get(player).setValue(1F);
        ShieldMod.logger.info("Repaired player "+player.getName()+"'s shield");
        ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldState.DISABLED, player, 1F), (EntityPlayerMP) player);
        ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldState.DISABLED, player, 1F), player);
    }

    public static void repairShieldPartial(EntityPlayer player, float shieldHealth) {
        if (shieldHealth > 1F) {shieldHealth = 1F;}
        player.setGlowing(false);
        player.removePotionEffect(new PotionShieldBroken());
        shieldStates.get(player).setKey(ShieldState.DISABLED);
        shieldStates.get(player).setValue(shieldHealth);
        ShieldMod.logger.info("Repaired player "+player.getName()+"'s shield to "+shieldHealth);
        ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldState.DISABLED, player, shieldHealth), (EntityPlayerMP) player);
        ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldState.DISABLED, player, shieldHealth), player);
    }
}
