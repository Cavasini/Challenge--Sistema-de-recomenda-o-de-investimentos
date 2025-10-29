package com.fiap.challenge.InvestmentDataService.fixedIncome.service;

import com.fiap.challenge.InvestmentDataService.client.BrapApiClient;
import com.fiap.challenge.InvestmentDataService.fixedIncome.mapper.FixedIncomeMapper;
import com.fiap.challenge.InvestmentDataService.fixedIncome.model.FixedIncomeDTO;
import com.fiap.challenge.InvestmentDataService.fixedIncome.model.FixedIncomeResponse;
import com.fiap.challenge.InvestmentDataService.client.brapi_dto.InflationDataDTO;
import com.fiap.challenge.InvestmentDataService.client.brapi_dto.PrimeRateDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class FixedIncomeAdjustmentService {

    private static final Logger log = LoggerFactory.getLogger(FixedIncomeAdjustmentService.class);
    private static final DecimalFormat DECIMAL_FORMAT_BR = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));


    private final BrapApiClient brapApiClient;

    public FixedIncomeAdjustmentService(BrapApiClient brapApiClient) {
        this.brapApiClient = brapApiClient;
    }

    public FixedIncomeResponse searchFixedIncomes() {
        List<FixedIncomeDTO> fixedIncomeDTOS = new ArrayList<>();
        fixedIncomeDTOS.add(FixedIncomeMapper.mapTesouroSelic2029());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapTesouroSelic2031());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbWillFinanceira122Cdi());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbLusoBrasileiro110Cdi());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbOurinvest108_5Cdi());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbRciBrasil102Cdi());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbBancoXpSpecialOffer());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapLcaBancoBvAgosto2025());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbPineJun2027());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapCdbMidwayFinanceira109Cdi());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapTesouroIpcaMais2029());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapTesouroIpcaMaisComJurosSemestrais2050());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapUsTreasuryBond10Year());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapMicrosoftCorpBond2030());
        fixedIncomeDTOS.add(FixedIncomeMapper.mapIsharesGlobalAggregateBondEtf());


        Optional<InflationDataDTO> inflationDataOptional = brapApiClient.getInflation();
        String inflationValue = inflationDataOptional
                .map(InflationDataDTO::value)
                .orElse("3.5");


        Optional<PrimeRateDataDTO> primeRateDataOptional = brapApiClient.getPrimeRate();
        String selicValue = primeRateDataOptional
                .map(PrimeRateDataDTO::value)
                .orElse("10.5");

        return new FixedIncomeResponse(fixedIncomeDTOS, inflationValue, selicValue);
    }


}