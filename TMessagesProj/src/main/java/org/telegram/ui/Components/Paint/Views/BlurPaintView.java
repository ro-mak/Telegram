package org.telegram.ui.Components.Paint.Views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Gravity;
import android.widget.ImageView;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.BlurCreator;
import org.telegram.ui.Components.Point;

public class BlurPaintView extends EntityView  {

    private Bitmap blurredBitmap;
    private Paint paint = new Paint();

    public BlurPaintView(Context context, Point pos, Bitmap originalBitmap) {
        super(context, pos);
        int widthHalf = originalBitmap.getWidth()/2;
        int heightHalf = originalBitmap.getHeight()/2;
        blurredBitmap = blurRegion(originalBitmap, widthHalf - 100, heightHalf - 100, widthHalf + 100,heightHalf + 100, 15f);
        ImageView imageView = new ImageView(context);
        ImageView imageView2 = new ImageView(context);
        imageView.setImageBitmap(blurredBitmap);
        imageView2.setImageBitmap(originalBitmap);
        addView(imageView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
        addView(imageView2,LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.CENTER));
    }
    private Bitmap blurRegion(Bitmap originalBitmap, int left, int top, int right, int bottom, float blurRadius) {
        Bitmap blurred = BlurCreator.blur(ApplicationLoader.applicationContext, originalBitmap, blurRadius);
        Bitmap originalBackground = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap blurredBackground = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Paint cropPaint = new Paint();
        cropPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas blurredCanvas = new Canvas(blurredBackground);
        Canvas originalBitmapCanvas = new Canvas(originalBackground);

        originalBitmapCanvas.drawBitmap(originalBitmap, 0, 0, null);

        originalBitmapCanvas.drawRect(left, top, right, bottom, cropPaint);
        blurredCanvas.drawBitmap(blurred, 0, 0, paint);
        blurredCanvas.drawBitmap(originalBackground, 0, 0, paint);

        return blurredBackground;
    }

    public Bitmap getResultBitmap(){
        return blurredBitmap;
    }

    @Override
    protected SelectionView createSelectionView() {
        return new BlurSelectionView(getContext());
    }

    public class BlurSelectionView extends SelectionView {
        public BlurSelectionView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(blurredBitmap,0,0,paint);
        }
    }

}
