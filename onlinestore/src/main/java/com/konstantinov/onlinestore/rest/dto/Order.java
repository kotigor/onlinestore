package com.konstantinov.onlinestore.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.konstantinov.onlinestore.bd.orders.DeliveryMethod;
import com.konstantinov.onlinestore.bd.orders.OrderStatus;
import com.konstantinov.onlinestore.bd.orders.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "Order")
@Validated
public class Order {
    //@Null
    @Schema(description = "id", required = false)
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Schema(description = "user", required = true)
    @JsonProperty("user")
    private User user;

    @NotNull
    @Schema(description = "Id cake - count", required = true)
    @JsonProperty("cakes")
    private Map<Long, Integer> cakes;

    @NotNull
    @Schema(description = "Delivery method", required = true)
    @JsonProperty("deliveryMethod")
    private DeliveryMethod deliveryMethod;

    @NotNull
    @Schema(description = "payment", required = true)
    @JsonProperty("payment")
    private PaymentMethod payment;

    @NotNull
    @Schema(description = "address", required = true)
    @JsonProperty("address")
    private String address;

    @Null
    @Schema(description = "Order status", required = false)
    @JsonProperty("status")
    private OrderStatus status;

    @NotNull
    @Schema(description = "Order date", required = true)
    @JsonProperty("date")
    private String date;
}
