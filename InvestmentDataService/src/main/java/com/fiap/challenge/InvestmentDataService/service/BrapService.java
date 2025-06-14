package com.fiap.challenge.InvestmentDataService.service;

import com.fiap.challenge.InvestmentDataService.client.BrapClient;
import com.fiap.challenge.InvestmentDataService.model.dto.StockDTO;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BrapService {

    private final BrapClient brapClient;

    public BrapService(BrapClient brapClient){
        this.brapClient = brapClient;
    }

    public void getConservador(){

        Set<StockDTO> result = brapClient.searchConservative();

            int count = 1;

            for(StockDTO stockDTO : result){
                System.out.println(count + ":" + stockDTO.getStock());
                count++;
            }
    }

    public void getModerado(){

        Set<StockDTO> result = brapClient.searchModerate();

        int count = 1;

        for(StockDTO stockDTO : result){
            System.out.println(count + ":" + stockDTO.getStock());
            count++;
        }
    }

    public void getOusado(){

        Set<StockDTO> result = brapClient.searchAggressive();

        int count = 1;

        for(StockDTO stockDTO : result){
            System.out.println(count + ":" + stockDTO.getStock());
            count++;
        }
    }

}
