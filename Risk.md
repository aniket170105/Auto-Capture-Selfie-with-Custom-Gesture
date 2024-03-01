# Smile Classification Project

## Overview
This project will classify smiles and no-smiles using a custom dataset and the YOLOv8 model. The trained model is then used in real-time to capture selfies automatically based on user-selected gestures. 

## Project Structure
- `dataset_creation/`: Contains scripts and resources for creating the custom dataset.
- `model_training/`: Includes code and files related to training the YOLOv8 model.
- `real_time_implementation/`: Implements the real-time smile classification using the trained model.
- `app_development/`: Resources for developing the mobile app for gesture-based selfie capture.

## Potential Risks
- **Data Quality:** Ensure the dataset captures diverse smile and no-smile gestures.
- **Model Performance:** Evaluate and optimize the YOLOv8 model for accurate real-time classification.
- **Hardware Compatibility:** Test the real-time implementation on different hardware setups.
- **User Experience:** Design the app interface to be intuitive and user-friendly.
- **Ethical Considerations:** Address biases and obtain user consent for image processing.

## Future Work
- Implement additional features such as emotion recognition.
- Enhance model performance through continual training and refinement.


## License
This project is licensed under [MIT License](LICENSE).

## Acknowledgments
- [YOLO](https://github.com/ultralytics/ultralytics): Inspiration for model training.
- [OpenCV](https://opencv.org/): Used for real-time image processing.

