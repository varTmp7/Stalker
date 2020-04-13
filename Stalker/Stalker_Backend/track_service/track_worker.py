import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(host='rabbitmq'))
channel = connection.channel()
channel.queue_declare(queue='tracks_queue', durable=True)


def callback(ch, method, properties, body):
    print("Received: ", body)
    track = body.decode()
    print("add", track, "to DB")


channel.basic_qos(prefetch_count=1)
channel.basic_consume(queue='tracks_queue', on_message_callback=callback())
channel.start_consuming()
