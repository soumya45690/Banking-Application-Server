# Banking Application Server

A learning/demo banking management app using Java 17, Spring Boot, Maven, REST APIs, Oracle Database, PL/SQL, Spring Data JPA/Hibernate, and a plain HTML/CSS/JavaScript frontend.

> Educational project only. Not for real banking or real customer data.

## Current features
- Create, list, retrieve, and update demo accounts
- Deposit and withdraw through REST endpoints
- Balance inquiry and transaction history
- Oracle PL/SQL procedures for deposit/withdraw
- Browser dashboard served by Spring Boot
- Postman collection for API testing
- Request validation and readable API errors

## Prerequisites
- JDK 17+
- Maven 3.9+
- Oracle Database with a service name (the default configuration uses `localhost:1521/FREEPDB1`)
- SQL client such as SQL Developer or SQLcl

## 1. Create a database user
Connect as an administrator to your local development database and create a dedicated schema. Example only; choose your own password and follow your lab's rules:

```sql
CREATE USER bank_app IDENTIFIED BY "choose_a_local_password";
GRANT CREATE SESSION, CREATE TABLE, CREATE PROCEDURE TO bank_app;
ALTER USER bank_app QUOTA UNLIMITED ON USERS;
```

If your Oracle setup uses a different tablespace or a different service name, adjust these commands and `application.properties`.

## 2. Create tables and procedures
Connect as `bank_app`, then run `database/oracle_setup.sql`. It creates `BANK_ACCOUNTS`, `BANK_TRANSACTIONS`, and `deposit_money` / `withdraw_money`.

## 3. Configure the application
Edit `src/main/resources/application.properties`, or set environment variables:

- `DB_URL`, e.g. `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
- `DB_USERNAME`, e.g. `bank_app`
- `DB_PASSWORD`, your local schema password

Never commit real credentials to Git.

## 4. Run
From this folder:

```bash
mvn spring-boot:run
```

Open `http://localhost:8080` in a browser. The dashboard is served from Spring Boot's `static` directory.

## REST endpoints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Create account |
| GET | `/api/accounts` | List accounts |
| GET | `/api/accounts/{id}` | Account details |
| PUT | `/api/accounts/{id}` | Update account holder/email |
| GET | `/api/accounts/{id}/balance` | Balance inquiry |
| POST | `/api/accounts/{id}/deposit` | Deposit |
| POST | `/api/accounts/{id}/withdraw` | Withdrawal |
| GET | `/api/accounts/{id}/transactions` | Transaction history |

### Example create request
`POST http://localhost:8080/api/accounts`
```json
{"accountHolder":"Demo User","email":"demo@example.com"}
```

### Example deposit request
`POST http://localhost:8080/api/accounts/1/deposit`
```json
{"amount":1000.00}
```

## Important implementation notes
- This starter uses PL/SQL procedures for balance changes. Spring's `@Transactional` is used to coordinate procedure calls and transaction-log inserts.
- The withdrawal procedure locks the selected account row and checks the current database balance before deducting funds.
- For production-grade behavior, improve error translation for Oracle errors, use a dedicated account-number generator, add authentication/authorization, apply rate limits, and add automated integration tests.
- `@CrossOrigin("*")` is convenient for a local demo; restrict origins before deployment.
- This is a project starter, not a production banking system.
