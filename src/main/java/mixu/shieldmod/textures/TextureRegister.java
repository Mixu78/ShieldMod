package mixu.shieldmod.textures;

import codechicken.lib.texture.TextureUtils;
import com.github.mixu78.mixulib.helpers.ResourceHelper;
import mixu.shieldmod.ShieldMod;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.client.renderer.texture.TextureMap;

public class TextureRegister implements TextureUtils.IIconRegister {
    @Override
    public void registerIcons(TextureMap textureMap) {
        textureMap.registerSprite(ResourceHelper.getResource(ShieldMod.MODID, "models/shield/shield_sphere"));
        textureMap.registerSprite(ResourceHelper.getResource(ShieldMod.MODID, "gui/shield_health_bar"));

        ShieldRender.shieldModel = null;
    }
}
