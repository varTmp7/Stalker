#!/bin/sh

if [ "$NOT_K8S" ]
then
  echo "Not using K8S"
else
echo "Waiting for postgres..."

while ! nc -z postgres 5432; do
  sleep 0.1
done

echo "PostgreSQL started"

echo "Waiting for rethink..."

while ! nc -z rethink 28015; do
  sleep 0.1
done

echo "RethinkDB started"
fi
gunicorn -b 0.0.0.0:5000 wsgi:app