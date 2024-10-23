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

public class ItemForEyes extends AppCompatImageView {
    private boolean isOverlayVisible = false;
    private Bitmap overlayBitmap;
    private Bitmap currentBitmap;
    private List<DetectionResult> detectionResults;
    private List<List<Integer>> boundingBoxes;  // Stores multiple bounding boxes
    private List<String> classNames; // Danh sách để lưu trữ tên lớp
    private ImageView originalImageView; // Stores the original ImageView
    private Bitmap originalBitmap; // Lưu bitmap gốc nếu cần

    public ItemForEyes(Context context) {
        super(context);
        init();
    }

    public ItemForEyes(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemForEyes(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        boundingBoxes = new ArrayList<>();
        classNames = new ArrayList<>();
        detectionResults = new ArrayList<>();
    }

    public void addDetectionResult(DetectionResult result) {
        this.detectionResults.add(result);
        Log.d("ItemForEyes", "Đã thêm DetectionResult: " + result);
        invalidate(); // Yêu cầu vẽ lại
    }


    public void setOriginalImageView(ImageView originalImageView) {
        this.originalImageView = originalImageView;
        // Có thể thêm code để lưu originalBitmap nếu cần
        Log.d("ItemForEyes", "Original ImageView đã được cài đặt: " + originalImageView);
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
            currentBitmap = newBitmap; // Cập nhật bitmap hiện tại
            isOverlayVisible = true; // Đánh dấu overlay đang hiển thị
        }
        invalidate(); // Yêu cầu vẽ lại
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

            float overlayScaleFactor = 1.2f; // Tỷ lệ phóng to kính

            // Tính toán vùng bao phủ kính
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (DetectionResult result : detectionResults) {
                if ("eyes".equals(result.getClassName())) {
                    List<Integer> bbox = result.getBbox();
                    float left = bbox.get(0) * scaleX;
                    float top = bbox.get(1) * scaleY;
                    float right = bbox.get(2) * scaleX;
                    float bottom = bbox.get(3) * scaleY;

                    // Cập nhật các tọa độ tối thiểu và tối đa
                    if (left < minX) minX = left;
                    if (top < minY) minY = top;
                    if (right > maxX) maxX = right;
                    if (bottom > maxY) maxY = bottom;

                }
            }

            // Tính toán kích thước và vị trí cho kính
            float overlayWidth = (maxX - minX) * overlayScaleFactor;
            float overlayHeight = overlayBitmap != null ? (overlayBitmap.getHeight() * overlayWidth / overlayBitmap.getWidth()) : 0;

            float overlayLeft = minX + (maxX - minX - overlayWidth) / 2;
            float overlayTop = minY + (maxY - minY - overlayHeight) / 2;

            // Vẽ kính lên canvas
            if (overlayBitmap != null) {
                canvas.drawBitmap(overlayBitmap, null, new RectF(overlayLeft, overlayTop, overlayLeft + overlayWidth, overlayTop + overlayHeight), null);
            }
        }
    }


}
