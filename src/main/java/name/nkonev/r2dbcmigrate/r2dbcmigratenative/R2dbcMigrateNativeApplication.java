package name.nkonev.r2dbcmigrate.r2dbcmigratenative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@ImportRuntimeHints(MigrationRuntimeHints.class)
@SpringBootApplication
public class R2dbcMigrateNativeApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(R2dbcMigrateNativeApplication.class);

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customer")
    public Flux<Customer> getCustomer() {
        return customerRepository.findAllByOrderById();
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

    public static void main(String[] args) {
		SpringApplication.run(R2dbcMigrateNativeApplication.class, args);
	}

}

class MigrationRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("db/migration/*.sql");
        }

}
