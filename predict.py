from ultralytics import YOLO
import numpy as np
import cv2

model = YOLO('D:\\SE\\Auto-Capture-Selfie-with-Custom-Gesture\\runs\\classify\\train2\\weights\\best.pt')
# results = model('D:\\SE\\Auto-Capture-Selfie-with-Custom-Gesture\\Unlabelled\\IMG_20240222_203729.jpg')


cap = cv2.VideoCapture(0)

while True:
    ret, frame = cap.read()
    if not ret:
        break
    results = model(frame)

    name_dict = results[0].names
    print(results[0].probs.top1)

    cv2.imshow('frame', frame)

    if cv2.waitKey(1) == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()