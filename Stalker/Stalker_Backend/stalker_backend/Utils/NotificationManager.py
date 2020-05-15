import requests
from os import getenv


class NotificationManger:
    __url = 'https://onesignal.com/api/v1/notifications'
    __app_id = ""
    __auth_token = ""

    def __init__(self):
        self.__app_id = getenv('APP_ID')
        self.__auth_token = getenv('NOTIFICATION_AUTH_TOKEN')

    def send_notifications(self, message):
        if getenv('TESTING'):
            return
        json_to_send = {
            "app_id": self.__app_id,
            "included_segments": "Subscribed Users",
            "data": {"foo": " bar"},
            "contents": {"en": message}
        }
        headers = {'Authorization': "Basic" + self.__auth_token}
        response = requests.post(self.__url, headers=headers, json=json_to_send)
        return response.json().get('id')
