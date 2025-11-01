package com.stock.stockbackend.mapper;

import com.stock.stockbackend.dto.StockEntryDTO;
import com.stock.stockbackend.model.StockEntry;

public class StockEntryMapper {
    public static StockEntryDTO toDTO(StockEntry entry) {
        return StockEntryDTO.builder()
                .id(entry.getId())
                .productId(entry.getProduct().getId())
                .productName(entry.getProduct().getName())
                .quantity(entry.getQuantity())
                .reason(entry.getReason()) // NUEVO
                .date(entry.getDate())
                .build();
    }
}