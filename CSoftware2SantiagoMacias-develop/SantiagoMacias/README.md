# 🏦 Bank Management System — Hexagonal Architecture + DDD + Spring Boot

## Arquitectura

```
┌──────────────────────────────────────────────────────────────────┐
│  INTERFACE LAYER (adapter/in/web)                                │
│  AuthController │ UserController │ AccountController             │
│  LoanController │ TransferController │ AuditLogController        │
└──────────────────────┬───────────────────────────────────────────┘
                       │ calls Input Ports
┌──────────────────────▼───────────────────────────────────────────┐
│  APPLICATION LAYER (application/)                                │
│  UserUseCase │ AccountUseCase │ LoanUseCase                      │
│  TransferUseCase │ AuditLogUseCase                               │
│  Ports: Input (interfaces) │ Output (interfaces)                 │
│  DTOs: UserDto │ BankingDto                                      │
└──────────────────────┬───────────────────────────────────────────┘
                       │ uses Domain
┌──────────────────────▼───────────────────────────────────────────┐
│  DOMAIN LAYER (domain/)  ← NO Spring, NO JPA                    │
│  Entities: User │ AuditLog                                       │
│  Aggregates: BankAccount │ Loan │ Transfer                       │
│  Value Objects: Email │ PhoneNumber │ Money │ Enums              │
│  Domain Services: TransferDomainService │ LoanDisbursementService│
│  Repository Interfaces (output ports at domain level)            │
└──────────────────────┬───────────────────────────────────────────┘
                       │ implemented by
┌──────────────────────▼───────────────────────────────────────────┐
│  INFRASTRUCTURE LAYER (adapter/out/persistence)                  │
│  JPA Entities │ Spring Data Repositories │ Mappers               │
│  UserPersistenceAdapter │ BankAccountPersistenceAdapter          │
│  LoanPersistenceAdapter │ TransferPersistenceAdapter             │
│  AuditLogPersistenceAdapter (NoSQL simulation via JSON)          │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Cómo ejecutar

### Requisitos
- Java 17+
- Maven 3.8+
- VS Code con "Extension Pack for Java"

### Ejecutar
```bash
cd bank-hexagonal
mvn clean spring-boot:run
```

O en VS Code: abrir `BankApplication.java` → Run

### URLs
| URL | Descripción |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | **Swagger UI (Postman alternativo)** |
| http://localhost:8080/h2-console | H2 Console (ver BD en memoria) |
| http://localhost:8080/api-docs | OpenAPI JSON |

**H2 Console config:** JDBC URL: `jdbc:h2:mem:bankdb` | User: `sa` | Password: (vacío)

---

## 🔑 Credenciales de prueba

| Rol                 | ID      | Password       | Cuenta          | Saldo    |
|---------------------|---------|----------------|-----------------|----------|
| Internal Analyst    | ANA001  | analyst123     | —               | —        |
| Teller              | TEL001  | teller123      | —               | —        |
| Commercial Employee | COM001  | commercial123  | —               | —        |
| Client Individual 1 | CLI001  | client123      | ACC0000000001   | $15,000  |
| Client Individual 2 | CLI002  | client456      | ACC0000000002   | $8,500   |
| Company Admin       | CORP001 | company123     | ACC0000000003   | $250,000 |
| Company Supervisor  | SUP001  | supervisor123  | —               | —        |
| Company Employee    | EMP001  | employee123    | —               | —        |

---

## 📬 Guía de uso con Postman

### BASE URL: `http://localhost:8080`

---

### 1️⃣ AUTENTICACIÓN

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "identificationNumber": "ANA001",
  "password": "analyst123"
}
```
**Respuesta:** guarda el `token` de la respuesta y úsalo en el header:
```
Authorization: Bearer <token>
```

#### Registrar usuario
```
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "Carlos Pérez",
  "identificationNumber": "CLI003",
  "email": "carlos.perez@email.com",
  "phone": "3001234567",
  "birthDate": "1990-06-15",
  "address": "Calle 123 Bogotá",
  "role": "CLIENT_INDIVIDUAL",
  "password": "password123"
}
```

---

### 2️⃣ USUARIOS

```
GET  /api/users                           → Todos los usuarios (ANALYST)
GET  /api/users/{id}                      → Usuario por ID
GET  /api/users/by-identification/{id}    → Usuario por número de ID
PATCH /api/users/{id}/status              → Cambiar estado
  Body: { "status": "BLOCKED" }
```

---

### 3️⃣ CUENTAS BANCARIAS

#### Abrir cuenta (login como CLI001)
```
POST /api/accounts
{
  "ownerIdentificationNumber": "CLI001",
  "accountType": "SAVINGS",
  "currency": "USD"
}
```
AccountType: `SAVINGS | CHECKING | PERSONAL | BUSINESS`

#### Otras operaciones
```
GET  /api/accounts                        → Todas las cuentas (ANALYST/TELLER)
GET  /api/accounts/{accountNumber}        → Cuenta específica
GET  /api/accounts/owner/{idNumber}       → Cuentas por dueño

POST /api/accounts/{number}/deposit       → Depósito (TELLER/ANALYST)
  Body: { "amount": 500.00 }

POST /api/accounts/{number}/withdraw      → Retiro (TELLER/ANALYST)
  Body: { "amount": 200.00 }

PATCH /api/accounts/{number}/block        → Bloquear (ANALYST)
PATCH /api/accounts/{number}/unblock      → Desbloquear (ANALYST)
```

---

### 4️⃣ PRÉSTAMOS — Flujo completo

#### Paso 1: Solicitar préstamo (login CLI001)
```
POST /api/loans
{
  "loanType": "Personal",
  "requestedAmount": 10000.00,
  "currency": "USD",
  "termMonths": 24,
  "disbursementAccountNumber": "ACC0000000001"
}
```

#### Paso 2: Ver préstamos en revisión (login ANA001)
```
GET /api/loans/status/UNDER_REVIEW
```

#### Paso 3: Aprobar préstamo (login ANA001)
```
POST /api/loans/{id}/approve
{
  "approvedAmount": 9000.00,
  "currency": "USD",
  "interestRate": 8.5,
  "termMonths": 24
}
```

#### Paso 4: Desembolsar (login ANA001)
```
POST /api/loans/{id}/disburse
```
→ El saldo de `ACC0000000001` aumenta en $9,000

#### Rechazar préstamo (login ANA001)
```
POST /api/loans/{id}/reject
{
  "reason": "Insufficient credit history"
}
```

---

### 5️⃣ TRANSFERENCIAS — Flujo completo

#### Transferencia directa (login CLI001 — monto normal)
```
POST /api/transfers
{
  "sourceAccount": "ACC0000000001",
  "destinationAccount": "ACC0000000002",
  "amount": 500.00
}
→ Estado: EXECUTED inmediatamente
```

#### Transferencia empresarial de alto monto (login EMP001 — supera $5,000)
```
POST /api/transfers
{
  "sourceAccount": "ACC0000000003",
  "destinationAccount": "ACC0000000001",
  "amount": 10000.00
}
→ Estado: PENDING_APPROVAL
```

#### Ver transferencias pendientes (login SUP001)
```
GET /api/transfers/pending
```

#### Aprobar transferencia (login SUP001)
```
POST /api/transfers/{id}/approve
```

#### Rechazar transferencia (login SUP001)
```
POST /api/transfers/{id}/reject
{
  "reason": "Unauthorized transaction"
}
```

#### Forzar vencimiento (login ANA001)
```
POST /api/transfers/process-expired
```
→ Las transferencias con más de 60 minutos pasan a EXPIRED

---

### 6️⃣ BITÁCORA DE AUDITORÍA

```
GET /api/audit-log                        → Todos los registros (ANALYST)
GET /api/audit-log/product/{productId}    → Por producto (ej: ACC0000000001, 1)
GET /api/audit-log/user/{userId}          → Por usuario (ANALYST)
```

Cada entrada incluye un campo `detailData` con JSON específico de la operación (saldos antes/después, motivos, etc.)

---

## 📐 Principios DDD implementados

| Concepto DDD | Implementación |
|---|---|
| **Entity** | `User`, `AuditLog` — tienen identidad propia (ID) |
| **Aggregate Root** | `BankAccount`, `Loan`, `Transfer` — única entrada para modificar estado |
| **Value Object** | `Email`, `PhoneNumber`, `Money` — inmutables, sin identidad |
| **Domain Service** | `TransferDomainService`, `LoanDisbursementDomainService` — lógica que cruza agregados |
| **Repository (interface)** | `UserRepository`, `BankAccountRepository`, etc. — puertos de dominio |
| **Factory Method** | `User.create()`, `BankAccount.open()`, `Loan.request()`, `Transfer.createDirect/Pending` |

## 🔐 Seguridad
- Contraseñas hasheadas con BCrypt
- Autenticación JWT stateless
- Cada operación valida el rol del usuario autenticado
- Los clientes solo pueden ver/operar sus propios productos
