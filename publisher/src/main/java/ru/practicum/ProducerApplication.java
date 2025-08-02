package ru.practicum;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.Order;
import ru.practicum.model.User;
import ru.practicum.repository.OrderRepository;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@SpringBootApplication
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
public class ProducerApplication {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    List<Integer> users = new ArrayList<>();

    @PostConstruct
    public void init() {
        generateRandomEntity();
    }

    public Integer createUser() {
        User user = User.builder()
                .name("user" + (random.nextInt(9_999_999) + 1))
                .email("email" + (random.nextInt(9_999_999) + 1))
                .build();
        return userRepository.saveAndFlush(user).getId();
    }

    public Integer createOrder() {
        if(users.size() == 0){
            return -1;
        }

        long randomUserId = users.get(random.nextInt(users.size()));

        Optional<User> userOpt = userRepository.findById(randomUserId);
        User user;
        if(userOpt.isPresent()){
            user = userOpt.get();
        }else{
            return -1;
        }

        Order order = Order.builder()
                .product_name("order"+random.nextInt(99_999) + 1)
                .quantity(random.nextInt(99_999) + 1)
                .user_id(user.getId())
                .build();

        return orderRepository.saveAndFlush(order).getId();
    }

    // Метод для тестирования
    public void generateRandomEntity() {
        while (true){
            try {
                Thread.sleep(2000);

                if ((random.nextInt(10) + 1) % 2 == 0) {
                    Integer userId = createUser();
                    users.add(userId);
                    log.info("Создан пользователь с ID: {}", userId);
                } else {
                    Integer orderId = createOrder();
                    log.info("Создан заказ с ID: {}", orderId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Ожидание прервано", e);
            }
        }
    }
}