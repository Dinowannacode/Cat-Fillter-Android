from flask import Flask, request, jsonify, send_file
from PIL import Image
import numpy as np
import cv2
import io
import os
import torch
from pathlib import Path
from detect import run

app = Flask(__name__)

# Đường dẫn đến mô hình YOLOv9
weights_path = Path('best.pt')

@app.route('/detect', methods=['POST'])
def detect():
    if 'image' not in request.files:
        return jsonify({'error': 'No image part'}), 400

    file = request.files['image']
    if not file:
        return jsonify({'error': 'Something wrong'}), 400
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    img = Image.open(file.stream).convert('RGB')
    img = np.array(img)

    # Lưu ảnh tạm thời để chạy inference
    temp_image_path = 'temp_image.jpg'
    img_pil = Image.fromarray(img)
    img_pil.save(temp_image_path)

    # Chạy inference và lấy kết quả
    detections = run(weights=weights_path, source=temp_image_path)

    # Xóa ảnh tạm thời
    if os.path.exists(temp_image_path):
        os.remove(temp_image_path)

    # Trả về kết quả dưới dạng JSON
    return jsonify(detections)

if __name__ == '__main__':
    app.run(debug=True)
