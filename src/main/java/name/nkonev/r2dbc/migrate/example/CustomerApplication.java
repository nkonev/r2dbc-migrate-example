package name.nkonev.r2dbc.migrate.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
@RestController
public class CustomerApplication {

    @Autowired
    private DatabaseClient databaseClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @GetMapping("/customer")
    public Flux<Customer> getCustomer() {
        return getAllCustomersFlux(databaseClient);
    }

    @Bean
    public CommandLineRunner commandlineEntryPoint(DatabaseClient databaseClient) {
        return args -> {
            LOGGER.info("=== Print all customers ===");
            getAllCustomersFlux(databaseClient).subscribe(customer -> {
                LOGGER.info("Output: {}", customer);
            });
        };
    }

    private Flux<Customer> getAllCustomersFlux(DatabaseClient databaseClient) {
        return databaseClient.execute("SELECT * FROM customer ORDER BY id")
            .map((row, rowMetadata) -> {
                Integer id = row.get("id", Integer.class);
                String firstName = row.get("first_name", String.class);
                String lastName = row.get("last_name", String.class);
                return new Customer(id, firstName, lastName);
            }).all();
    }
}