package mixu.shieldmod.client.keybind;

import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.handler.ShieldStateHandler;
import mixu.shieldmod.network.PacketShieldStateUpdated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeybindHandler {
    boolean stateSent = false;

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (Keybinds.shieldKey.isKeyDown()) {
            if (!stateSent) {
                ShieldMod.network.sendToServer(new PacketShieldStateUpdated(ShieldStateHandler.ShieldState.ENABLED));
                stateSent = true;
            }
        }
        else if (!Keybinds.shieldKey.isKeyDown()) {
            if (stateSent) {
                ShieldMod.network.sendToServer(new PacketShieldStateUpdated(ShieldStateHandler.ShieldState.DISABLED));
                stateSent = false;
            }
        }
        if (ShieldRender.playerShields.get(Minecraft.getMinecraft().player).getKey() == ShieldStateHandler.ShieldState.BROKEN) {
            KeyBinding.unPressAllKeys();
        }
    }
}
