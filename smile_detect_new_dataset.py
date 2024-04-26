from roboflow import Roboflow
from decouple import config

rf = Roboflow(api_key=config('new_api_key'))
project = rf.workspace("chengzhigang").project("smile-detect")
version = project.version(1)
dataset = version.download("yolov8")


