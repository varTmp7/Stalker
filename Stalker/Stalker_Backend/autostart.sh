#!/bin/bash
echo "Inserisci la password dell'account gmail vartmp7"
read -r VAR1
VAR1="$VAR1-vartmp7@gmail.com"

pip3 install -r requirements.txt
export FLASK_APP=stalker_backend
export FLASK_ENV=development
export RETHINK_URL=localhost
export DATABASE_TYPE=sqlite:///
export TESTING=True
export FLASK_DEBUG=True
export EMAIL_PASSWORD={$VAR1}
export MAIL_SUPPRESS_SEND=False
export CLOUDINARY_CLOUD_NAME=vartmp7
export CLOUDINARY_API_KEY=151551771723931
export CLOUDINARY_API_SECRET=i440HfmeJ0x0PR2WnYXfpin8J_4


screen docker container start rethink

flask run