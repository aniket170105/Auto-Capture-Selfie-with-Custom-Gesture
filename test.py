from inference_sdk import InferenceHTTPClient
from decouple import config

# initialize the client
CLIENT = InferenceHTTPClient(
    api_url= config('api_url'),
    api_key= config('api_key')
)

# infer on a local image
result = CLIENT.infer("D:\\SoftwareEngi\\New_tests\\download.jpg", model_id=config('model_id'))
print(result)
