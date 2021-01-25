package com.hooni.quotesaver.util

import android.graphics.*
import com.squareup.picasso.Transformation


class PicassoTransformationDarken : Transformation {
    override fun transform(bitmap: Bitmap): Bitmap? {
        val source = bitmap.copy(Bitmap.Config.ARGB_8888, true) ?: return null
        val paint = Paint()
        val filter: ColorFilter = LightingColorFilter(-0x00aaaaaa, 0x00222222)
        paint.colorFilter = filter
        val canvas = Canvas(source)
        canvas.drawBitmap(source, 0f, 0f, paint)
        bitmap.recycle()
        return source
    }

    override fun key(): String {
        return "darken"
    }

}