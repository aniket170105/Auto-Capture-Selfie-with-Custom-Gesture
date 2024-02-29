from inference_sdk import InferenceHTTPClient

# initialize the client
CLIENT = InferenceHTTPClient(
    api_url="http://detect.roboflow.com",
    api_key="ilgWC7zxDlrfvA5r8V7T"
)

# infer on a local image
result = CLIENT.infer("D:\\SoftwareEngi\\New_tests\\download.jpg", model_id="smile-detection-le9k5/2")
print(result)
