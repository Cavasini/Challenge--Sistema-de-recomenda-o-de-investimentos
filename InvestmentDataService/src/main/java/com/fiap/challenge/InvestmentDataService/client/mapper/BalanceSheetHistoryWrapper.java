package com.fiap.challenge.InvestmentDataService.client.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fiap.challenge.InvestmentDataService.client.brapi_dto.BalanceSheetStatement;

import java.util.List;

public class BalanceSheetHistoryWrapper {
    @JsonProperty("balanceSheetStatements")
    private List<BalanceSheetStatement> balanceSheetStatements;

    public List<BalanceSheetStatement> getBalanceSheetStatements() {
        return balanceSheetStatements;
    }

    public void setBalanceSheetStatements(List<BalanceSheetStatement> balanceSheetStatements) {
        this.balanceSheetStatements = balanceSheetStatements;
    }
}
