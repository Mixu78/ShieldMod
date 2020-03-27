package mixu.shieldmod.client.keybind;

import com.github.mixu78.mixulib.lib.KVPair;
import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.client.render.ShieldRender;
import mixu.shieldmod.network.PacketShieldActivatedClient;
import mixu.shieldmod.network.PacketShieldActivatedServer;
import mixu.shieldmod.network.PacketShieldDeactivatedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeybindHandler {
    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        boolean packetSent = false;

        if (Keybinds.shieldKey.isKeyDown()) {
            if (ShieldRender.playerShields.containsKey(Shieldmod.proxy.getLocalPlayer())) {
                ShieldRender.playerShields.get(Shieldmod.proxy.getLocalPlayer()).setKey(true);
                if (!packetSent) {
                    Shieldmod.network.sendToServer(new PacketShieldActivatedServer(Shieldmod.proxy.getLocalPlayer(), ShieldRender.playerShields.get(Shieldmod.proxy.getLocalPlayer()).getValue()));
                    packetSent = true;
                }
            }
            else ShieldRender.playerShields.put(Shieldmod.proxy.getLocalPlayer(), new KVPair<Boolean, Float>(true, 1F));
        }
        if (!Keybinds.shieldKey.isKeyDown()) {
            if (ShieldRender.playerShields.containsKey(Shieldmod.proxy.getLocalPlayer())) {
                ShieldRender.playerShields.get(Shieldmod.proxy.getLocalPlayer()).setKey(false);
                if (packetSent) {
                    Shieldmod.network.sendToServer(new PacketShieldDeactivatedServer(Shieldmod.proxy.getLocalPlayer()));
                    packetSent = false;
                }
            }
            else ShieldRender.playerShields.put(Shieldmod.proxy.getLocalPlayer(), new KVPair<Boolean, Float>(false, 1F));
        }
    }
}
