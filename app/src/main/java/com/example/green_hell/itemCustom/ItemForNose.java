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

public class ItemForNose extends AppCompatImageView {
    private boolean isOverlayVisible = false;
    private Bitmap overlayBitmap;
    private Bitmap currentBitmap;
    private List<DetectionResult> detectionResults;
    private List<List<Integer>> boundingBoxes;  // Stores multiple bounding boxes
    private List<String> classNames; // List to store class names
    private ImageView originalImageView; // Stores the original ImageView
    private Bitmap originalBitmap; // Store original bitmap if needed

    public ItemForNose(Context context) {
        super(context);
        init();
    }

    public ItemForNose(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemForNose(Context context, AttributeSet attrs, int defStyleAttr) {
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
        Log.d("ItemForNose", "Original ImageView set: " + originalImageView);
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
        } else {
            // Nếu bitmap mới khác bitmap hiện tại hoặc overlay không hiển thị
            overlayBitmap = makeBackgroundTransparent(newBitmap);
            currentBitmap = newBitmap; // Cập nhật bitmap hiện tại
            isOverlayVisible = true; // Đánh dấu overlay đang hiển thị
        }
        invalidate(); // Yêu cầu vẽ lại
    }

    public void addDetectionResult(DetectionResult result) {
        this.detectionResults.add(result);
        Log.d("ItemForNose", "Đã thêm DetectionResult: " + result);
        invalidate(); // Yêu cầu vẽ lại
    }

    private Bitmap makeBackgroundTransparent(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Create new bitmap with the same size and transparency
        Bitmap transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transparentBitmap);
        Paint paint = new Paint();
        paint.setAlpha(0); // Set transparency

        // Draw transparent background
        canvas.drawRect(0, 0, width, height, paint);

        // Draw original bitmap onto new bitmap
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelColor = bitmap.getPixel(x, y);
                // If pixel is not background color (assuming white as background), keep the color
                if (pixelColor != Color.WHITE) {
                    transparentBitmap.setPixel(x, y, pixelColor);
                }
            }
        }

        return transparentBitmap;
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

            float overlayScaleFactor = 2.0f;

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (DetectionResult result : detectionResults) {
                if ("nose".equals(result.getClassName())) {
                    List<Integer> bbox = result.getBbox();
                    float left = bbox.get(0) * scaleX;
                    float top = bbox.get(1) * scaleY;
                    float right = bbox.get(2) * scaleX;
                    float bottom = bbox.get(3) * scaleY;


                    if (left < minX) minX = left;
                    if (top < minY) minY = top;
                    if (right > maxX) maxX = right;
                    if (bottom > maxY) maxY = bottom;
                }
            }

            // Calculate size and position for the overlay
            float overlayWidth = (maxX - minX) * overlayScaleFactor;
            float overlayHeight = overlayBitmap != null ? (overlayBitmap.getHeight() * overlayWidth / overlayBitmap.getWidth()) : 0;

            float overlayLeft = minX + (maxX - minX - overlayWidth) / 2;
            float overlayTop = minY + (maxY - minY - overlayHeight) / 2;

            // Draw the overlay on the canvas
            if (overlayBitmap != null) {
                canvas.drawBitmap(overlayBitmap, null, new RectF(overlayLeft, overlayTop, overlayLeft + overlayWidth, overlayTop + overlayHeight), null);
            }
        }
    }
}
