package com.sayuki.litematica.neoforge;

import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.gui.GuiConfigs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// NeoForge用エントリーポイント - クライアント限定で共通初期化と設定画面登録をやる
@Mod(value = "litematica", dist = Dist.CLIENT)
public class LitematicaNeoForge {
    public LitematicaNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // 共通初期化 - Fabric側と同じ流れでmasaのコードを呼ぶ
        Litematica.onInitialize();

        // 設定画面登録 - Mod一覧のボタンから開けるようにする、Supplierは明示キャストしないとオーバーロードで曖昧になる
        fi.dy.masa.malilib.registry.Registry.CONFIG_SCREEN.registerConfigScreenFactory(new fi.dy.masa.malilib.util.data.ModInfo("litematica", "Litematica", GuiConfigs::new));
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (java.util.function.Supplier<IConfigScreenFactory>) () -> (IConfigScreenFactory) (minecraft, parent) -> {
                    GuiConfigs gui = new GuiConfigs();
                    gui.setParent(parent);
                    return gui;
                });

        // ペイロード登録 - ModバスのイベントでServuxのlitematicsチャンネルを受信できるようにする
        modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {
            NeoForgePayloads.register(event.registrar("litematica"));
        });
    }
}
