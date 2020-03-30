package mixu.shieldmod.potion;

import mixu.shieldmod.ShieldMod;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PotionShieldBroken extends Potion {

    public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(UUID.fromString("57d535ea-dd8e-43de-b1cb-49e05622bc1b"), "Shieldmod Speed Modifier", -5D, 0);

    public PotionShieldBroken() {
        super(true, 0);
        setRegistryName(ShieldMod.MODID, "shield_broken");
        setPotionName("potions.shield_broken");
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "ad7ea01d-65d8-4e3d-a735-d53ff5ef4ab2", -1D, 1);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLivingBaseIn;
            player.setVelocity(0D, 0D, 0D);
            player.velocityChanged = true;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }
}
