# CAT FACIAL COMPONENT DETECTION USING CONVOLUTIONAL NEURAL NETWORKS

This project is an Android application integrated with a Python Flask backend server. The Android app captures an image, sends it to the Flask server, and receives a response with the processed result returned from a YOLOV9 model already trained with custom data. The result is used to overlay items (e.g., glasses, mouth, etc.) on a cat's face in the image.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Running the Server](#running-the-server)
- [Running the Android App](#running-the-android-app)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This project demonstrates the integration of an Android app with a Flask backend for cat facial component detection. The app enables users to capture or select an image, which is then processed by the server using a custom-trained YOLOV9 model. The server detects facial features on the cat and sends back the results to the app, where the corresponding overlay (e.g., glasses, mouth, etc.) is drawn on the detected regions.

## Features

- Select or capture an image from the Android device.
- Send the image to a Flask backend server for processing.
- Receive and parse the detection results from the server.
- Overlay images (such as glasses or other items) onto specific facial components (eyes, mouth) detected in the cat's face.
- Handle multiple overlays dynamically based on detection results.

## Technologies Used

### Android
- **Language**: Java/Kotlin
- **Framework**: Android SDK
- **Networking**: Retrofit/OkHttp (for making HTTP requests)
- **Image Loading**: Glide/Picasso (for handling images in the app)
- **UI**: ConstraintLayout, Custom Views (e.g., `ItemForEyes`, `ItemForMouth`), RecyclerView
- **JSON Parsing**: Gson/Moshi (for parsing JSON responses from the server)
- **Image Processing**: Custom Canvas drawing methods to overlay images on detected regions

### Backend (Flask)
- **GIT**: https://github.com/WongKinYiu/yolov9.gits
- **Language**: Python
- **Framework**: Flask (for creating the web server and handling requests)
- **Deep Learning**: YOLOV9 (custom-trained model for cat facial component detection)
- **Image Processing**: OpenCV/PIL (for handling and preprocessing images)
- **Data Handling**: JSON (for sending and receiving data)
- **Deployment**: Gunicorn/WSGI (for serving the Flask app in production)
- **Environment Management**: virtualenv (for managing Python dependencies)
- **Testing**: unittest/PyTest (for writing and running tests)

## Getting Started

To get started with this project, follow the instructions below.

### Prerequisites

- **Android Studio**: Make sure you have the latest version of Android Studio installed.
- **Python 3.x**: Install Python for running the Flask server.
- **virtualenv**: (Optional) for managing Python dependencies.
- **YOLOV9 model**: Ensure you have the custom-trained YOLOV9 model for detecting cat facial components.

### Running the Server

1. Clone the repository to your local machine.
2. Navigate to the backend directory: `cd yolov9`
3. Create a virtual environment and activate it:
4. Install the required dependencies: `pip3 install -r requirements.txt`
5. On Mac: Run the Flask server:`python3 main.py`
6. Example Response:
{
  "detections": [
    {
      "bbox": [x1, y1, x2, y2],
      "className": "eyes",
      "confidence": 0.95
    },
    {
      "bbox": [x1, y1, x2, y2],
      "className": "mouth",
      "confidence": 0.90
    }
  ]
}

### Running the Android App

1. Open the Android project in Android Studio.
2. Sync the project with Gradle files to ensure all dependencies are installed.
3. Run the app on an emulator or physical device.
4. In the app, capture or select an image of a cat, and send it to the server for processing.

### API Endpoints
### POST /process_image
- **Description**: Accepts an image file, processes it using the YOLOV9 model, and returns the detected bounding boxes with their corresponding class names (e.g., eyes, mouth).
- **Request Body**: Multipart/form-data containing the image file.
- **Response**: JSON containing the bounding boxes, class names, and confidence scores.

### Contributing
- Hồ Nhật Duy: https://github.com/Dinowannacode
- Huỳnh Thanh Duy: https://github.com/Duy-quan

### License
This `README.md` should provide a clear and comprehensive overview of your project, including instructions for setting up and running both the Android app and Flask server, as well as information on contributing to the project.
