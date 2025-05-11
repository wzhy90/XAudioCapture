package io.github.wzhy.xaudiocapture.xposed;

import static android.util.Log.getStackTraceString;
import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static io.github.wzhy.xaudiocapture.Constants.LOG_TAG;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.media.AudioAttributes;
import android.os.Build;

import androidx.annotation.Keep;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.wzhy.xaudiocapture.Configuration;

@Keep
public class HookMain implements IXposedHookLoadPackage {

    // taken from ApplicationInfo.java
    // https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/android/content/pm/ApplicationInfo.java
    private static final int PRIVATE_FLAG_ALLOW_AUDIO_PLAYBACK_CAPTURE = 1 << 27;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if ("android".equals(lpparam.packageName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                _hookPostTiramisu(lpparam);
            } else {
                _hookPreTiramisu(lpparam);
            }
        } else {
            _hookAPP(lpparam);
        }
    }

    private void _hookAPP(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log(LOG_TAG + ": " + lpparam.packageName + " Hooked!");
        Class<?> builderClass = XposedHelpers.findClass(
                "android.media.AudioAttributes.Builder",
                lpparam.classLoader
        );
        XposedBridge.hookAllMethods(
                builderClass,
                "setAllowedCapturePolicy",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = AudioAttributes.ALLOW_CAPTURE_BY_ALL;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.args[0] = AudioAttributes.ALLOW_CAPTURE_BY_ALL;
                        XposedBridge.log(LOG_TAG + ": " + lpparam.packageName + " setAllowedCapturePolicy to: " + param.args[0]);
                    }
                }
        );
    }

    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    private void _hookPostTiramisu(final XC_LoadPackage.LoadPackageParam lpparam) {
        final String PM_CLASS = "com.android.server.pm.ComputerEngine";
        XposedBridge.log(LOG_TAG + ": Hook Computer Engine!");

        findAndHookMethod(
                PM_CLASS,
                lpparam.classLoader,
                "getApplicationInfo",
                String.class, long.class, int.class, /* packageName, flags, userId */
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            ApplicationInfo appInfo = (ApplicationInfo) param.getResult();
                            if (appInfo != null)
                                hookPrivateFlags(appInfo, appInfo.packageName);
                        } catch (Exception e) {
                            XposedBridge.log(LOG_TAG + ": " + getStackTraceString(e));
                        }
                    }
                }
        );

        try {
            hookAllMethods(
                    lpparam.classLoader.loadClass(PM_CLASS),
                    "getInstalledApplications",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                List<ApplicationInfo> infos = (List<ApplicationInfo>) param.getResult();
                                if (infos != null) {
                                    for (ApplicationInfo info : infos) {
                                        hookPrivateFlags(info, info.packageName);
                                    }
                                }
                            } catch (Exception e) {
                                XposedBridge.log(LOG_TAG + ": " + getStackTraceString(e));
                            }
                        }
                    }
            );
        } catch (Exception e) {
            XposedBridge.log(LOG_TAG + ": " + getStackTraceString(e));
        }
    }

    @SuppressWarnings("unchecked")
    private void _hookPreTiramisu(final XC_LoadPackage.LoadPackageParam lpparam) {
        final String PM_CLASS = "com.android.server.pm.PackageManagerService";
        XposedBridge.log(LOG_TAG + ": Hook Package Manager Service!");

        findAndHookMethod(
                PM_CLASS,
                lpparam.classLoader,
                "getApplicationInfo",
                String.class, int.class, int.class, /* packageName, flags, userId */
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            ApplicationInfo appInfo = (ApplicationInfo) param.getResult();
                            if (appInfo != null)
                                hookPrivateFlags(appInfo, appInfo.packageName);
                        } catch (Exception e) {
                            XposedBridge.log(LOG_TAG + ": " + getStackTraceString(e));
                        }
                    }
                }
        );

        findAndHookMethod(
                PM_CLASS,
                lpparam.classLoader,
                "getInstalledApplicationsListInternal",
                int.class, int.class, int.class, /* flags, userId, callingUid */
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            List<ApplicationInfo> infos = (List<ApplicationInfo>) param.getResult();
                            if (infos != null) {
                                for (ApplicationInfo info : infos) {
                                    hookPrivateFlags(info, info.packageName);
                                }
                            }
                        } catch (Exception e) {
                            XposedBridge.log(LOG_TAG + ": " + getStackTraceString(e));
                        }
                    }
                }
        );

    }

    private void hookPrivateFlags(ApplicationInfo appInfo, String packageName) {
        if (Configuration.isEnabled(packageName)) {
            int privateFlags = XposedHelpers.getIntField(
                    appInfo,
                    "privateFlags"
            );
            privateFlags |= PRIVATE_FLAG_ALLOW_AUDIO_PLAYBACK_CAPTURE;
            XposedHelpers.setIntField(
                    appInfo,
                    "privateFlags",
                    privateFlags
            );
            XposedBridge.log(LOG_TAG + ": new Flags " + packageName);
        }
    }
}
