package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

import static com.seebie.server.validation.ValidationConstant.NO_WHITESPACE_AT_ENDS;

public record Challenge(@NotBlank @Pattern(regexp= NO_WHITESPACE_AT_ENDS) String name,
                        @NotBlank String description,
                        @NotNull LocalDate start,
                        @NotNull LocalDate finish) {

}
