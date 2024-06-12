package org.cisnux.mydietary.presentation.ui.components

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.MainActivity
import org.cisnux.mydietary.commons.utils.AppDestination
import org.cisnux.mydietary.commons.utils.isDevModeActive
import kotlinx.coroutines.launch

private const val NOTIFICATION_ID = 1
private const val CHANNEL_ID = "dietary dev mode"
private const val CHANNEL_NAME = "dietary"

@Composable
fun NotificationDialog(
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var openDialog by remember { mutableStateOf(true) }
    val sendNotification: () -> Unit = {
        val intent = Intent(
            Intent.ACTION_VIEW,
            AppDestination.DevModeRoute.deepLinkPattern.toUri(),
            context,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_foreground
                )
            )
            setContentTitle(context.getString(R.string.dev_mode_notif_title))
            setContentText(context.getString(R.string.open_dev_mode_menu))
            setAutoCancel(true)
            setOngoing(true)

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = CHANNEL_NAME

            setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGiven ->
            if (isGiven and context.isDevModeActive) {
                sendNotification()
            }
        }
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                LaunchedEffect(Unit) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                if (openDialog)
                    AlertDialog(
                        onDismissRequest = {
                            openDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.dev_mode_dismiss_message))
                            }
                        },
                        title = {
                            Text(text = stringResource(R.string.dev_mode_notification))
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.dev_mode_body)
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDialog = false
                                }
                            ) {
                                Text(stringResource(R.string.allow))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    openDialog = false
                                }
                            ) {
                                Text(stringResource(id = R.string.cancel))
                            }
                        }
                    )
            }

            else -> {
                if (context.isDevModeActive)
                    sendNotification()
            }
        }
    } else {
        if (context.isDevModeActive)
            sendNotification()
    }
}