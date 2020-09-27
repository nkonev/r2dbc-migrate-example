package name.nkonev.r2dbc.migrate.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import java.util.Collections;
import name.nkonev.r2dbc.migrate.core.R2dbcMigrate;
import name.nkonev.r2dbc.migrate.core.R2dbcMigrateProperties;
import name.nkonev.r2dbc.migrate.reader.ReflectionsClasspathResourceReader;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import java.io.ByteArrayOutputStream;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

public class CustomerApplication {

    static final ObjectMapper mapper = new ObjectMapper();

    private static byte[] toByteArray(Object any) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            mapper.writeValue(out, any);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public static void main(String[] args) {
        R2dbcMigrateProperties properties = new R2dbcMigrateProperties();
        properties.setResourcesPaths(Collections.singletonList("db/migration/"));
        ConnectionFactory connectionFactory = getConnectionFactory();

        R2dbcMigrate.migrate(connectionFactory, properties, new ReflectionsClasspathResourceReader()).block();

        DisposableServer server =
                HttpServer.create()
                        .port(8383)
                        .route(routes ->
                                routes.get("/customer",
                                        (request, response) -> {
                                          Flux<Customer> customerFlux = Flux.usingWhen(connectionFactory.create(),
                                              connection -> Flux.from(connection.createStatement("SELECT * FROM customer ORDER BY id").execute()).concatMap(o -> o.map((row, rowMetadata) -> {
                                                Integer id = row.get("id", Integer.class);
                                                String firstName = row.get("first_name", String.class);
                                                String lastName = row.get("last_name", String.class);
                                                return new Customer(id, firstName, lastName);
                                              })), Connection::close);
                                          ByteBufFlux byteBufFlux = ByteBufFlux.fromInbound(customerFlux.map(CustomerApplication::toByteArray));
                                          return response.send(byteBufFlux);
                                        }))
                        .bindNow();

        server.onDispose()
                .block();
    }

    private static ConnectionFactory getConnectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                    .option(DRIVER, "postgresql")
                    .option(HOST, "127.0.0.1")
                    .option(PORT, 4433)
                    .option(USER, "app_user")
                    .option(PASSWORD, "password")
                    .option(DATABASE, "customer_db")
                    .build());
    }

}