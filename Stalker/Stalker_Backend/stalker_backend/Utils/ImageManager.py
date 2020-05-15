from os import getenv
from cloudinary import uploader, utils, config


class ImageManager:

    def __init__(self, image_name: str):
        self.image_name = image_name
        config(
            cloud_name=getenv('CLOUDINARY_CLOUD_NAME'),
            api_key=getenv('CLOUDINARY_API_KEY'),
            api_secret=getenv('CLOUDINARY_API_SECRET')
        )

    def upload(self, image):
        return uploader.upload(image, public_id=self.image_name)

    def get_url(self):
        return utils.cloudinary_url(self.image_name + '.jpg')[0]

    def remove(self):
        return uploader.destroy(self.image_name)
