package com.stock.stockbackend.service;

import com.stock.stockbackend.dto.StockEntryDTO;
import com.stock.stockbackend.mapper.StockEntryMapper;
import com.stock.stockbackend.model.Product;
import com.stock.stockbackend.model.StockEntry;
import com.stock.stockbackend.model.User;
import com.stock.stockbackend.repository.ProductRepository;
import com.stock.stockbackend.repository.StockEntryRepository;
import com.stock.stockbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerEntry(StockEntryDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setStock((product.getStock() == null ? 0 : product.getStock()) + dto.getQuantity());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null); // opcional

        StockEntry entry = StockEntry.builder()
                .product(product)
                .quantity(dto.getQuantity())
                .reason(dto.getReason())
                .date(LocalDateTime.now())
                .user(user)
                .build();

        stockEntryRepository.save(entry);
        productRepository.save(product);
    }

    public List<StockEntryDTO> getAllEntries() {
        List<StockEntry> entries = stockEntryRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        return entries.stream().map(StockEntryMapper::toDTO).toList();
    }
}
