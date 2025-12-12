package name.nkonev.r2dbc.migrate.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
public class CustomerApplicationTest {

    @Autowired
    private WebTestClient webClient;

    @ServiceConnection
    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:14.1-alpine3.14");

    @Test
    @DisplayName("Should return list")
    void shouldGetCustomers()  {
        webClient.mutateWith((builder, webHttpHandlerBuilder, clientHttpConnector) -> builder.responseTimeout(Duration.of(2, ChronoUnit.MINUTES)))
                .get().uri("/customer")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .value(customers -> {
                    assertThat(customers.get(0).getFirstName()).isEqualTo("Test Customer Name 1");
                    assertThat(customers.get(1).getFirstName()).isEqualTo("Funny User Name 2");
                    assertThat(customers.get(2).getLastName()).isEqualTo("Unicode escape as workaround");
                    assertThat(customers.get(7).getFirstName()).isEqualTo("Only Test");
                })
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
                .value(customer -> {
                    assertThat(customer.getFirstName()).isEqualTo("Test Customer Name 1");
                })
        ;
    }
}
