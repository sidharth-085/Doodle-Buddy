package my.app.kidsDrawingApp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attribute: AttributeSet): View(context, attribute) {

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()

    private var drawPath: CustomPath? = null
    private var canvasBitmap: Bitmap? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var canvas: Canvas? = null
    private var color = Color.BLACK
    private var brushSize: Float = 0.toFloat()
    private var storePaths = ArrayList<CustomPath>()
    private var storeUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawingView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, drawPaint)

        for (path in storePaths) {
            drawPaint!!.strokeWidth = path.brushThickness
            drawPaint!!.color = path.color
            canvas.drawPath(path, drawPaint!!)
        }
        if (!drawPath!!.isEmpty) {
            drawPaint!!.strokeWidth = drawPath!!.brushThickness
            drawPaint!!.color = drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath!!.color = color
                drawPath!!.brushThickness = brushSize
                drawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath!!.moveTo(touchX, touchY)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath!!.lineTo(touchX, touchY)
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                storePaths.add(drawPath!!)
                drawPath = CustomPath(color, brushSize)
            }

            else -> {
                return false
            }
        }
        invalidate()
        return true
    }

    private fun setUpDrawingView() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushSize)
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.color = Color.BLACK
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    fun setNewBrushSize(newSize: Float) {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics
        )
        drawPaint!!.strokeWidth = brushSize
    }

    fun setNewBrushColor(newColor: String) {
        color = Color.parseColor(newColor)
        drawPaint!!.color = color
    }

    fun onClickUndo() {
        if (storePaths.size > 0) {
            val temp: CustomPath = storePaths.removeAt(storePaths.size - 1)
            storeUndoPaths.add(temp)
            invalidate()
        }
    }

    fun onClickRedo() {
        if (storeUndoPaths.size > 0) {
            val temp: CustomPath = storeUndoPaths[storeUndoPaths.size - 1]
            storePaths.add(temp)
            storeUndoPaths.removeAt(storeUndoPaths.size - 1)
            invalidate()
        }
    }
}