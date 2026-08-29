package com.sayuki.litematica.platform;

import java.util.ServiceLoader;

// サービスを読み込む - 指定クラスの実装を取得、ServiceLoaderでプラットフォーム別の実装を拾ってくる
public class Services {
    // プラットフォームヘルパー - ゲームディレクトリとかバージョン取得とか、ここから呼ぶ
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    // サービスを読み込む - 指定クラスの実装を取得、見つからなかったら例外投げる
    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("サービスが見つからなかった: " + clazz.getName() + " - META-INF/servicesに登録されてるか確認して"));
    }
}
