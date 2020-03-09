FROM python:3.7.6

COPY . /app

WORKDIR /app
RUN apt-get update && \
    apt-get -y install netcat && \
    apt-get clean

RUN pip install -r requirements.txt
RUN chmod +x entrypoint.sh


ENV PYTHONDONTWRITEBYTECODE 1
ENV FLASK_APP "stalker_backend"
ENV FLASK_ENV "development"
ENV FLASK_DEBUG True

EXPOSE 5000

CMD ["./entrypoint.sh"]