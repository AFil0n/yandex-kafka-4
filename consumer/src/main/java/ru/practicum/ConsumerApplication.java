package ru.practicum;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
@EnableConfigurationProperties
@Slf4j
public class ConsumerApplication {

    private static final Properties props;
    private static final String TOPIC_USERS = "customers.public.users";
    private static final String TOPIC_ORDERS = "customers.public.orders";

    static {
        props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-0:9092,kafka-1:9092,kafka-2:9092"); // Адрес Kafka-брокера
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "console-consumer-group");   // Группа потребителя
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");       // Чтение с начала топика
    }

    public static void main(String[] args) {
        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
                getMessages(consumer);
        }
    }

    private static void getMessages(Consumer<String, String> consumer) {
        consumer.subscribe(List.of(TOPIC_USERS, TOPIC_ORDERS)); // Подписываемся на топик

        while (true) {
            ConsumerRecords<String, String> records;

            try {
                records = consumer.poll(Duration.ofMillis(1_000L)); // Получаем сообщения из топика (может вернуть пустые данные, если нет готовых сообщений)
            } catch (Exception e) {
                log.error("Ошибка при получении сообщений: {}", e.getMessage());
                return;
            }

            if (!records.isEmpty() && records.count() > 0) {
                log.info("Получено {} сообщений", records.count());

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Получено сообщение: {}", record.value());
                }
            }
        }

    }

}




