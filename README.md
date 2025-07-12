# NTT Data Microservices Challenge

## Descrição

Aplicação baseada em microsserviços utilizando Spring Boot e Spring Cloud, aplicando os conceitos de arquitetura moderna com Service Discovery, API Gateway e comunicação entre serviços, além de aplicar persistência de dados e boas práticas REST.

**Desafio Técnico desenvolvido para a empresa NTT Data.**

## Repositório

```
nttdata-microservices-challenge
```

## Desenvolvedor

**Ezandro Bueno**

## Contexto do Negócio

Sistema de gestão de pedidos com catálogo de produtos composto por:

- **Microsserviço 1 (Catálogo de Produtos)**: Permite cadastrar, listar e consultar produtos (nome, descrição, preço). Persistência via H2 Database.
- **Microsserviço 2 (Simulador de Pedidos)**: Realiza chamadas ao Microsserviço 1 para buscar produtos disponíveis. Permite simular a criação de um pedido com base em uma lista de produtos. Não necessita de persistência.

## Arquitetura Proposta

```
Browser → API Gateway → Service Discovery
                ↓
        Microsserviço 1 ← → Microsserviço 2
                ↓
           H2 Database
```

## Estrutura do Projeto

### Eureka Server
```
src/main/java/developer/ezandro/eureka_server/
├── EurekaServerApplication.java
└── resources/
    └── application.yml
```

### Product Service
```
src/main/java/developer/ezandro/product_service/
├── config/
│   └── DataInitializer.java
├── controller/
│   └── ProductController.java
├── dto/
│   ├── ProductRequestDTO.java
│   └── ProductResponseDTO.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ProductNotFoundException.java
├── model/
│   └── Product.java
├── repository/
│   └── ProductRepository.java
├── service/
│   ├── ProductService.java
│   └── ProductServiceImpl.java
└── ProductServiceApplication.java
```

### Order Service
```
src/main/java/developer/ezandro/order_service/
├── client/
│   └── ProductClient.java
├── controller/
│   └── OrderController.java
├── dto/
│   ├── OrderItemRequestDTO.java
│   ├── OrderItemResponseDTO.java
│   ├── OrderRequestDTO.java
│   ├── OrderResponseDTO.java
│   └── ProductDTO.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── OrderNotFoundException.java
│   ├── OrderSimulationException.java
│   └── ProductNotFoundException.java
├── model/
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
├── service/
│   ├── OrderService.java
│   └── OrderServiceImpl.java
└── OrderServiceApplication.java
```

### Gateway API
```
src/main/java/developer/ezandro/gateway_api/
├── config/
│   ├── AuthInterceptor.java
│   └── WebConfig.java
└── GatewayApiApplication.java
```

## Configurações

### Portas dos Serviços

| Serviço | Porta |
|---------|-------|
| Eureka Server | 8761 |
| Product Service | 8101 |
| Order Service | 8200 |
| API Gateway | 8765 |

### Configuração do Eureka Server (application.yml)

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

xstream:
  security:
    warnings: false
```

### Configuração do Product Service (application.yml)

```yaml
server:
  port: 8101

spring:
  application:
    name: product-service
  datasource:
    url: jdbc:h2:mem:productdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka

xstream:
  security:
    warnings: false

logging:
  level:
    com.zaxxer.hikari: WARN
```

### Configuração do Order Service (application.yml)

```yaml
server:
  port: 8200

spring:
  application:
    name: order-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

logging:
  level:
    developer.ezandro.orderservice: DEBUG
    org.springframework.cloud.openfeign: DEBUG
```

### Configuração do Gateway API (application.yml)

```yaml
server:
  port: 8765

spring:
  application:
    name: gateway-api
  cloud:
    gateway:
      server:
        webmvc:
          routes:
            - id: product-service
              uri: lb://PRODUCT-SERVICE
              predicates:
                - Path=/products/**
            - id: order-service
              uri: lb://ORDER-SERVICE
              predicates:
                - Path=/orders/**
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true

logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.web: DEBUG

authentication:
  token: secret123
```

## Dados Iniciais

O Product Service possui um `DataInitializer` que carrega produtos automaticamente na inicialização:

```java
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (this.productRepository.count() == 0) {
            this.productRepository.save(new Product(null, "Product A", "Description A", 10.50));
            this.productRepository.save(new Product(null, "Product B", "Description B", 20.00));
            this.productRepository.save(new Product(null, "Product C", "Description C", 30.75));
        }
    }
}
```

**Produtos pré-carregados:**
- Product A (ID: 1) - R$ 10,50
- Product B (ID: 2) - R$ 20,00  
- Product C (ID: 3) - R$ 30,75

## Rotas da API

### Products

- **POST** `http://localhost:8765/products` - Criar produto
- **GET** `http://localhost:8765/products` - Listar produtos

### Orders

- **GET** `http://localhost:8765/orders/available-products` - Buscar produtos disponíveis
- **POST** `http://localhost:8765/orders/simulate` - Simular pedido

## Segurança

O sistema implementa autenticação simplificada com Spring Security usando token Bearer.

### Configuração de Segurança

**AuthInterceptor** - Intercepta todas as requisições para validar o token:

```java
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${authentication.token}")
    private String validToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            String token = authHeader.substring(TOKEN_PREFIX.length());

            if (validToken.equals(token)) {
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid or missing token.");
        return false;
    }
}
```

### Autenticação

- **Token fixo**: `secret123`
- **Header**: `Authorization: Bearer secret123`
- **Rotas protegidas**: `/products/**` e `/orders/**`

## Como Executar

### 1. Iniciar Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

### 2. Iniciar Product Service
```bash
cd product-service
mvn spring-boot:run
```

### 3. Iniciar Order Service
```bash
cd order-service
mvn spring-boot:run
```

### 4. Iniciar Gateway API
```bash
cd gateway-api
mvn spring-boot:run
```

### 5. Acessar Eureka Dashboard
```
http://localhost:8761
```

## Requisitos Técnicos

✅ **Conter dois microsserviços independentes**
✅ **Utilizar Spring Boot em todos os serviços**
✅ **Usar Spring Cloud Eureka como Service Discovery**
✅ **Utilizar Spring Cloud Gateway como API Gateway**
✅ **Utilizar REST APIs com boas práticas**
✅ **Garantir que:**
- Microsserviço 1 está acessível por `/products`
- Microsserviço 2 está acessível por `/orders`
- Todos os endpoints devem ser acessados via Gateway

## Extras

✅ **Implementa autenticação simplificada com Spring Security**
- Token fixo no header Authorization: Bearer {token}
- API Gateway valida o token antes de redirecionar a requisição
- Pode-se usar um filtro simples ou um AuthenticationManager customizado

## Tecnologias Utilizadas

- Java 24
- Spring Boot 3.x
- Spring Cloud Gateway
- Spring Cloud Eureka
- Spring Data JPA
- H2 Database
- Spring Security
- OpenFeign
- Maven

## Exemplos de Uso

### Listar produtos (incluindo os pré-carregados)
```bash
curl -X GET http://localhost:8765/products \
  -H "Authorization: Bearer secret123"
```

### Criar um novo produto
```bash
curl -X POST http://localhost:8765/products \
  -H "Authorization: Bearer secret123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Product D",
    "description": "Description for Product D",
    "price": 15.99
  }'
```

**Resposta:**
```json
{
    "id": 4,
    "name": "Product D",
    "description": "Description for Product D",
    "price": 15.99
}
```

### Buscar produtos disponíveis
```bash
curl -X GET http://localhost:8765/orders/available-products \
  -H "Authorization: Bearer secret123"
```

### Simular pedido
```bash
curl -X POST http://localhost:8765/orders/simulate \
  -H "Authorization: Bearer secret123" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 3,
        "quantity": 1
      }
    ]
  }'
```

**Resposta:**
```json
{
    "id": 1,
    "orderDate": "2025-07-12T11:24:52.63088803",
    "totalAmount": 51.75,
    "status": "PENDING",
    "items": [
        {
            "id": 1,
            "productId": 1,
            "productName": "Product A",
            "quantity": 2,
            "unitPrice": 10.5,
            "subtotal": 21.0
        },
        {
            "id": 2,
            "productId": 3,
            "productName": "Product C",
            "quantity": 1,
            "unitPrice": 30.75,
            "subtotal": 30.75
        }
    ]
}
```

## Observações

- **Desafio Técnico NTT Data**: Projeto desenvolvido como parte do processo seletivo
- O projeto foi desenvolvido em inglês para manter a convenção internacional
- A persistência é feita apenas no Product Service com H2 Database
- O Order Service não possui persistência, apenas simula pedidos
- Produtos são pré-carregados automaticamente via DataInitializer
- Todas as rotas são protegidas por autenticação Bearer token
- O cálculo do valor total do pedido é feito automaticamente baseado nos produtos selecionados
