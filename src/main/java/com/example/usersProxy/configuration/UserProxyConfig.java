package com.example.usersProxy.configuration;

import com.example.usersProxy.exception.UserInternalServerException;
import com.example.usersProxy.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class UserProxyConfig {

    @Value("${usersservice.base.url}")
    private String usersServiceBaseUrl;

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(errorHandler())
                .baseUrl(usersServiceBaseUrl)
                .build();
    }

    @Bean
    public BlockingQueue<String> loginBlockingQueue(){
        return new LinkedBlockingQueue<String>();
    }

    private static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new UserInternalServerException(errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new UserNotFoundException(errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
