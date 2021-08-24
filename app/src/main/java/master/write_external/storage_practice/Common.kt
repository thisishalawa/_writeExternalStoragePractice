package master.write_external.storage_practice

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun hasWriteFilePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestWriteFilesPermission(
    context: Context,
    _imageRequestPermission: Int
) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    ) {
        AlertDialog.Builder(context)
            .setTitle("permission_needed")
            .setMessage("file_permission_des")
            .setPositiveButton(
                "ok"
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), _imageRequestPermission
                )
            }.setNegativeButton(
                "cancel"
            ) { dialog, _ -> dialog.dismiss() }.create()
            .show()
    } else {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            _imageRequestPermission
        )
    }
}


