package org.telegram.ui.Components.Paint.Views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.Paint.BlurCreator;
import org.telegram.ui.Components.Point;

public class BlurPaintView extends EntityView  {

    private Bitmap blurredBitmap;
    private Paint paint = new Paint();

    public BlurPaintView(Context context, Point pos, Bitmap originalBitmap) {
        super(context, pos);
        blurredBitmap = cropOriginalBitmap(originalBitmap);
    }
    private Bitmap cropOriginalBitmap(Bitmap originalBitmap) {
        Bitmap copyOfOriginal = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blurred = BlurCreator.blur(ApplicationLoader.applicationContext, copyOfOriginal);
        Bitmap bmOverlay = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(blurred, 0, 0, null);
        canvas.drawRect(0, 0, 300, 300, paint);

        return bmOverlay;
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
