package mixu.shieldmod.handler;

import com.github.mixu78.mixulib.lib.KVPair;
import com.google.common.collect.Maps;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.network.PacketShieldStateUpdated;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.UUID;

public class ShieldStateHandler {

    public enum ShieldStates {
        //Shield state enums
        ENABLED (1),
        DISABLED (0),
        BROKEN (2),
        UNKNOWN (-1);

        private final int levelID;

        ShieldStates(int levelID) {
            this.levelID = levelID;
        }

        public int getLevelID() {
            return this.levelID;
        }

        public static ShieldStates getByID(int id) {
            for(ShieldStates e : values()) {
                if(e.levelID == id) return e;
            }
            return UNKNOWN;
        }
    }

    public static Map<EntityPlayer, KVPair<ShieldStates, Float>> shieldStates = Maps.newHashMap();
    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(UUID.fromString("57d535ea-dd8e-43de-b1cb-49e05622bc1b"), "Shieldmod Speed Modifier", -5D, 0);

    @SubscribeEvent
    public void onTickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.type != TickEvent.Type.SERVER || event.side != Side.SERVER) {
            return;
        }
        if (shieldStates.size() <= 0) {
            return;
        }

        //For each enabled shield, decrease by 0.005F. For each disabled shield, increase by 0.005F until hitting 1F
        shieldStates.forEach((player, stateAndHealth) -> {
            if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.ENABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() - 0.005F);
            }
            else if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.DISABLED) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            } else if (stateAndHealth.getKey() == ShieldStateHandler.ShieldStates.BROKEN) {
                stateAndHealth.setValue(stateAndHealth.getValue() >= 1F ? 1F : stateAndHealth.getValue() + 0.005F);
            }
        });

        //For each shield
        shieldStates.forEach((player, stateAndHealth) -> {
            if (stateAndHealth.getValue() <= 0F) {
                //If health < 0, set shield to broken
                stateAndHealth.setKey(ShieldStates.BROKEN);
                ShieldMod.logger.info("Player "+player.getName()+"'s shield broke!");
                ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldStates.BROKEN, player, 0F), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldStates.BROKEN, player, 0F), player);
                //TODO: Stun player for x seconds
                player.setGlowing(true);
                //Doesn't work, jumping bypasses
                player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(SPEED_MODIFIER);
                stateAndHealth.setKey(ShieldStates.BROKEN);
                stateAndHealth.setValue(0F);
            }
            //If shield is broken but now has full health
            if (stateAndHealth.getKey() == ShieldStates.BROKEN && stateAndHealth.getValue() == 1F) {
                ShieldMod.logger.info("Player "+player.getName()+"'s shield is now usable again");
                player.setGlowing(false);
                player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER);
                stateAndHealth.setKey(ShieldStates.DISABLED);
                ShieldMod.network.sendTo(new PacketShieldStateUpdated(ShieldStates.DISABLED, player, 1F), (EntityPlayerMP) player);
                ShieldMod.network.sendToAllTracking(new PacketShieldStateUpdated(ShieldStates.DISABLED, player, 1F), player);
            }
        });
    }
}
