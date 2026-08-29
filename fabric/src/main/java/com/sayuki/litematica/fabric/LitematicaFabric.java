package com.sayuki.litematica.fabric;

import net.fabricmc.api.ModInitializer;
import fi.dy.masa.litematica.Litematica;

// Fabric用エントリーポイント - fabric.mod.jsonから呼ばれる、共通初期化に丸投げする
// MOD自体はクライアント限定(environment: client)なのでModInitializerで問題ない
public class LitematicaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // 共通初期化を呼ぶ - ローダーに依存しない処理は全部ここでやる
        Litematica.onInitialize();
    }
}
