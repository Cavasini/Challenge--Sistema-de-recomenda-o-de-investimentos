package com.fiap.challenge.InvestmentDataService.fixedIncome.controller;

import com.fiap.challenge.InvestmentDataService.fixedIncome.model.FixedIncomeDTO;
import com.fiap.challenge.InvestmentDataService.fixedIncome.model.FixedIncomeResponse;
import com.fiap.challenge.InvestmentDataService.fixedIncome.service.FixedIncomeAdjustmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/v1/fixedIncome")
public class FixedIncomeController {

    private final FixedIncomeAdjustmentService fixedIncomeAdjustmentService;

    public FixedIncomeController(FixedIncomeAdjustmentService fixedIncomeAdjustmentService){
        this.fixedIncomeAdjustmentService = fixedIncomeAdjustmentService;
    }

    @GetMapping
    public ResponseEntity<FixedIncomeResponse> getConservativeStocks(){
        FixedIncomeResponse response = fixedIncomeAdjustmentService.searchFixedIncomes();
        if(response == null){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(response);
    }
}
