package mixu.shieldmod.potion;

import mixu.shieldmod.ShieldMod;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionRegisterer {
    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(new PotionShieldBroken());
        ShieldMod.logger.info("Registered potions");
    }
}
