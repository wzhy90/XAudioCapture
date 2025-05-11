package io.github.wzhy.xaudiocapture;

import android.annotation.SuppressLint;

import static io.github.wzhy.xaudiocapture.BuildConfig.APPLICATION_ID;

public class Constants {
    public static final String LOG_TAG = "[XAudioCapture]";

    @SuppressLint("SdCardPath")
    public static final String CONFIG_PATH_FORMAT = "/data/user_de/%d/" + APPLICATION_ID + "/config/%s";

    public static final int SORT_ORDER_LABEL = 0;
    public static final int SORT_ORDER_PACKAGE_NAME = 1;
    public static final int SORT_ORDER_INSTALL_TIME = 2;
    public static final int SORT_ORDER_UPDATE_TIME = 3;

    public static final String PREF_KEY_SORT_ORDER = "preference_sort_order";
    public static final String PREF_KEY_SHOW_SELECTED_FIRST = "preference_show_selected_first";
    public static final String PREF_KEY_SHOW_SYSTEM = "preference_show_system";

    private Constants() {
    }
}
