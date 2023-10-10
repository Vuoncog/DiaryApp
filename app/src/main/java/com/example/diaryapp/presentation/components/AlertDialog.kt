package com.example.diaryapp.presentation.components

import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    isDialogOpened: Boolean,
    onCloseDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    if (isDialogOpened) {
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            confirmButton = {
                Button(onClick = onConfirmDialog) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onCloseDialog) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = onCloseDialog,
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            )
        )
    }
}