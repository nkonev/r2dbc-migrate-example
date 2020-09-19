package name.nkonev.r2dbc.migrate.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerApplicationTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("Should return list")
    void shouldGetCustomers()  {
        webClient.mutateWith((builder, webHttpHandlerBuilder, clientHttpConnector) -> builder.responseTimeout(Duration.of(2, ChronoUnit.MINUTES)))
                .get().uri("/customer")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .value(List::size, equalTo(3))
                .value(customers -> customers.get(0).getFirstName(), equalTo("Test Customer Name 1"))
                .value(customers -> customers.get(1).getFirstName(), equalTo("Funny User Name 2"))
        ;
    }

    @Test
    @DisplayName("Should return item")
    void shouldGetCustomerById()  {
        webClient.mutateWith((builder, webHttpHandlerBuilder, clientHttpConnector) -> builder.responseTimeout(Duration.of(2, ChronoUnit.MINUTES)))
                .get().uri("/customer/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Customer.class)
                .value(Customer::getFirstName, equalTo("Test Customer Name 1"))
        ;
    }
}