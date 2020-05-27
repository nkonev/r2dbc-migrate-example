package name.nkonev.r2dbc.migrate.example;

import io.r2dbc.spi.ConnectionFactoryOptions;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
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

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @GetMapping("/customer")
    public Flux<Customer> getCustomer() {
        return databaseClient.execute("SELECT * FROM customer ORDER BY id").map((row, rowMetadata) -> {
            Integer id = row.get("id", Integer.class);
            String firstName = row.get("first_name", String.class);
            String lastName = row.get("last_name", String.class);
            return new Customer(id, firstName, lastName);
        }).all();
    }

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer setConnectTimeout() {
        // PostgresqlConnectionFactoryProvider
        // ConnectionFactoryOptions
        return builder -> builder.option(ConnectionFactoryOptions.CONNECT_TIMEOUT, Duration.ofSeconds(10));
    }

}