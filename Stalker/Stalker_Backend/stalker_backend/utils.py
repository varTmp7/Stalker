from flask_mail import Message
from stalker_backend import mail
from os import getenv


def send_email_to_new_admins(email, password, name, surname):
    if getenv('TESTING') == True:
        return
    print("Invio email a nuovo admin")
    msg = Message("Salve %s, benvenuto in Stalker. La sua password di accesso è %s" % (name + " " + surname, password),
                  recipients=[email])
    mail.send(msg)


def send_email_reset_password(email, password, name, surname):
    if getenv('TESTING') == True:
        return
    msg = Message(
        "Salve %s, Lei ha fatto richiesta per il reset delle password. La sua nuova password di accesso è %s" % (
            name + " " + surname, password),
        recipients=[email])
    mail.send(msg)
