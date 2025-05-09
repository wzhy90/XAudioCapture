package io.github.libxposed.xaudiocapture.xposed;

import static de.robv.android.xposed.XC_MethodReplacement.returnConstant;
import static de.robv.android.xposed.XposedBridge.getXposedVersion;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static io.github.libxposed.xaudiocapture.BuildConfig.APPLICATION_ID;

import androidx.annotation.Keep;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.github.libxposed.xaudiocapture.MainApplication;

@Keep
public class HookSelf implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!APPLICATION_ID.equals(lpparam.packageName))
            return;

        findAndHookMethod(
                MainApplication.class.getName(),
                lpparam.classLoader,
                "getActiveXposedVersion",
                returnConstant(getXposedVersion())
        );
    }
}
