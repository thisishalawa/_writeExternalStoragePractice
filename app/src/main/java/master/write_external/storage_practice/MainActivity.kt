package master.write_external.storage_practice

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import master.write_external.storage_practice.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

private const val GALLERY = 1
private const val CAMERA: Int = 2
private const val IMAGE_DIRECTORY: String = "/abc_test"
const val TAG = "_TAG"


class MainActivity : AppCompatActivity(), View.OnClickListener {

    // binding
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestMultiplePermissions()
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        Toast.makeText(
                            applicationContext,
                            "All permissions are granted by user!",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.iv.setOnClickListener(this@MainActivity)

                    } else if (report.isAnyPermissionPermanentlyDenied) {
                        openSettingsDialog()
                        Toast.makeText(
                            applicationContext,
                            " permissions denied !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(
                    applicationContext, "Some Error!" +
                            "_", Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun openSettingsDialog() {


        /*
        * WIP . .
        *
        * */
    }

    private fun saveImage(myBitmap: Bitmap): String {
        Log.d(TAG, "savingImage...")

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val wallpaperDirectory = File(
            Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
        )

        val fileName =
            Calendar.getInstance().timeInMillis.toString().replace(":", ".") +
                    ".jpg"

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }


        try {
            val fileDestination1 = File(
                wallpaperDirectory,
                Calendar.getInstance().timeInMillis.toString().replace(":", ".") +
                        ".jpg"
            )


            val fileDestination2: File =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)


            Log.d(TAG, "saveImage: file1 : $fileDestination1")
            Log.d(TAG, "saveImage: file2 : $fileDestination2")

            /*
            *
            *  use fileDestination1 ->
            *  /storage/emulated/0/abc_test/1629810839754.jpg
            *  failed Operation not permitted
            *  show in gallery
            *
            *  use fileDestination2 ->
            *  /storage/emulated/0/Android/data/master.write_external.storage_practice/files/storage/emulated/0/abc_test/1629810839734.jpg
            *  works fine
            *
            * */
            fileDestination2.createNewFile()
            val fo = FileOutputStream(fileDestination2)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(fileDestination2.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d(
                TAG, "File Saved --->"
                        + fileDestination2.absolutePath
            )
            return fileDestination2.absolutePath

        } catch (e1: IOException) {
            e1.printStackTrace()
            Log.d(
                TAG, "failed ${
                    e1.message
                }"
            )
            Toast.makeText(
                this, e1.message, Toast.LENGTH_SHORT
            ).show()
        }



        return ""
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")

        val pictureDialogItems = arrayOf(
            "Select photo from gallery",
            "Capture photo from camera"
        )

        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    /*
    *
    *
    *
    *
    *
    *
    * */
    override fun onClick(p0: View?) {
        if (p0?.id == R.id.iv) {
            showPictureDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path: String = saveImage(bitmap)
                    binding.iv.setImageBitmap(bitmap)


                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA) {
            val thumbnail = data!!.extras!!["data"] as Bitmap?
            binding.iv.setImageBitmap(thumbnail)
            saveImage(thumbnail!!)
        }
    }


}