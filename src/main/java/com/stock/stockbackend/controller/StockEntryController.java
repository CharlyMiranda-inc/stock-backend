package com.stock.stockbackend.controller;

import com.stock.stockbackend.dto.StockEntryDTO;
import com.stock.stockbackend.service.StockEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-entries")
@RequiredArgsConstructor
public class StockEntryController {

    private final StockEntryService stockEntryService;

    @PostMapping
    public ResponseEntity<Void> registerStockEntry(@RequestBody StockEntryDTO dto) {
        stockEntryService.registerEntry(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<StockEntryDTO>> getAllEntries() {
        List<StockEntryDTO> entries = stockEntryService.getAllEntries();
        return ResponseEntity.ok(entries);
    }

}

