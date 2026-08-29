package fi.dy.masa.litematica.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fi.dy.masa.litematica.gui.GuiConfigs;

// ModMenu連携 - FabricでModMenuから設定画面を開けるようにする、NeoForgeでは使わない
public class ModMenuImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        // 設定画面を開く - 親スクリーンを覚えておかないと戻るボタンが効かなくなるのでsetParentする
        return (screen) -> {
            GuiConfigs gui = new GuiConfigs();
            gui.setParent(screen);
            return gui;
        };
    }
}
