package com.epam.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // Log the error
        logger.error("Error occurred: {}", ex.getMessage(), ex);

        // Determine the HTTP status and error message
        HttpStatus status = determineHttpStatus(ex);
        String errorMessage = determineErrorMessage(ex);

        // Set response status and headers
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create error response
        String errorResponse = String.format("{\"status\": %d, \"error\": \"%s\"}", status.value(), errorMessage);

        // Write the response
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(errorResponse.getBytes())));
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
        } else if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalStateException) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // Default status
    }

    private String determineErrorMessage(Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return ((ResponseStatusException) ex).getReason() != null
                    ? ((ResponseStatusException) ex).getReason()
                    : "Undefined route or resource not found.";
        } else if (ex instanceof IllegalArgumentException) {
            return "Invalid request parameters.";
        } else if (ex instanceof IllegalStateException) {
            return "Request could not be processed due to a conflict.";
        }
        return "An unexpected error occurred. Please try again later.";
    }
}