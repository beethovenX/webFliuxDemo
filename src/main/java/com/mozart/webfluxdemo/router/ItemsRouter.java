package com.mozart.webfluxdemo.router;

import com.mozart.webfluxdemo.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.mozart.webfluxdemo.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler){

        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT).and(accept(APPLICATION_JSON))
                ,itemsHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT+"/{id}").and(accept(APPLICATION_JSON))
                ,itemsHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT).and(accept(APPLICATION_JSON))
                ,itemsHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT+"/{id}").and(accept(APPLICATION_JSON))
                        ,itemsHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT+"/{id}").and(accept(APPLICATION_JSON))
                        ,itemsHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler){
        return RouterFunctions
                .route(GET("/fun/runtimeException").and(accept(APPLICATION_JSON))
                        ,itemsHandler::itemsEx);

    }

	
}
