# Запуск 

- Открыть PowerShell
- Перейти в папку infra
- Выполнить docker-compose up -d

> Пытался наиболее упростить процес запуска приложения 

# Проверка

 - Открыть PowerShell
 - Перейти в папку infra
 - Дождаться запуск всех контейнеров
 - Выполнить docker exec -it postgres psql -h 127.0.0.1 -U postgres -d customers
 - Выполнить 
    ```
    SELECT table_name 
    customers-# FROM information_schema.tables
    customers-# WHERE table_schema = 'public';
    ```
   
    ``` Вывод
    table_name 
    ------------
    users
    orders
    (2 rows)

   ```
   
 - Выполнить`\q` для выхода
 - Зайти на http://localhost:8080/ (интерфейс debezium)и проверить, что создался pg-connector
 - Зайти на http://localhost:8085/ui/clusters/kraft/all-topics?perPage=25 (топики kafka) и проверить, что есть топики customers.public.orders и customers.public.users, 
 - Зайти на http://localhost:9090/targets (Prometheus) и проверить, что все ендпоинты работают
 - Зайти на http://localhost:3000/ (grafana)
 - Нажать `Add data source` и выбрать Prometheus
 - Ввести в поле URL http://localhost:9090/
 - Выбрать Access -> Browser
 - Нажать Sava and test

 - Проверить consumer log docker logs --tail 100 consumer
```
2025-08-03 00:36:52 21:36:52.773 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:36:52 21:36:52.773 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �  order20721����Õڝ
2025-08-03 00:36:54 21:36:54.777 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:36:54 21:36:54.777 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �user3012424email6860309���ŕڝ
2025-08-03 00:36:56 21:36:56.781 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:36:56 21:36:56.781 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: order965301����Ǖڝ
2025-08-03 00:36:58 21:36:58.785 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:36:58 21:36:58.785 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �  order112231�����ɕڝ
2025-08-03 00:37:00 21:37:00.789 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:37:00 21:37:00.789 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �
2025-08-03 00:37:00 order109381ڊ���˕ڝ
2025-08-03 00:37:02 21:37:02.793 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:37:02 21:37:02.793 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �user6636145email6762507���̕ڝ
2025-08-03 00:37:04 21:37:04.797 [main] INFO ru.practicum.ConsumerApplication -- Получено 1 сообщений
2025-08-03 00:37:04 21:37:04.797 [main] INFO ru.practicum.ConsumerApplication -- Получено сообщение: �user6042232email8207743���Εڝ```
```

- Проверить consumer log docker logs --tail 100 publisher

```
2025-08-02T21:37:50.323Z  INFO 1 --- [publisher] [           main] ru.practicum.ProducerApplication         : Создан пользователь с ID: 1087
2025-08-03 00:37:52 Hibernate: 
2025-08-03 00:37:52     select
2025-08-03 00:37:52         u1_0.id,
2025-08-03 00:37:52         u1_0.email,
2025-08-03 00:37:52         u1_0.name 
2025-08-03 00:37:52     from
2025-08-03 00:37:52         users u1_0 
2025-08-03 00:37:52     where
2025-08-03 00:37:52         u1_0.id=?
2025-08-03 00:37:52 Hibernate: 
2025-08-03 00:37:52     insert 
2025-08-03 00:37:52     into
2025-08-03 00:37:52         orders
2025-08-03 00:37:52         (product_name, quantity, user_id) 
2025-08-03 00:37:52     values
2025-08-03 00:37:52         (?, ?, ?)
2025-08-03 00:37:52 2025-08-02T21:37:52.327Z  INFO 1 --- [publisher] [           main] ru.practicum.ProducerApplication         : Создан заказ с ID: 565
2025-08-03 00:37:54 Hibernate: 
2025-08-03 00:37:54     insert 
2025-08-03 00:37:54     into
2025-08-03 00:37:54         users
2025-08-03 00:37:54         (email, name) 
2025-08-03 00:37:54     values
2025-08-03 00:37:54         (?, ?)
2025-08-03 00:37:54 2025-08-02T21:37:54.331Z  INFO 1 --- [publisher] [           main] ru.practicum.ProducerApplication         : Создан пользователь с ID: 1088
2025-08-03 00:37:56 Hibernate: 
2025-08-03 00:37:56     insert 
2025-08-03 00:37:56     into
2025-08-03 00:37:56         users
2025-08-03 00:37:56         (email, name) 
2025-08-03 00:37:56     values
2025-08-03 00:37:56         (?, ?)
```

# Расположение файлов

 - Файл с настройками для конфигурации Debezium Connector `infra/kafka-connect/pg-config.json`
 - Настройка  Kafka Connect `infra/kafka-connect/kafka-connect.yml`
 - Настройка prometheus `infra/prometheus/prometheus.yml`
 - Добавление конектора debezium `infra/kafka-connect/init-connector.sh`

# Компоненты и связи
 - PostgreSQL (postgres) - База данных/ Образ: debezium/postgres:16 (с поддержкой Debezium).
 - Kafka Brokers (kafka-0, kafka-1, kafka-2) - Кластер Kafka в режиме KRaft
 - Kafka UI - Веб-интерфейс для мониторинга Kafka.
 - Kafka Connect - Интеграция Kafka с внешними системами
 - Prometheus Мониторинг метрик Kafka Connect
 - Grafana - Визуализация метрик из Prometheus
 - Debezium UI - Управление Debezium через веб-интерфейс
 - Consumer - Приложение-потребитель данных из Kafka
 - Publisher - Приложения, для создания данных в таблицах PostgreSQL
 - Schema Registry - Kafka Connect настроен на AvroConverter, который требует Schema Registry

# Описание настроек Debezium Connector
`"name"` - Наименование
`"connector.class"` - класс коннектора для работы с PostgreSQL.

Параметры подключения к PostgreSQL
```
"database.hostname": "postgres",
"database.port": "5432",
"database.user": "postgres",
"database.password": "postgres",
"database.dbname": "customers",
``` 

`"database.server.name"` - имя логического сервера
`"table.include.list"` - Список таблиц, изменения которых будут отслеживаться
`"transforms": "unwrap",` - извлекает только актуальное состояние записи
`"transforms.unwrap.drop.tombstones"` - оставляет tombstone-сообщения (сигналы об удалении).
`"transforms.unwrap.delete.handling.mode"` - заменяет сообщение на null с ключом удалённой записи.

Настройка топиков Kafka
```
"topic.prefix": "customers",
"topic.creation.enable": "true",
"topic.creation.default.replication.factor": "-1",
"topic.creation.default.partitions": "-1",
```

`"skipped.operations": "none",` - Какие операции записывать в Kafka (none - все)

