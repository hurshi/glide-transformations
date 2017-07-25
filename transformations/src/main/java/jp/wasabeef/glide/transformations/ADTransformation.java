package jp.wasabeef.glide.transformations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

/**
 * Created by Gavin on 17/1/19.
 */

public class ADTransformation implements Transformation<Bitmap> {
    private BitmapPool bitmapPool;
    private int textColorRes;
    private int textBgColorRes;
    private String text;
    private int textSize;

    public ADTransformation(Context context, String text, int textColorRes, int textBgColorRes, int textSize) {
        bitmapPool = Glide.get(context).getBitmapPool();
        this.text = text;
        this.textColorRes = textColorRes;
        this.textBgColorRes = textBgColorRes;
        this.textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics());
    }

    @Override
    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);

        Rect rectangle = new Rect();
        paint.getTextBounds(text, 0, text.length(), rectangle);
        paint.setColor(textBgColorRes);
        Rect rect2 = new Rect(0, height - rectangle.height(), rectangle.width(), height);
        canvas.drawRect(rect2, paint);

        paint.setColor(textColorRes);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        canvas.drawText(text, rect2.left, (rect2.bottom + rect2.top - fontMetrics.bottom - fontMetrics.top) / 2, paint);
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update("ADTransformation".getBytes(CHARSET));
    }
}
