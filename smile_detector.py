import numpy as np
import cv2
import matplotlib.pyplot as plt

faceCascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
smileCascade = cv2.CascadeClassifier('haarcascade_smile.xml')

cap = cv2.VideoCapture(0)
i = 0

plt.ion()  # Turn on interactive mode for matplotlib
fig, ax = plt.subplots()

while True:
    ret, img = cap.read()
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.3,
        minNeighbors=5,
        minSize=(30, 30)
    )

    for (x, y, w, h) in faces:
        cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
        roi_gray = gray[y:y + h, x:x + w]

        smile = smileCascade.detectMultiScale(
            roi_gray,
            scaleFactor=1.5,
            minNeighbors=15,
            minSize=(25, 25),
        )

        if len(smile) > 1:
            print(f"Machuda {i}")
            i += 1

        for (sx, sy, sw, sh) in smile:
            if len(smile) > 1:
                cv2.putText(img, "Smiling", (x, y - 30), cv2.FONT_HERSHEY_SIMPLEX,
                            2, (0, 255, 0), 3, cv2.LINE_AA)

    # Update the plot with the current frame
    ax.imshow(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    fig.canvas.draw()

    k = cv2.waitKey(30) & 0xff
    if k == 27:  # press 'ESC' to quit
        break

plt.ioff()  # Turn off interactive mode after the loop
cap.release()
cv2.destroyAllWindows()
