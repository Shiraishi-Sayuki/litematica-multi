package fi.dy.masa.litematica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fi.dy.masa.malilib.event.InitializationHandler;

// 共通MODメインクラス - FabricとNeoForgeどっちからも呼ばれる、ローダーに依存しない初期化をまとめる
public class Litematica
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    // 共通初期化 - 各ローダーのエントリーポイントから呼ぶ、InitHandlerを登録するだけ
    public static void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    // デバッグログ - 設定で有効のときだけ出す、うるさくならないようにする
    public static void debugLog(String msg, Object... args)
    {
        if (fi.dy.masa.litematica.config.Configs.Generic.DEBUG_LOGGING.getBooleanValue() || Reference.DEBUG_MODE)
        {
            String message = "[DEBUG] "+msg;
            Litematica.LOGGER.info(message, args);
        }
    }
}
