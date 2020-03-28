package mixu.shieldmod.handler;

import com.github.mixu78.mixulib.lib.KVPair;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerJoinHandler {
    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        //When a player joins server, add them to the shieldStates table
        ShieldStateHandler.shieldStates.put(event.player, new KVPair<>(ShieldStateHandler.ShieldStates.DISABLED, 1F));
    }
}
