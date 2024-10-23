package com.example.green_hell.itemCustom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.green_hell.DetectionResult;

import java.util.ArrayList;
import java.util.List;

public class ItemForEars extends AppCompatImageView {
    private boolean isOverlayVisible = false;
    private Bitmap overlayBitmap;
    private Bitmap currentBitmap;
    private List<DetectionResult> detectionResults;
    private List<List<Integer>> boundingBoxes;  // Stores multiple bounding boxes
    private List<String> classNames; // List to store class names
    private ImageView originalImageView; // Stores the original ImageView
    private Bitmap originalBitmap; // Store original bitmap if needed

    public ItemForEars(Context context) {
        super(context);
        init();
    }

    public ItemForEars(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemForEars(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        boundingBoxes = new ArrayList<>();
        classNames = new ArrayList<>();
        detectionResults = new ArrayList<>(); // Initialize this list
    }

    public void setOriginalImageView(ImageView originalImageView) {
        this.originalImageView = originalImageView;
        Log.d("ItemForEars", "Original ImageView set: " + originalImageView);
    }

    public void setOverlayBitmap(int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId, null);
        Bitmap newBitmap;

        if (drawable instanceof BitmapDrawable) {
            newBitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            newBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        if (isOverlayVisible && newBitmap.equals(currentBitmap)) {
            // Nếu overlay hiện tại đang hiển thị và bitmap mới giống bitmap hiện tại, ẩn overlay
            overlayBitmap = null;
            isOverlayVisible = false;
        }
        invalidate(); // Yêu cầu vẽ lại
    }


    public void addDetectionResult(DetectionResult result) {
        this.detectionResults.add(result);
        Log.d("ItemForEars", "Added DetectionResult: " + result);
        invalidate(); // Request a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (detectionResults != null && originalImageView != null) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            float scaleX = (float) originalImageView.getWidth() / 640f;
            float scaleY = (float) originalImageView.getHeight() / 640f;

            float overlayScaleFactor = 1.2f; // Scale factor for the overlay

            // Draw overlay on each bounding box for 'ears'
            for (DetectionResult result : detectionResults) {
                if ("ears".equals(result.getClassName())) {
                    List<Integer> bbox = result.getBbox();
                    float left = bbox.get(0) * scaleX;
                    float top = bbox.get(1) * scaleY;
                    float right = bbox.get(2) * scaleX;
                    float bottom = bbox.get(3) * scaleY;

                    // Calculate size and position for the overlay
                    float overlayWidth = (right - left) * overlayScaleFactor;
                    float overlayHeight = overlayBitmap != null ? (overlayBitmap.getHeight() * overlayWidth / overlayBitmap.getWidth()) : 0;

                    float overlayLeft = left + (right - left - overlayWidth) / 2;
                    float overlayTop = top + (bottom - top - overlayHeight) / 2;

                    // Draw the overlay on the canvas
                    if (overlayBitmap != null) {
                        canvas.drawBitmap(overlayBitmap, null, new RectF(overlayLeft, overlayTop, overlayLeft + overlayWidth, overlayTop + overlayHeight), null);
                    }
                }
            }
        }
    }

}
