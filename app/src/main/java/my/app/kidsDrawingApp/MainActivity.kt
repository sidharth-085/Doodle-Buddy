package my.app.kidsDrawingApp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var galleryBtn: ImageButton? = null
    private var saveToGalleryBtn: ImageButton? = null
    private var undoButton: ImageButton? = null
    private var redoButton: ImageButton? = null
    private var eraser: ImageButton? = null
    private var brushSizeSelectorBtn: ImageButton? = null
    private var requestPermission: ActivityResultLauncher<Array<String>>? = null
    private var progressBarDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setNewBrushSize(10.toFloat())
        eraser = findViewById(R.id.btnEraser)
        brushSizeSelectorBtn = findViewById(R.id.btnBrushSizeSelector)
        galleryBtn = findViewById(R.id.galleryBtn)
        saveToGalleryBtn = findViewById(R.id.saveToGalleryBtn)
        undoButton = findViewById(R.id.undoBtn)
        redoButton = findViewById(R.id.redoBtn)

        val redColorPaint = findViewById<ImageButton>(R.id.redColor)
        val blueColorPaint = findViewById<ImageButton>(R.id.blueColor)
        val greenColorPaint = findViewById<ImageButton>(R.id.greenColor)
        val yellowColorPaint = findViewById<ImageButton>(R.id.yellowColor)
        val orangeColorPaint = findViewById<ImageButton>(R.id.orangeColor)
        val violetColorPaint = findViewById<ImageButton>(R.id.violetColor)
        val navyColorPaint = findViewById<ImageButton>(R.id.navyColor)
        val blackColorPaint = findViewById<ImageButton>(R.id.blackColor)
        blackColorPaint.background = ContextCompat.getDrawable(
            this, R.drawable.selected_color_outline
        )

        fun defaultBordersForButtons() {
            redColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            blueColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            greenColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            yellowColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            orangeColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            blackColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            navyColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            violetColorPaint?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
            eraser?.background = ContextCompat.getDrawable(
                this, R.drawable.normal_color_outline
            )
        }

        redColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(redColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        blueColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(blueColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        greenColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(greenColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        yellowColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(yellowColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        blackColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(blackColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        violetColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(violetColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        orangeColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(orangeColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        navyColorPaint!!.setOnClickListener {
            defaultBordersForButtons()
            drawingView!!.setNewBrushColor(navyColorPaint.tag.toString())
            it.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
        }

        eraser?.setOnClickListener {
            defaultBordersForButtons()
            eraser!!.background = ContextCompat.getDrawable(
                this, R.drawable.selected_color_outline
            )
            drawingView!!.setNewBrushColor("#FFFFFF")
        }

        brushSizeSelectorBtn!!.setOnClickListener {
            showBrushSizeDialogOnScreen()
        }

        undoButton?.setOnClickListener {
            drawingView?.onClickUndo()
        }

        redoButton?.setOnClickListener {
            drawingView?.onClickRedo()
        }

        val galleryActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->

                if(result.resultCode == RESULT_OK && result?.data != null) {
                    val imageView: ImageView = findViewById(R.id.importedImage)
                    imageView.setImageURI(result.data?.data)
                }
            }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted) {
                    Toast.makeText(this,
                        "Permission granted to access system files",
                        Toast.LENGTH_LONG).show()

                    val intentPicker = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    galleryActivityLauncher.launch(intentPicker)

                }
                else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this,
                            "Permission denied to access system files",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        galleryBtn!!.setOnClickListener {
            externalStoragePermission()
        }

        saveToGalleryBtn!!.setOnClickListener {
            if (isReadStoragePermissionAllowed()) {
                showProgressBarDialog()
                lifecycleScope.launch {
                    val frameLayout: FrameLayout = findViewById(R.id.frameLayoutDrawingView)
                    val finalBitmap: Bitmap = getBitmapFromView(frameLayout)
                    saveBitmapFile(finalBitmap)
                }
            }
        }
    }

    private fun shareFile(result: String) {
        MediaScannerConnection.scanFile(this, arrayOf(result), null) {
            _ , uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"

            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }

    private fun showProgressBarDialog() {
        progressBarDialog = Dialog(this@MainActivity)
        progressBarDialog?.setContentView(R.layout.progress_bar_animation)
        progressBarDialog?.setCancelable(false)
        progressBarDialog?.show()
    }

    private fun cancelProgressBarDialog() {
        if (progressBarDialog != null) {
            progressBarDialog?.dismiss()
            progressBarDialog = null
        }
    }

    private fun showBrushSizeDialogOnScreen() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.brush_size_dialog)
        brushDialog.setTitle("Brush Size: ")

        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.smallBrushSize)
        smallBtn.setOnClickListener {
            drawingView!!.setNewBrushSize(10.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.mediumBrushSize)
        mediumBtn.setOnClickListener {
            drawingView!!.setNewBrushSize(20.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.largeBrushSize)
        largeBtn.setOnClickListener {
            drawingView!!.setNewBrushSize(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap= Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val bitmapBackground = view.background
        val canvas = Canvas(bitmap)
        if (bitmapBackground != null) {
            bitmapBackground.draw(canvas)
        }
        else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)

                    val file = File(externalCacheDir?.absoluteFile.toString() +
                            File.separator + "KidsDrawingApp_" +
                            System.currentTimeMillis() / 1000  + ".png"
                    )

                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(byteArrayOutputStream.toByteArray())
                    fileOutputStream.close()

                    result = file.absolutePath

                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            cancelProgressBarDialog()
                            Toast.makeText(this@MainActivity,
                                "File saved successfully: $result",
                                Toast.LENGTH_SHORT
                            ).show()

                            shareFile(result)

                        }
                        else {
                            cancelProgressBarDialog()
                            Toast.makeText(this@MainActivity,
                                "Something went wrong saving the file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                catch(e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun isReadStoragePermissionAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun externalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
           showRationalDialog("Kids Drawing App",
               "Kids Drawing App needs to access your external storage")
        }
        else {
            requestPermission!!.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun showRationalDialog(title: String, Message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Cancel") {
                dialog, _ -> dialog.dismiss()
        }
        builder.setCancelable(true)
        builder.create().show()
    }

}