FROM python:3.7.6

RUN apt-get update && \
    apt-get -y install netcat openssl dos2unix && \
    apt-get clean

RUN mkdir /app

WORKDIR /app

COPY requirements.txt ./
RUN pip install -r requirements.txt

COPY stalker_backend/ stalker_backend/
COPY wsgi.py ./
COPY entrypoint.sh ./

RUN dos2unix entrypoint.sh
RUN chmod +x entrypoint.sh


ENV PYTHONDONTWRITEBYTECODE 1
ENV FLASK_APP "stalker_backend"
ENV FLASK_ENV "development"

EXPOSE 5000

CMD ["./entrypoint.sh"]
