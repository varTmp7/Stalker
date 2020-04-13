from flask_mail import Message
from stalker_backend import mail


def send_email_to_new_admins(email, password, name, surname):

    ''' NOT WORKING PROPERLY
    msg = Message("Salve %s, benvenuto in Stalker. La sua password di accesso è %s" % (name + " " + surname, password),
                  recipients=[email])
    mail.send(msg)
    '''
    pass