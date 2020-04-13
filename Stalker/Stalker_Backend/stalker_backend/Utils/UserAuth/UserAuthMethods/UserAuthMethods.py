from abc import ABC, abstractmethod


class UserAuthMethod(ABC):
    @abstractmethod
    def login(self, user, password):
        """This method should return ad dict representing info about logged user"""
