package shield.load.shieldapi.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Configuration
public class TestRouter {

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(GET("/api/ping"), request ->
                ServerResponse.ok()
                        .contentType(TEXT_PLAIN)
                        .body(BodyInserters.fromValue("PONG"))
        );
    }
}