#!/bin/sh

echo "Waiting for postgres..."

while ! nc -z postgresql-1-postgresql-svc 5432; do
  sleep 0.1
done

echo "PostgreSQL started"

echo "Waiting for rethink..."

while ! nc -z rethinkdb-proxy 28015; do
  sleep 0.1
done

echo "RethinkDB started"

if [ "$LOCAL_HTTPS" ]; then
  echo "Running in local https mode"
  echo "Generating self-signed certificate"
  openssl req -x509 -newkey rsa:4096 -nodes -keyout key.pem -out cert.pem -days 365 -subj "/C=US/ST=Denial/L=Springfield/O=Dis/CN=www.example.com"
  gunicorn --certfile=cert.pem --keyfile=key.pem -b 0.0.0.0:443 wsgi:app
else
  echo "Gunicorn mode"
  gunicorn -b 0.0.0.0:5000 wsgi:app
fi
