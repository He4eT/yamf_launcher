package eu.ottop.yamlauncher

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.UserHandle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppUtils(private val context: Context) {

    private val sharedPreferenceManager = SharedPreferenceManager(context)

    suspend fun getInstalledApps(launcherApps: LauncherApps): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        var sortedApps = listOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        withContext(Dispatchers.Default) {
            for (i in launcherApps.profiles.indices) {
                launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                    if (!sharedPreferenceManager.isAppHidden(
                            app.applicationInfo.packageName,
                            i
                        ) && app.applicationInfo.packageName != context.applicationInfo.packageName
                    ) {
                        allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                    }
                }
            }

            sortedApps = allApps.sortedBy {
                sharedPreferenceManager.getAppName(
                    it.first.applicationInfo.packageName,
                    it.second.second,
                    it.first.applicationInfo.loadLabel(context.packageManager)
                ).toString().lowercase()
            }
        }
        return sortedApps

    }

    fun getHiddenApps(): List<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>> {
        val allApps = mutableListOf<Pair<LauncherActivityInfo, Pair<UserHandle, Int>>>()
        val launcherApps = context.getSystemService(AppCompatActivity.LAUNCHER_APPS_SERVICE) as LauncherApps
        for (i in launcherApps.profiles.indices) {
            launcherApps.getActivityList(null, launcherApps.profiles[i]).forEach { app ->
                if (sharedPreferenceManager.isAppHidden(app.applicationInfo.packageName, i)) {
                    allApps.add(Pair(app, Pair(launcherApps.profiles[i], i)))
                }
            }
        }
        return allApps.sortedBy {
            sharedPreferenceManager.getAppName(
                it.first.applicationInfo.packageName,
                it.second.second,
                context.packageManager.getApplicationLabel(it.first.applicationInfo)
            ).toString().lowercase()
        }
    }

    fun getAppInfo(
        launcherApps: LauncherApps,
        packageName: String,
        profile: Int
    ): ApplicationInfo? {
        return try {
            launcherApps.getApplicationInfo(packageName, 0, launcherApps.profiles[profile])
        } catch (_: Exception) {
            null
        }
    }

    fun launchApp(launcherApps: LauncherApps, appInfo: LauncherActivityInfo, userHandle: UserHandle) {
        val mainActivity = launcherApps.getActivityList(appInfo.applicationInfo.packageName, userHandle).firstOrNull()
        if (mainActivity != null) {
            launcherApps.startMainActivity(mainActivity.componentName,  userHandle, null, null)
        } else {
            Toast.makeText(context, "Cannot launch app", Toast.LENGTH_SHORT).show()
        }
    }
}