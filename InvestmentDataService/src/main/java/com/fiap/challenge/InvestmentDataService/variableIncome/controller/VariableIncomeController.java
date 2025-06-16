package com.fiap.challenge.InvestmentDataService.variableIncome.controller;

import com.fiap.challenge.InvestmentDataService.client.brapi_dto.StockResponseDto;
import com.fiap.challenge.InvestmentDataService.variableIncome.service.VariableIncomeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;

@RestController
@RequestMapping("api/v1/variable")
public class VariableIncomeController {

    private final VariableIncomeService variableIncomeService;

    public VariableIncomeController(VariableIncomeService variableIncomeService){
        this.variableIncomeService = variableIncomeService;
    }

    @GetMapping("/conservative")
    public ResponseEntity<Set<String>> getConservativeStocks(){
        Set<String> response = variableIncomeService.searchConservative();
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/moderate")
    public ResponseEntity<Set<String>> getModerateStocks(){
        Set<String> response = variableIncomeService.searchModerate();
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/aggressive")
    public ResponseEntity<Set<String>> getAggressiveStocks(){
        Set<String> response = variableIncomeService.searchAggressive();
        if(response.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/details")
    public ResponseEntity<StockResponseDto> searchStockDetails(@RequestBody ArrayList<String> requestBody) {
        return variableIncomeService.searchStockDetails(requestBody)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
