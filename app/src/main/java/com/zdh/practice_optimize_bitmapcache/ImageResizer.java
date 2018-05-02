package com.zdh.practice_optimize_bitmapcache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by zdh on 2018/5/1.
 * 压缩图片
 */

public class ImageResizer {

    public ImageResizer() {

    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只会去加载图片的原始宽高 loading raw width and height of bitmap
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //计算采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        int width = options.outWidth;
        int height = options.outHeight;
        int inSampledSize = 1;

        // 超出显示范围则缩放
        if (width > reqWidth || height > reqHeight){
            /*提前除以2，避免一些不必要的尺寸被缩放:
              例如：200*300 在 100*100 显示，采样率为2则为100*150 在 100*100的iv
              还能够接受，但是如果采样率为4，则图片过度缩小，会导致显示时拉升而模糊
              所以提前使用了2的采样率后，在比较时提前缩小的尺寸如果还大于显示范围则必须改变采样
             */
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            // 除2后，如120*150 显示在100*100 中，如果使用了采样率为2，则就缩小了，导致小于显示范围而拉伸模糊
            // 所以在除2后，比显示范围小的是能够接受直接显示的，这样做避免的采样率的过度变化

            if (halfWidth / inSampledSize >= reqWidth
                    && halfHeight / inSampledSize >= reqHeight) {
                // 值必须是2的指数
                inSampledSize *= 2;
            }

        }

        return inSampledSize;
    }
}
