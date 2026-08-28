package com.xos.personalsystem.core.focus

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import com.xos.personalsystem.data.local.dao.FocusAppDao
import com.xos.personalsystem.domain.entities.FocusApp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusModeManager @Inject constructor(
    private val context: Context,
    private val focusAppDao: FocusAppDao
) {
    
    fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    fun requestUsageStatsPermission() {
        val intent = android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
        context.startActivity(intent)
    }
    
    suspend fun getInstalledApps(): List<FocusApp> {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        return apps.mapNotNull { appInfo ->
            if (appInfo.packageName == context.packageName) return@mapNotNull null
            if (appInfo.packageName.startsWith("android.")) return@mapNotNull null
            
            FocusApp(
                id = java.util.UUID.randomUUID().toString(),
                packageName = appInfo.packageName,
                appName = packageManager.getApplicationLabel(appInfo).toString(),
                isBlocked = false,
                icon = "" // Will load icon later
            )
        }.sortedBy { it.appName }
    }
    
    suspend fun addBlockedApp(packageName: String) {
        val existing = focusAppDao.getByPackage(packageName)
        if (existing == null) {
            val app = FocusApp(
                id = java.util.UUID.randomUUID().toString(),
                packageName = packageName,
                appName = getAppName(packageName),
                isBlocked = true
            )
            focusAppDao.insert(app.toEntity())
        } else {
            existing.isBlocked = true
            focusAppDao.update(existing)
        }
    }
    
    suspend fun removeBlockedApp(packageName: String) {
        val existing = focusAppDao.getByPackage(packageName)
        if (existing != null) {
            existing.isBlocked = false
            focusAppDao.update(existing)
        }
    }
    
    suspend fun getBlockedApps(): List<FocusApp> {
        return focusAppDao.getBlocked().map { it.toDomain() }
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
    
    private fun FocusApp.toEntity(): com.xos.personalsystem.data.local.entities.FocusAppEntity {
        return com.xos.personalsystem.data.local.entities.FocusAppEntity(
            id = id,
            packageName = packageName,
            appName = appName,
            isBlocked = isBlocked,
            icon = icon,
            createdAt = createdAt
        )
    }
    
    private fun com.xos.personalsystem.data.local.entities.FocusAppEntity.toDomain(): FocusApp {
        return FocusApp(
            id = id,
            packageName = packageName,
            appName = appName,
            isBlocked = isBlocked,
            icon = icon,
            createdAt = createdAt
        )
    }
}
