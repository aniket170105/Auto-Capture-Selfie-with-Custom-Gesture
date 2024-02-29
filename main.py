from ultralytics import YOLO
model = YOLO('yolov8n-cls.pt')
model.train(data='D:\SE\Auto-Capture-Selfie-with-Custom-Gesture\Data',epochs=20,imgsz=64)

