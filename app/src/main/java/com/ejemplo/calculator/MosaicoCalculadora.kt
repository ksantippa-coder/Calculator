package com.ejemplo.calculator

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService

class MosaicoCalculadora : TileService() {
    override fun onClick() {
        super.onClick()
        val intento = Intent(this, ActividadPrincipal::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent(intento))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intento)
        }
    }

    private fun pendingIntent(intento: Intent): android.app.PendingIntent {
        return android.app.PendingIntent.getActivity(
            this,
            0,
            intento,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
    }
}
