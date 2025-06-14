package com.fiap.challenge.InvestmentDataService;

import com.fiap.challenge.InvestmentDataService.client.BrapClient;
import com.fiap.challenge.InvestmentDataService.service.BrapService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvestmentDataServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(InvestmentDataServiceApplication.class, args);

		BrapService service = new BrapService(new BrapClient());
		System.out.println("Conservador:");
		service.getConservador();
		System.out.println();

		System.out.println("Moderado:");
		service.getModerado();
		System.out.println();

		System.out.println("Ousado:");
		service.getOusado();
		System.out.println();
	}

}
