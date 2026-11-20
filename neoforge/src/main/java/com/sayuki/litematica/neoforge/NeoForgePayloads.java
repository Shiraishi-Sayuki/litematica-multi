package com.sayuki.litematica.neoforge;

import fi.dy.masa.litematica.network.ServuxLitematicaHandler;
import fi.dy.masa.litematica.network.ServuxLitematicaPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

// NeoForge用ペイロード登録 - Servuxのlitematicsチャンネルの受信を共通ハンドラに橋渡しする
public class NeoForgePayloads {
    // 登録処理 - registrarはRegisterPayloadHandlersEventからもらう、playToClientで受信だけ受ける
    // 受信処理はメインスレッドでやらないとワールドアクセスが危ないのでenqueueWorkで投げる
    public static void register(PayloadRegistrar registrar) {
        // optional() を付けないと NeoForge はこのチャンネルを「必須」とみなし、
        // Servux の無いサーバーへ接続しただけで mod 不一致で切断される
        registrar.optional().playToClient(ServuxLitematicaPacket.Payload.ID, ServuxLitematicaPacket.Payload.CODEC, (payload, context) ->
                context.enqueueWork(() -> ServuxLitematicaHandler.getInstance().receivePlayPayload(payload, null)));
    }
}
