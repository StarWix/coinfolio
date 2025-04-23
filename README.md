# fina.sh

На данный момент _**fina.sh**_ представляет собой консольную версию приложения.  

Чтобы _**fina.sh**_ запускалось в интерактивном режиме нужно добавить в _application.yaml_:
```yaml
spring:
  shell:
    interactive:
      enabled: true # default false
```

Доступные команды:

- **portfolio overview**
- **pull prices**
- **pull transactions**

### Конфигурация Provider
На данный момент _Providers_ конфигурируются вручную добавлением записей в таблицы  
***provider_config*** и ***provider_config_properties***:  

### Provider Config Table
| provider_config |                   |        |
|-----------------|-------------------|--------|
| id              | name              | source |
| 1               | some_eth_provider | eth    |

### Provider Config Properties Table
| provider_config_properties |         |                                            |
|----------------------------|---------|--------------------------------------------|
| provider_config_id         | key     | value                                      |
| 1                          | address | 0x12579CeC673809F1c6e83eBE6eC7711882539559 |



