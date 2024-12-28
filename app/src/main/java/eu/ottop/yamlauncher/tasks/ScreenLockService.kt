package eu.ottop.yamlauncher.tasks

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class ScreenLockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action == "LOCK_SCREEN") {
            performLockScreen()
        }
        if (intent != null && intent.action == "RECENTS") {
            performShowRecents()
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun performLockScreen() {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }

    private fun performShowRecents() {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
    }
}