# 💸 Sistema de Recomendação de Investimentos

Este projeto é um **sistema de recomendação de investimentos personalizado**, que utiliza o perfil do usuário como base para sugerir ativos (ações, fundos e investimentos) alinhados aos seus objetivos, tolerância ao risco e interesses sociais e macroeconômicos.

---

## 🧠 Visão Geral dos Serviços

### 🔍 ProfileAnalyzerService
Responsável por analisar o perfil do investidor com base em **7 perguntas**. A partir das respostas, classifica o investidor em um dos perfis:

- **Conservador**
- **Moderado**
- **Ousado**

Além disso, identifica interesses como:
- Preocupações com **inflação** e **dólar**
- Interesse em **investimentos ESG** (com responsabilidade social e ambiental)
- Necessidade de **liquidez**

#### 📥 Exemplo de entrada:
```json
{
  "userId": "maria_iniciante",
  "answers": {
    "q1": "a",
    "q2": "c",
    "q3": "a",
    "q4": "a",
    "q5": "d",
    "q6": "a",
    "q7": "a"
  },
  "monthlyInvestmentValue": 1000.00
}
```
📤 Exemplo de retorno:
```json
{
  "user": "maria_iniciante",
  "totalScore": 13,
  "profileClassification": "Conservador",
  "identifiedInterests": {
    "liquidityNeeded": false,
    "esgInterest": "none",
    "macroeconomicConcerns": [],
    "riskToleranceNotes": "Tolerância a risco alinhada ao perfil classificado."
  }
}
```
---

### 📊 InvestmentDataService
Este serviço é responsável por **buscar e organizar dados de investimentos**, como:

- Ações brasileiras e estrangeiras
- Fundos de investimento diversos
- Investimentos de renda fixa e variável

Aplica filtros básicos conforme o perfil do investidor, retornando apenas os ativos relevantes para seus interesses identificados.

---

### 🧠 RecommenderService
O cérebro do sistema. Este serviço **processa os dados financeiros** recebidos do `InvestmentDataService` e os **combina com o perfil do usuário** (fornecido pelo `ProfileAnalyzerService`) para:

- Calcular scores de compatibilidade com cada ativo
- Avaliar riscos e retornos estimados
- Selecionar os melhores investimentos recomendados para o perfil

---

## 📦 Estrutura do Repositório

```bash
.
├── .github/workflows         # Configurações de CI/CD
├── AuthService               # Serviço de autenticação
├── InvestmentDataService     # Serviço de coleta de dados de ativos
├── ProfileAnalyzerService    # Serviço de análise de perfil do investidor
├── RecommenderService        # Serviço de recomendação personalizada
├── docker-compose.yml        # Orquestração dos serviços
└── README.md                 # Documentação do projeto
```

---

## 🚀 Executando com Docker

Para executar todos os serviços juntos:

```bash
docker-compose up --build
```

---

## ☁️ Deploy em Produção

Este sistema foi **deployado na AWS** utilizando **boas práticas de CI/CD**, garantindo:

- Automatização do pipeline de build, teste e deploy
- Versionamento contínuo
- Integração com workflows do GitHub Actions

---

## 📌 Status do Projeto

✅ Fase inicial com os serviços principais implementados.  
🚀 Deploy funcional na nuvem com CI/CD.  
🔜 Em desenvolvimento contínuo para integração com fontes de dados externas e APIs de mercado.

---

## 👨‍💻 Autor

- Emanuelle Soares - RM97973
- Julia Amorim - RM99609
- Lana Leite - RM551143
- Matheus Cavasini - RM97722


