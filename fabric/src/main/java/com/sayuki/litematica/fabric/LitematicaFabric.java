package com.sayuki.litematica.fabric;

import net.fabricmc.api.ModInitializer;
import fi.dy.masa.litematica.Litematica;

// Fabricエントリポイント - fabric.mod.jsonのmainから呼ばれる、共通初期化に橋渡しするだけ
// MOD自体はクライアント限定(environment: client)なのでModInitializerで問題ない
public class LitematicaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Litematica.onInitialize();
    }
}
