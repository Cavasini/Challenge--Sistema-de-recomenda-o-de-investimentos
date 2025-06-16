package com.fiap.challenge.InvestmentDataService.fixedIncome.model;

import java.util.List;

public record FixedIncomeResponse(List<FixedIncomeDTO> fixedIncomeDTOSList, String inflation, String primeRate) {
}
