# PrepaidStore

PrepaidStore is a Spring Boot application simulating a simple prepaid marketplace where users
recharge their accounts and purchase products from merchants.

## Project Modules
- **Account Management:** Users, Merchants, and System accounts.
- **Product Catalog:** Products registered by the system.
- **Merchant Inventory:** Each merchant manages its own stock and pricing.
- **Prepaid Account Transactions:** Recharge, Purchase, and Balance management.
- **Scheduled Reconciliation:** Merchants reconcile inventory stock daily.

---
## Requirements
- Java 21
- Gradle 8+
- H2 Database

---
## Technical Requirements
- Java 21
- Spring Boot 3.4.4
- Spring Data JPA
- H2 Database (for local testing)
- Flyway for database migrations
- Lombok for boilerplate reduction
- OpenAPI/Swagger for API documentation
- Gradle for build management

---
## How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/ramsvidor/prepaidstore
   ```

2. Configure your database connection in `src/main/resources/application.yaml`.

   (Default is H2 for local testing)

3. Run database migrations:
   ```bash
   ./gradlew flywayMigrate
   ```

4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

5. Access the API:
    - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    - Health Check: [http://localhost:8080/api/health](http://localhost:8080/api/health)

---
## Running Tests

To execute all unit tests:
```bash
./gradlew test
```
---
## Database default records

Flyway migration inserts some initial data for testing purposes:

- **Accounts:**
    - System Account (SYSTEM)
    - Testing User Account (USER)
    - Testing Merchant Account (MERCHANT)

- **Products:**
    - Tesla Model X
    - Tesla Model S

- **Merchant Stock:**
    - Testing Merchant Account sells Tesla Model X

Flyway script sample:
```sql
-- Insert Default Accounts
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000001', 'System Account', 'SYSTEM');
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000002', 'Testing User Account', 'USER');
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000003', 'Testing Merchant Account', 'MERCHANT');

-- Insert Default Products
INSERT INTO products (sku, name)
VALUES ('tesla-model-x', 'Tesla Model X');
INSERT INTO products (sku, name)
VALUES ('tesla-model-s', 'Tesla Model S');

-- Insert Default Merchant Stock
INSERT INTO merchant_products (merchant_id, sku, price, stock)
VALUES ('00000000-0000-0000-0000-000000000003', 'tesla-model-x', 99990, 10);
```

---
## Postman Collection

You can import the ready-to-use Postman collection for quick testing:

**File:** `src/test/resources/postman-requests-collection.json`

Postman Collection includes examples for:
- Creating Accounts
- Adding Products
- Managing Merchant Inventory
- Recharging Users
- Purchasing Products
- Checking Balances

---
## API Endpoints

### Summary
| Method | Endpoint                                | Description                        |
|:------:|:----------------------------------------|:-----------------------------------|
| GET    | `/api/health`                           | Health check                      |
| GET    | `/api/accounts`                         | List all accounts                 |
| POST   | `/api/accounts`                         | Create a new account              |
| GET    | `/api/accounts/{id}`                    | Find account by ID                |
| GET    | `/api/products`                         | List all products                 |
| POST   | `/api/products`                         | Create a new product              |
| GET    | `/api/products/{sku}`                   | Find product by SKU               |
| GET    | `/api/users`                            | List all users                    |
| GET    | `/api/users/{userId}`                   | Find user by ID                   |
| GET    | `/api/users/{userId}/balance`           | Get user's balance                |
| POST   | `/api/users/{userId}/recharge`           | Recharge user account             |
| POST   | `/api/users/{userId}/purchase`           | Purchase from a merchant          |
| POST   | `/api/merchants/{merchantId}/products`   | Add merchant's product            |
| GET    | `/api/merchants/{merchantId}/products`   | List merchant's products          |
| GET    | `/api/merchants/{merchantId}/balance`    | Get merchant's balance            |

---
### Account
| Method | Endpoint | Description |
|:------:|:--------:|:-----------|
| POST | `/api/accounts` | Create new User, Merchant, or System account |
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{id}` | Find account by ID |

Example cURL to create a user:
```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "accountType": "USER"}'
```

---

### Product
| Method | Endpoint | Description |
|:------:|:--------:|:-----------|
| POST | `/api/products` | Add a new product |
| GET | `/api/products` | List all products |
| GET | `/api/products/{sku}` | Get product by SKU |

Example cURL to add a product:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"sku": "SKU123", "name": "Product Name"}'
```

---

### Merchant
| Method | Endpoint | Description |
|:------:|:--------:|:-----------|
| POST | `/api/merchants/{merchantId}/products` | Add product to merchant inventory |
| GET | `/api/merchants/{merchantId}/products` | List products for a merchant |
| GET | `/api/merchants/{merchantId}/balance` | Get merchant balance |

Example cURL to add product to merchant:
```bash
curl -X POST http://localhost:8080/api/merchants/{merchantId}/products \
  -H "Content-Type: application/json" \
  -d '{"sku": "SKU123", "price": 100.00, "stock": 10}'
```

---

### User
| Method | Endpoint | Description |
|:------:|:--------:|:-----------|
| POST | `/api/users/{userId}/recharge` | Recharge user account |
| POST | `/api/users/{userId}/purchase` | Purchase a product |
| GET | `/api/users/{userId}/balance` | Get user balance |
| GET | `/api/users/{userId}` | Get user account by ID |
| GET | `/api/users` | List all users |

Example cURL to recharge a user:
```bash
curl -X POST http://localhost:8080/api/users/{userId}/recharge \
  -H "Content-Type: application/json" \
  -d '{"amount": 200.00}'
```

Example cURL to purchase a product:
```bash
curl -X POST http://localhost:8080/api/users/{userId}/purchase \
  -H "Content-Type: application/json" \
  -d '{"merchantId": "{merchantId}", "sku": "SKU123", "quantity": 1}'
```

---

## Notes
- **Currency:** Only "USD" supported for now.
- **Stock Reconciliation:** Runs automatically every day at 2AM.

---

## License
This project is provided for educational purposes only.