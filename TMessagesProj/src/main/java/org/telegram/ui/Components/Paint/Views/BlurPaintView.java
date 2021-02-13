package org.telegram.ui.Components.Paint.Views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.BlurCreator;
import org.telegram.ui.Components.Point;

public class BlurPaintView extends EntityView {

    private Bitmap resultBitmap;
    private Paint paint = new Paint();
    private Bitmap originalBitmap;
    private Bitmap bitmapToEdit;
    private ImageView imageView2;

    public BlurPaintView(
            Context context,
            Point pos,
            Bitmap originalBitmap,
            Bitmap bitmapToEdit
    ) {
        super(context, pos);
        setWillNotDraw(false);
        if (context == null) return;
        if (pos == null) return;
        if (originalBitmap == null) return;
        if (bitmapToEdit == null) return;
        this.originalBitmap = originalBitmap;
        this.bitmapToEdit = bitmapToEdit;
        Bitmap combinedBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas combineCanvas = new Canvas(combinedBitmap);
        Paint combinePaint = new Paint();
        combinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        combineCanvas.drawBitmap(originalBitmap, 0f, 0f, combinePaint);
        combineCanvas.drawBitmap(bitmapToEdit, 0f, 0f, combinePaint);
        resultBitmap = combinedBitmap;
        imageView2 = new ImageView(context);
        imageView2.setImageBitmap(resultBitmap);
        addView(imageView2, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP));
        setOnTouchListener(new OnTouchListener() {
                               @Override
                               public boolean onTouch(View v, MotionEvent event) {
                                   int action = event.getAction();
                                   switch (action) {
                                       case MotionEvent.ACTION_DOWN:
                                       case MotionEvent.ACTION_MOVE:
                                           int x = (int) event.getX();
                                           int y = (int) event.getY();
                                           resultBitmap = blurRegion(resultBitmap, x - 100, y - 100, x + 100, y + 100, 15f);
                                           imageView2.setImageBitmap(resultBitmap);
                                           invalidate();
                                           for (int i = 0; i < getChildCount(); i++) {
                                               View child = getChildAt(i);
                                               child.invalidate();
                                           }
                                           updateSelectionView();
                                           break;
                                       default:
                                           performClick();
                                   }
                                   return true;
                               }
                           }
        );
    }

    public void setMaxWidth(int maxWidth) {
     //   imageView2.setMaxWidth(maxWidth);
    }

    public BlurPaintView(Context context, BlurPaintView blurPaintView, Point pos) {
       this(context, pos, blurPaintView.originalBitmap, blurPaintView.bitmapToEdit);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Blur", "Draw");
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
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

    public Bitmap getResultBitmap() {
        return resultBitmap;
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
        }
    }

}
