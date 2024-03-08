package com.seebie.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record Challenge(@NotBlank @Pattern(regexp= NO_WHITESPACE_AT_ENDS) String name,
                        String description,
                        @NotNull LocalDate start,
                        @NotNull LocalDate finish) {

    public static final String NO_WHITESPACE_AT_ENDS = "^\\s*$|^[^\\s].*[^\\s]$";

}
