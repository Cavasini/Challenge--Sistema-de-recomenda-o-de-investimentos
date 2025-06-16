package com.fiap.challenge.InvestmentDataService.fixedIncome.mapper;

import com.fiap.challenge.InvestmentDataService.fixedIncome.model.FixedIncomeDTO;

import java.time.LocalDate;

public class FixedIncomeMapper {

    // --- Mapeamentos dos Tesouros SELIC ---
    public static FixedIncomeDTO mapTesouroSelic2031() {
        return new FixedIncomeDTO(
                "Tesouro SELIC 2031",
                "Tesouro Selic",
                14.75, // Estimativa da Selic atual (Junho/2025)
                "Selic",
                false,
                true,
                LocalDate.of(2031, 3, 1),
                166.02,
                "Tesouro Nacional",
                1.0,
                "Tesouro Direto / XP Investimentos");
    }

    public static FixedIncomeDTO mapTesouroSelic2029() {
        return new FixedIncomeDTO(
                "Tesouro SELIC 2029",
                "Tesouro Selic",
                14.75, // Estimativa da Selic atual (Junho/2025)
                "Selic",
                false,
                true,
                LocalDate.of(2029, 3, 1),
                159.21,
                "Tesouro Nacional",
                1.0,
                "Tesouro Direto / XP Investimentos"
        );
    }

    // --- Mapeamentos dos CDBs (Pós-Fixados) ---
    public static FixedIncomeDTO mapCdbBancoXpSpecialOffer() {
        return new FixedIncomeDTO(
                "CDB Banco XP 150% CDI (Oferta Especial)",
                "CDB",
                150.00, // % do CDI
                "CDI",
                false,
                true,
                LocalDate.of(2025, 8, 14),
                20000.00,
                "Banco XP",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbWillFinanceira122Cdi() {
        return new FixedIncomeDTO(
                "Will Financeira 122% CDI",
                "CDB",
                122.00,
                "CDI",
                false,
                false,
                LocalDate.of(2028, 6, 13),
                1000.00,
                "Will Financeira",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbLusoBrasileiro110Cdi() {
        return new FixedIncomeDTO(
                "Luso Brasileiro 110% CDI",
                "CDB",
                110.00,
                "CDI",
                false,
                false,
                LocalDate.of(2028, 6, 13),
                1000.00,
                "Luso Brasileiro",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbMidwayFinanceira109Cdi() {
        return new FixedIncomeDTO(
                "Midway Financeira 109% CDI",
                "CDB",
                109.00,
                "CDI",
                false,
                false,
                LocalDate.of(2028, 6, 13),
                1000.00,
                "Midway Financeira",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbOurinvest108_5Cdi() {
        return new FixedIncomeDTO(
                "Ourinvest 108.5% CDI",
                "CDB",
                108.50,
                "CDI",
                false,
                false,
                LocalDate.of(2028, 6, 13),
                1000.00,
                "Ourinvest",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbRciBrasil102Cdi() {
        return new FixedIncomeDTO(
                "RCI Brasil 102% CDI",
                "CDB",
                102.00,
                "CDI",
                false,
                false,
                LocalDate.of(2028, 6, 13),
                1000.00,
                "RCI Brasil",
                2.0,
                "XP Investimentos"
        );
    }

    public static FixedIncomeDTO mapCdbPineJun2027() {
        return new FixedIncomeDTO(
                "CDB Pine – JUN/2027",
                "CDB",
                116.5,
                "CDI",
                false,
                false,
                LocalDate.of(2027, 6, 30),
                1000.00,
                "Banco Pine",
                2.0,
                "XP Investimentos"
        );
    }

    // --- Mapeamentos das LCAs (Prefixadas ou Pós-fixadas) ---
    public static FixedIncomeDTO mapLcaBancoBvAgosto2025() {
        return new FixedIncomeDTO(
                "LCA BANCO BV S/A – AGO/2025",
                "LCA",
                10.85,
                "Prefixado",
                true,
                false,
                LocalDate.of(2025, 8, 7),
                1000.00,
                "Banco BV S/A",
                2.0,
                "XP Investimentos"
        );
    }

    // --- Novos Mapeamentos (Renda Fixa IPCA e Global) ---

    // Tesouro IPCA+
    public static FixedIncomeDTO mapTesouroIpcaMais2029() {
        return new FixedIncomeDTO(
                "Tesouro IPCA+ 2029",
                "Tesouro IPCA+",
                7.61, // Taxa real (IPCA + 7.61%, referência Junho/2025)
                "IPCA",
                false,
                true, // Liquidez diária, mas com marcação a mercado
                LocalDate.of(2029, 5, 15),
                50.00, // Preço unitário fracionado
                "Tesouro Nacional",
                1.0,
                "Tesouro Direto"
        );
    }

    public static FixedIncomeDTO mapTesouroIpcaMaisComJurosSemestrais2050() {
        return new FixedIncomeDTO(
                "Tesouro IPCA+ com Juros Semestrais 2050",
                "Tesouro IPCA+ (com juros)",
                7.00, // Taxa real (IPCA + 7.00%, referência Junho/2025)
                "IPCA",
                false,
                true, // Liquidez diária, mas com marcação a mercado
                LocalDate.of(2050, 8, 15),
                50.00,
                "Tesouro Nacional",
                1.0,
                "Tesouro Direto"
        );
    }

    // Títulos Globais
    public static FixedIncomeDTO mapUsTreasuryBond10Year() {
        return new FixedIncomeDTO(
                "US Treasury Bond 10-Year",
                "Bond (Soberano)",
                4.57, // Rendimento de referência (yield to maturity, Jan/2025)
                "USD Yield",
                false, // Não isento de IR para residentes brasileiros
                true, // Alta liquidez
                LocalDate.of(2035, 5, 15),
                1000.00, // Exemplo de investimento mínimo (nominal)
                "Governo dos EUA (US Treasury)",
                1.0,
                "Plataforma Global (Nomad/Inter)"
        );
    }

    public static FixedIncomeDTO mapMicrosoftCorpBond2030() {
        return new FixedIncomeDTO(
                "Microsoft Corp Bond 2030",
                "Bond (Corporativo)",
                5.50, // Rendimento de referência
                "USD Yield",
                false,
                false, // Liquidez pode variar
                LocalDate.of(2030, 9, 1),
                1000.00,
                "Microsoft Corporation",
                1.5,
                "Plataforma Global (Nomad/Inter)"
        );
    }

    public static FixedIncomeDTO mapIsharesGlobalAggregateBondEtf() {
        return new FixedIncomeDTO(
                "Ishares Global Aggregate Bond ETF",
                "ETF (Renda Fixa)",
                3.80, // Rendimento médio do portfólio do ETF
                "USD Yield",
                false,
                true, // Liquidez diária de ETF
                null, // ETFs não têm vencimento fixo individual
                50.00, // Preço da cota
                "Diversos (via BlackRock)",
                2.0,
                "Plataforma Global (Nomad/Inter)"
        );
    }
}