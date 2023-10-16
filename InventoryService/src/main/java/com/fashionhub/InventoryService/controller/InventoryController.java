package com.fashionhub.InventoryService.controller;

import com.fashionhub.InventoryService.dto.InventoryResponse;
import com.fashionhub.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes){
        log.info("Inside Controller "+ skuCodes);
        return inventoryService.isInStock(skuCodes);
    }
}
