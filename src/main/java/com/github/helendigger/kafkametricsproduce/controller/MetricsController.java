package com.github.helendigger.kafkametricsproduce.controller;

import com.github.helendigger.kafkametricsproduce.dto.Metric;
import com.github.helendigger.kafkametricsproduce.kafka.metrics.MetricsSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class MetricsController {
    private MetricsSender metricsSender;
    @PostMapping(value = "/metrics", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Post metric to service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Metric is accepted to be sent", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Metric.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Void> postMetric(@RequestBody @Validated Metric metric) {
        metricsSender.send(metric.getServiceName(), metric);
        return ResponseEntity.accepted().build();
    }

    /**
     * Handle validation errors, return bad request and a map with invalid values
     * @param exception exception to handle
     * @return response entity with bad request status and a map of invalid values and reason why they are invalid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestArguments(MethodArgumentNotValidException exception) {
        var validationErrors = exception.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.toMap(FieldError::getField, e -> Optional.ofNullable(e.getDefaultMessage())
                        .orElseGet(() -> "Invalid")));
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handle argument error, return bad request and an object {"error" : "description"} back to user
     * @param exception exception to handle
     * @return response entity with bad request status and object with error
     */
    @ExceptionHandler(value = {MethodArgumentConversionNotSupportedException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleInvalidConversion(Exception exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    /**
     * Every other error that is not validation or parsing request should be treated as Internal
     * @param throwable error that occurred inside the service
     * @return response entity with internal server error and object with error
     */
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleGenericException(Throwable throwable) {
        return ResponseEntity.internalServerError().body(Map.of("error", throwable.getMessage()));
    }
}
