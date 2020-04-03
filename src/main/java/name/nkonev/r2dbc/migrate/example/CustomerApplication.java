package name.nkonev.r2dbc.migrate.example;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
@RestController
public class CustomerApplication {

    @Autowired
    private ConnectionFactory connectionFactory;

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @GetMapping("/customer")
    public Flux<Customer> getCustomer() {
        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection.createStatement("SELECT * FROM customer").execute()).flatMap(o -> o.map((row, rowMetadata) -> {
                    Integer id = row.get("id", Integer.class);
                    String firstName = row.get("first_name", String.class);
                    String lastName = row.get("last_name", String.class);
                    return new Customer(id, firstName, lastName);
                })).doFinally(signalType -> connection.close()));
    }

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer setConnectTimeout() {
        // PostgresqlConnectionFactoryProvider
        // ConnectionFactoryOptions
        return builder -> builder.option(ConnectionFactoryOptions.CONNECT_TIMEOUT, Duration.ofSeconds(10));
    }

}