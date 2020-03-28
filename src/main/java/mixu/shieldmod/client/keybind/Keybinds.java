package mixu.shieldmod.client.keybind;

import mixu.shieldmod.ShieldMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class Keybinds {

    public static KeyBinding shieldKey = new KeyBinding("Activate shield(hold)", Keyboard.KEY_J, ShieldMod.KEYBIND_CATEGORY_NAME);

    public static void registerKeybinds() {
        ClientRegistry.registerKeyBinding(shieldKey);
    }
}
