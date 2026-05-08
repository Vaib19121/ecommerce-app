package com.ecommerce.product.dto;

import java.time.LocalDate;

public record ProductQnADto(
        Long id,
        String question,
        String answer,
        String askedBy,
        LocalDate answeredDate
) {}
