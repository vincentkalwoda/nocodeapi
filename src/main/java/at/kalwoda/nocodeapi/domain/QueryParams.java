package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record QueryParams(
        @NotBlank(message = "Key cannot be blank")
        String key,

        @NotNull(message = "Value cannot be null")
        String value
) {
}
