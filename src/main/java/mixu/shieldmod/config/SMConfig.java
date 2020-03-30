package mixu.shieldmod.config;

import mixu.shieldmod.ShieldMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = ShieldMod.MODID, category = "")
public class SMConfig {
    @Name("Overlay configs")
    public static Overlay Overlay = new Overlay();
    @Name("Shield configs")
    public static ShieldProperties ShieldProperties = new ShieldProperties();

    public static class Overlay {
        public boolean enabled = true;
    }

    public static class ShieldProperties {
        @Name("Regeneration rate")
        @Comment({"The rate at which the shield",
                "regains health. This is per tick."})
        public double regenRate = 0.005D;
        @Name("Usage damage rate")
        @Comment({"The rate at which the shield",
                "loses health when active. This is per tick."})
        public double usageDamageRate = 0.005D;
        @Name("Shield health")
        @Comment("Max amount of health a shield can have")
        public int shieldHealthMax = 20;
        @Name("Parrying time")
        @Comment({"The amount of ticks after deactivating shield",
                "that the player will \"parry\",",
                "meaning the shield and player takes no damage from a hit that",
                "occurs during this time. When player is hit this time is reset."})
        public int parryTime = 2;

        //TODO: Fix actualRegen and actualDamage rates

        @Ignore
        public float actualRegenRate = (float) (regenRate/shieldHealthMax);

        @Ignore
        public float actualUsageDamageRate = (float) (usageDamageRate/shieldHealthMax);
    }
}
