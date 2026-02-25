package shield.load.shieldapi.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.BodyInserters;
import shield.load.shieldapi.filter.RateLimiterHandlerFilterFunction;


@Configuration
public class Router {

    @Bean
    RouterFunction<ServerResponse> routes() {
        return route() //
                .GET("/api/ping", r -> ok() //
                        .contentType(TEXT_PLAIN) //
                        .body(BodyInserters.fromValue("PONG")) //
                ).filter(new RateLimiterHandlerFilterFunction()).build();
    }

}