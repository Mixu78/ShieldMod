package mixu.shieldmod.textures;

import codechicken.lib.texture.TextureUtils;
import com.github.mixu78.mixulib.helpers.ResourceHelper;
import mixu.shieldmod.Shieldmod;
import mixu.shieldmod.client.render.ShieldRender;
import net.minecraft.client.renderer.texture.TextureMap;

public class ShieldTextureRegister implements TextureUtils.IIconRegister {
    @Override
    public void registerIcons(TextureMap textureMap) {
        textureMap.registerSprite(ResourceHelper.getResource(Shieldmod.MODID, "models/shield/shield_sphere"));

        ShieldRender.shieldModel = null;
    }
}
