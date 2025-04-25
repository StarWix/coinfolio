# fina.sh

Asset History Tracker CLI

## Roadmap

| Feature                            | Version |
|------------------------------------|---------|
| Download of asset movement history | v0.1 ✅  |
| Download of historical price data  | v0.1 ✅  |
| Daily aggregated asset balances    | v0.1 ✅  |
| Data encryption                    | v0.3    |
| Daily balances per account         | v0.3    |
| Data visualization (charts)        | v0.3    |

## Roadmap by asset sources

### DEX (API integration)

| Blockchain | Transfers | Token Transfers | Stacking |
|------------|-----------|-----------------|----------|
| ETH        | v0.1 ✅    | v0.1 ✅          |          |
| ARB        | v0.1      | v0.1            |          |
| ATOM       | v0.1      |                 | v0.1     |
| DOT        | v0.1      |                 | v0.1     |
| DYDX       | v0.1      | v0.1            | v0.1     |
| OSMO       | v0.1      | v0.1            | v0.1     |
| SOL        | v0.1      |                 | v0.1     |
| BNB        | v0.1      | v0.1            |          |
| BTC        | v0.2      |                 |          |
| TRX        | v0.2      | v0.2            | v0.2     |

### DEX Liquidity (API integration)

| Protocol   | Blockchain | Liquidity Pools |
|------------|------------|-----------------|
| Uniswap v2 | ETH        | v0.2            |
| Uniswap v2 | BNB        | v0.2            |
| Uniswap v2 | ARB        | v0.2            |

### CEX (API integration)

| Exchange   | Funding History | Spot trades | Futures trades | Simple earn |
|------------|-----------------|-------------|----------------|-------------|
| bybit.com  | v0.1            | v0.1        |                | v0.1        |
| htx.com    | v0.1 🛠️        | v0.1        | v0.1           | v0.1        |
| okx.com    | v0.2            | v0.2        | v0.2           | v0.2        |
| kraken.com | v0.2            | v0.2        | v0.2           | v0.2        |

### CEX (CSV imports)

| Exchange    | Funding History | Spot trades | Margin trades | Futures trades | Simple earn |
|-------------|-----------------|-------------|---------------|----------------|-------------|
| bybit.com   |                 |             |               | v0.2           |             |
| binance.com | v0.2            | v0.2        | v0.2          | v0.2           | v0.2        |
| ftx.com     | v0.2            | v0.2        |               | v0.2           | v0.2        |

More details: https://github.com/users/StarWix/projects/9/views/3

## Development

### Available Commands:

- **describe portfolio**
- **pull prices**
- **pull transactions**

### Provider Configuration

Currently, _Providers_ are configured manually by adding entries to the  
***provider_config*** and ***provider_config_properties*** tables:

### Provider Config Table

| id | name              | source |
|----|-------------------|--------|
| 1  | some_eth_provider | eth    |

### Provider Config Properties Table

| provider_config_id | key     | value                                      |
|--------------------|---------|--------------------------------------------|
| 1                  | address | 0x12579CeC673809F1c6e83eBE6eC7711882539559 |
