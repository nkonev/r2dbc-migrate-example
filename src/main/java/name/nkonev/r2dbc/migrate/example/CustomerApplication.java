package name.nkonev.r2dbc.migrate.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class CustomerApplication {

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @GetMapping("/customer")
    public Flux<Customer> getCustomer() {
        return customerRepository.findAllByOrderById();
    }

    @GetMapping("/customer/{id}")
    public Mono<Customer> getCustomerById(@PathVariable int id) {
        return customerRepository.findById(id);
    }

    @Bean
    public CommandLineRunner commandlineEntryPoint() {
        return args -> {
            LOGGER.info("=== Print all customers ===");
            customerRepository.findAllByOrderById().subscribe(customer -> {
                LOGGER.info("Output: {}", customer);
            });
        };
    }

}