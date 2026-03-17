package com.stock.stockbackend.service;

import com.stock.stockbackend.dto.*;
import com.stock.stockbackend.exception.InsufficientStockException;
import com.stock.stockbackend.exception.ResourceNotFoundException;
import com.stock.stockbackend.model.*;
import com.stock.stockbackend.repository.ProductRepository;
import com.stock.stockbackend.repository.SaleItemRepository;
import com.stock.stockbackend.repository.SaleRepository;
import com.stock.stockbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final SaleItemRepository saleItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Sale createSale(SaleRequestDTO request) {
        List<SaleItem> saleItems = new ArrayList<>();
        double total = 0.0;

        for (SaleRequestDTO.ItemDTO itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemDTO.getProductId()));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar stock
            product.setStock(product.getStock() - itemDTO.getQuantity());

            double selectedPrice = getPriceByPaymentMethod(product, PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));

            double itemTotal = selectedPrice * itemDTO.getQuantity();
            total += itemTotal;

            SaleItem saleItem = SaleItem.builder()
                .product(product)
                .quantity(itemDTO.getQuantity())
                .price(selectedPrice)
                .build();

            saleItems.add(saleItem);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        Sale sale = Sale.builder()
            .paymentMethod(request.getPaymentMethod())
            .date(LocalDateTime.now())
            .total(total)
            .user(user)
            .build();

        for (SaleItem item : saleItems) {
            item.setSale(sale);
        }

        sale.setItems(saleItems);
        return saleRepository.save(sale);
    }

    public List<Sale> getSalesBetween(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByDateBetween(start, end);
    }

    public List<ProductSalesReportDTO> getProductSalesReport(LocalDateTime start, LocalDateTime end) {
        return saleItemRepository.getProductSalesReport(start, end);
    }

    public List<DailySalesReportDTO> getDailySalesReport(LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = saleRepository.getDailySalesReport(start, end);
        return results.stream()
            .map(row -> new DailySalesReportDTO(
                    ((Date) row[0]).toLocalDate(),      // saleDate
                    ((Number) row[1]).longValue(),      // totalSalesCount
                    ((Number) row[2]).doubleValue()     // totalAmount
            ))
            .collect(Collectors.toList());
    }

    public List<UserSalesReportDTO> getSalesByUser(LocalDateTime from, LocalDateTime to) {
        return saleRepository.getSalesByUserBetween(from, to);
    }

    public List<SaleResponseDTO> getSalesAsDTO(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByDateBetween(start, end).stream()
            .map(this::convertToSaleResponseDTO)
            .collect(Collectors.toList());
    }

    private SaleResponseDTO convertToSaleResponseDTO(Sale sale) {
        List<SaleResponseDTO.SaleItemDTO> itemDTOs = sale.getItems().stream()
            .map(item -> new SaleResponseDTO.SaleItemDTO(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice()
            ))
            .collect(Collectors.toList());

        return new SaleResponseDTO(
            sale.getId(),
            sale.getDate(),
            sale.getPaymentMethod(),
            sale.getTotal(),
            sale.getUser().getEmail(),
            itemDTOs
        );
    }

    private double getPriceByPaymentMethod(Product product, PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case TRANSFERENCIA -> product.getTransferPrice();
            case EFECTIVO -> product.getCashPrice();
            case LISTA -> product.getListPrice();
        };
    }

}
