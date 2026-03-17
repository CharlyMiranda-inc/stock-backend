package com.stock.stockbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockEntryDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private String reason;        // NUEVO
    private LocalDateTime date;
}