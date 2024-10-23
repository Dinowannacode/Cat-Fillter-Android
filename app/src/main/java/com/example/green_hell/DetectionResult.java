package com.example.green_hell;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetectionResult {
    private List<Integer> bbox;  // Giữ nguyên kiểu dữ liệu này để tương thích với API trả về

    @SerializedName("class")  // Ánh xạ trường "class" trong JSON
    private String className;

    private float confidence;

    public DetectionResult(List<Integer> bbox, String className, float confidence) {
        this.bbox = bbox;
        this.className = className;
        this.confidence = confidence;
    }

    public List<Integer> getBbox() {
        return bbox;
    }

    public void setBbox(List<Integer> bbox) {
        this.bbox = bbox;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "DetectionResult{" +
                "bbox=" + bbox +
                ", className='" + className + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
