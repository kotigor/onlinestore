package com.konstantinov.onlinestore.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@Schema(description = "User")
@Validated
public class User {
    @Null
    @Schema(description = "id", required = false)
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Schema(description = "number", required = true)
    @JsonProperty("number")
    private String number;

    @NotNull
    @Schema(description = "name", required = true)
    @JsonProperty("name")
    private String name;

    @NotNull
    @Schema(description = "surname", required = true)
    @JsonProperty("surname")
    private String surname;

    @Null
    @Schema(description = "orders", required = false)
    @JsonProperty("orders")
    private List<Order> orders;
}
