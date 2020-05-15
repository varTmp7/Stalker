from flask import request, abort, jsonify
from flask_restful import Resource
from werkzeug.utils import secure_filename
from secrets import token_hex

from stalker_backend.config import ALLOWED_EXTENSIONS
from stalker_backend.Utils.ImageManager import ImageManager


class ImageList(Resource):

    @staticmethod
    def allowed_file(filename):
        return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

    def post(self):
        image = request.files.get('file')
        if not image and image.filename == '':
            abort(400, description='No selected file')
        if self.allowed_file(image.filename):
            filename = secure_filename(image.filename)
            hash_filename = token_hex(16)
            uploader = ImageManager(hash_filename)
            response = uploader.upload(image)
            return jsonify(image_url=response.get('url'))
        else:
            abort(400, description='Filename not allowed')
