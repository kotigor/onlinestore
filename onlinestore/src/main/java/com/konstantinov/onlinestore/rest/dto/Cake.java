package com.konstantinov.onlinestore.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@Schema(description = "Short info about cakes")
@Validated
public class Cake {
    @NotNull
    @Schema(description = "Name", required = true)
    @JsonProperty("name")
    private String name;

    @NotNull
    @Schema(description = "Calories of cake", required = true)
    @JsonProperty("calories")
    private BigDecimal calories;

    @NotNull
    @Schema(description = "Relative url of cake image", required = true)
    @JsonProperty("image")
    private String image;

    @NotNull
    @Schema(description = "Price of cake", required = true)
    @JsonProperty("price")
    private BigDecimal price;

    @NotNull
    @Schema(description = "Cake weight", required = true)
    @JsonProperty("weight")
    private BigDecimal weight;

    @Null
    @Schema(description = "id", required = false)
    @JsonProperty("id")
    private Long id;
}
