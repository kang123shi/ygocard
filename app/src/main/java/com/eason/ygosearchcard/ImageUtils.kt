package com.eason.ygosearchcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class ImageUtils {

    @Throws( IOException::class)
    fun getBitmapFormUri(context: Context, uri: Uri?): Bitmap? {
        var input: InputStream? = context.getContentResolver().openInputStream(uri!!)

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        val onlyBoundsOptions = BitmapFactory.Options()
        onlyBoundsOptions.inJustDecodeBounds = true //不加载到内存
        onlyBoundsOptions.inDither = true //optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565 //optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
        input?.close()
        val originalWidth = onlyBoundsOptions.outWidth
        val originalHeight = onlyBoundsOptions.outHeight
        if (originalWidth == -1 || originalHeight == -1) return null

        //图片分辨率以480x800为标准
        val hh = 800f //这里设置高度为800f
        val ww = 480f //这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var be = 1 //be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) { //如果宽度大的话根据宽度固定大小缩放
            be = (originalWidth / ww).toInt()
        } else if (originalWidth < originalHeight && originalHeight > hh) { //如果高度高的话根据宽度固定大小缩放
            be = (originalHeight / hh).toInt()
        }
        if (be <= 0) be = 1
        //比例压缩
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inSampleSize = be //设置缩放比例
        bitmapOptions.inDither = true
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565
        input = context.getContentResolver().openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
        input?.close()
        return bitmap //再进行质量压缩
    }
}