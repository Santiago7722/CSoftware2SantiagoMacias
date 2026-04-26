# 🏦 Bank Management System
## Arquitectura Hexagonal (Ports & Adapters) + DDD + Spring Boot

---

## 📋 TABLA DE CONTENIDO

1. [Requisitos previos](#requisitos-previos)
2. [Estructura del proyecto](#estructura-del-proyecto)
3. [Ejecución en Visual Studio Code](#ejecución-en-visual-studio-code)
4. [Uso en Postman](#uso-en-postman)
5. [Uso en el Navegador (Swagger UI)](#uso-en-el-navegador-swagger-ui)
6. [Uso en XAMPP (H2 Console)](#uso-en-xampp-h2-console)
7. [Credenciales de prueba](#credenciales-de-prueba)
8. [Todos los endpoints](#todos-los-endpoints)
9. [Flujos de negocio paso a paso](#flujos-de-negocio-paso-a-paso)
10. [Reglas de negocio — qué se puede y no se puede hacer](#reglas-de-negocio)
11. [Permisos por rol](#permisos-por-rol)
12. [Arquitectura del proyecto](#arquitectura-del-proyecto)

---

## ✅ REQUISITOS PREVIOS

Antes de ejecutar el proyecto asegúrate de tener instalado:

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| **Java JDK** | 17 o superior | https://adoptium.net |
| **Maven** | 3.8 o superior | https://maven.apache.org/download.cgi |
| **Visual Studio Code** | Cualquier versión reciente | https://code.visualstudio.com |
| **Postman** | Cualquier versión reciente | https://www.postman.com/downloads |

### Extensiones requeridas en VS Code
Instala el paquete completo de Java:
- Abre VS Code → Extensions (Ctrl+Shift+X)
- Busca e instala: **"Extension Pack for Java"** (Microsoft)

### Verificar instalación
Abre una terminal y ejecuta:
```bash
java -version      # debe mostrar: openjdk 17.x.x o superior
mvn -version       # debe mostrar: Apache Maven 3.x.x
```

---

## 📁 ESTRUCTURA DEL PROYECTO

```
SantiagoMacias/
├── pom.xml                          ← Dependencias Maven (NO modificar)
├── src/
│   └── main/
│       ├── java/com/bank/
│       │   ├── BankApplication.java         ← Punto de entrada Spring Boot
│       │   ├── domain/                      ← Lógica pura de negocio
│       │   │   ├── model/
│       │   │   │   ├── aggregate/           ← BankAccount, Loan, Transfer
│       │   │   │   ├── entity/              ← User, AuditLog
│       │   │   │   └── valueobject/         ← Money, Email, Enums
│       │   │   ├── service/                 ← Servicios de dominio
│       │   │   └── exception/               ← Excepciones de dominio
│       │   ├── application/                 ← Casos de uso
│       │   │   ├── usecase/                 ← UserUseCase, LoanUseCase...
│       │   │   ├── port/input/              ← Interfaces de entrada
│       │   │   ├── port/output/             ← Interfaces de salida
│       │   │   └── dto/                     ← Objetos de transferencia
│       │   ├── adapter/
│       │   │   ├── in/web/controller/       ← Controladores REST
│       │   │   └── out/persistence/         ← Repositorios JPA
│       │   └── config/                      ← Seguridad, JWT, DataSeeder
│       └── resources/
│           └── application.properties       ← Configuración del servidor
```

---

## 💻 EJECUCIÓN EN VISUAL STUDIO CODE

### Paso 1 — Abrir el proyecto correctamente

1. Abre VS Code
2. Clic en **File → Open Folder**
3. Navega hasta la carpeta **`SantiagoMacias`** (la que contiene el `pom.xml`)
4. Clic en **"Select Folder"**

> ⚠️ **IMPORTANTE:** Debes abrir la carpeta `SantiagoMacias`, NO la carpeta `CSoftware2` ni `CSoftware2SantiagoMacias`. Si abres la carpeta incorrecta, Maven no encontrará el `pom.xml`.

### Paso 2 — Esperar que VS Code cargue el proyecto

- VS Code detectará automáticamente el proyecto Maven
- Verás en la barra inferior: **"Java: Importing..."**
- Espera hasta que desaparezca ese mensaje (puede tardar 1-2 minutos la primera vez)

### Paso 3 — Ejecutar el proyecto

**Opción A — Desde la terminal integrada (recomendado):**
1. Abre terminal: **Terminal → New Terminal** (o Ctrl+`)
2. Ejecuta:
```bash
mvn spring-boot:run
```

**Opción B — Desde el botón Run:**
1. Abre el archivo `src/main/java/com/bank/BankApplication.java`
2. Clic en el botón ▶ **Run** que aparece sobre el método `main`

### Paso 4 — Verificar que inició correctamente

Verás en la consola:
```
[SEED] TEST CREDENTIALS (POST /api/auth/login):
[SEED]   Analyst:  ID=ANA001  | Pass=analyst123
...
Started BankApplication in X.XXX seconds (JVM running for X.XXX)
```

### ✅ El servidor está listo en: `http://localhost:8080`

### ❌ Errores comunes en VS Code

| Error | Causa | Solución |
|---|---|---|
| `No POM in this directory` | Estás en la carpeta incorrecta | Entra a la carpeta `SantiagoMacias` primero |
| `Unrecognised tag: 'n'` | Tag `<n>` en pom.xml | Cambia `<n>` por `<name>` en la línea 18 del pom.xml |
| `Failed to delete target` | VS Code tiene archivos bloqueados | Cierra VS Code, borra la carpeta `target` manualmente y vuelve a ejecutar |
| `local variables referenced from lambda must be final` | Error en AccountUseCase línea 63 | Agrega `final String finalOwnerId = ownerIdToUse;` antes de la lambda |
| `Port 8080 already in use` | Ya hay un servidor corriendo | Mata el proceso: `netstat -ano \| findstr :8080` luego `taskkill /PID [número] /F` |

### ⛔ Lo que NO debes hacer en VS Code

- ❌ NO abras la carpeta `CSoftware2` raíz — Maven no funciona desde ahí
- ❌ NO borres ni modifiques el `pom.xml` sin saber qué estás cambiando
- ❌ NO tengas dos instancias del servidor corriendo al mismo tiempo
- ❌ NO modifiques los archivos de la carpeta `target/` — se regeneran solos
- ❌ NO cambies el puerto 8080 mientras Postman ya está configurado con ese puerto

---

## 📬 USO EN POSTMAN

### Configuración inicial

#### Paso 1 — Importar todos los endpoints automáticamente

1. Asegúrate de que el servidor está corriendo (`mvn spring-boot:run`)
2. Abre Postman
3. Clic en **Import** (esquina superior izquierda)
4. Selecciona la pestaña **Link**
5. Pega: `http://localhost:8080/api-docs`
6. Clic **Continue → Import**

Postman importará automáticamente todos los endpoints con sus parámetros.

#### Paso 2 — Hacer Login y obtener el token JWT

Crea una nueva request:
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```
Body:
```json
{
  "identificationNumber": "ANA001",
  "password": "analyst123"
}
```

Respuesta exitosa:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "fullName": "Sarah Johnson",
    "role": "INTERNAL_ANALYST"
  }
}
```

#### Paso 3 — Configurar el token automático

**Para no pegar el token en cada request:**

1. En Postman ve a **Collections** → tu colección importada
2. Clic en los **3 puntos (...)** → **Edit**
3. Pestaña **Variables**, agrega:

| Variable | Initial Value |
|---|---|
| `baseUrl` | `http://localhost:8080` |
| `token` | *(déjalo vacío)* |

4. Pestaña **Authorization**:
   - Type: **Bearer Token**
   - Token: `{{token}}`

5. En la request de **Login**, pestaña **Tests**, pega:
```javascript
const response = pm.response.json();
if (response.data && response.data.token) {
    pm.collectionVariables.set("token", response.data.token);
    console.log("✅ Token saved automatically!");
}
```

Ahora cada vez que hagas login, el token se guarda solo.

#### Paso 4 — Agregar el token manualmente (alternativa simple)

En cualquier request:
1. Pestaña **Authorization**
2. Type: **Bearer Token**
3. Pega el token copiado de la respuesta del login

### ✅ Flujos completos en Postman

#### Flujo 1 — Registrar y ver usuario
```
POST /api/auth/register     ← Crear usuario nuevo
GET  /api/users             ← Ver todos (solo ANALYST)
GET  /api/users/by-identification/CLI001  ← Buscar por ID
```

#### Flujo 2 — Gestión de cuentas
```
POST /api/accounts                           ← Abrir cuenta
GET  /api/accounts                           ← Ver todas
GET  /api/accounts/{accountNumber}           ← Ver una cuenta
GET  /api/accounts/owner/{idNumber}          ← Cuentas de un cliente
POST /api/accounts/{accountNumber}/deposit   ← Depositar
POST /api/accounts/{accountNumber}/withdraw  ← Retirar
PATCH /api/accounts/{accountNumber}/block    ← Bloquear
PATCH /api/accounts/{accountNumber}/unblock  ← Desbloquear
```

#### Flujo 3 — Ciclo completo de préstamo
```
POST /api/loans              ← 1. Solicitar (cliente o empleado)
GET  /api/loans/status/UNDER_REVIEW  ← 2. Ver pendientes (analista)
POST /api/loans/{id}/approve ← 3. Aprobar (analista)
POST /api/loans/{id}/disburse ← 4. Desembolsar (analista)
```
O alternativamente:
```
POST /api/loans/{id}/reject  ← Rechazar (analista)
```

#### Flujo 4 — Transferencias con aprobación
```
POST /api/transfers               ← 1. Crear transferencia
GET  /api/transfers/pending       ← 2. Ver pendientes (supervisor)
POST /api/transfers/{id}/approve  ← 3. Aprobar (supervisor)
```
O alternativamente:
```
POST /api/transfers/{id}/reject   ← Rechazar (supervisor)
POST /api/transfers/process-expired ← Vencer transferencias (analista)
```

#### Flujo 5 — Auditoría
```
GET /api/audit-log                        ← Todos los logs (analista)
GET /api/audit-log/product/{productId}    ← Por cuenta/préstamo/transferencia
GET /api/audit-log/user/{userId}          ← Por usuario (analista)
```

### ❌ Lo que NO debes hacer en Postman

- ❌ NO hagas requests sin el token — recibirás error 403 Forbidden
- ❌ NO uses el token de un rol que no tiene permiso para ese endpoint
- ❌ NO intentes transferir de una cuenta bloqueada
- ❌ NO intentes aprobar un préstamo que no está en estado UNDER_REVIEW
- ❌ NO intentes desembolsar sin haber aprobado primero
- ❌ NO cambies el `Content-Type` — siempre debe ser `application/json`
- ❌ NO dejes el token vencido (expira en 24 horas — vuelve a hacer login)

---

## 🌐 USO EN EL NAVEGADOR (SWAGGER UI)

Swagger UI es la alternativa visual a Postman — funciona directamente en el navegador sin instalar nada.

### Paso 1 — Abrir Swagger UI

Con el servidor corriendo, abre en el navegador:
```
http://localhost:8080/swagger-ui.html
```

### Paso 2 — Autenticarse en Swagger

1. Clic en el botón **Authorize 🔒** (esquina superior derecha)
2. Primero haz login desde el endpoint `POST /api/auth/login`:
   - Expande la sección **Authentication**
   - Clic en **POST /api/auth/login → Try it out**
   - Ingresa las credenciales en el body
   - Clic **Execute**
   - Copia el `token` de la respuesta
3. Vuelve al botón **Authorize 🔒**
4. En el campo **bearerAuth** pega el token (sin la palabra "Bearer")
5. Clic **Authorize → Close**

### Paso 3 — Probar endpoints

1. Expande cualquier sección (Accounts, Loans, Transfers, etc.)
2. Clic en el endpoint que quieres probar
3. Clic en **Try it out**
4. Llena los parámetros requeridos
5. Clic **Execute**
6. Ve la respuesta en la sección **Response body**

### Otras URLs del navegador

| URL | Descripción |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Interfaz visual de todos los endpoints |
| `http://localhost:8080/api-docs` | JSON de la especificación OpenAPI (para importar en Postman) |
| `http://localhost:8080/h2-console` | Consola de la base de datos H2 |

### ✅ Lo que puedes hacer en Swagger

- ✅ Probar todos los endpoints sin configurar nada
- ✅ Ver los parámetros requeridos para cada endpoint
- ✅ Ver los modelos de request y response
- ✅ Autenticarte y probar con diferentes roles

### ❌ Lo que NO debes hacer en Swagger

- ❌ NO cierres o recargues el navegador sin saber que perderás la sesión
- ❌ NO uses Swagger en producción con datos reales (solo para pruebas)
- ❌ NO olvides hacer clic en **Authorize** antes de probar endpoints protegidos

---

## 🗄️ USO EN XAMPP (H2 Console)

> ⚠️ **Nota importante:** Este proyecto usa **H2** (base de datos en memoria), NO MySQL de XAMPP. No necesitas tener XAMPP corriendo para usar el proyecto. La H2 Console reemplaza a phpMyAdmin para ver la base de datos.

### ¿Qué es la H2 Console?

Es una interfaz web similar a phpMyAdmin de XAMPP, pero para la base de datos H2 que viene incluida con Spring Boot. Puedes ver tablas, ejecutar SQL y consultar datos directamente.

### Paso 1 — Acceder a H2 Console

Con el servidor corriendo, abre en el navegador:
```
http://localhost:8080/h2-console
```

### Paso 2 — Configurar la conexión

Rellena los campos exactamente así:

| Campo | Valor |
|---|---|
| **Driver Class** | `org.h2.Driver` |
| **JDBC URL** | `jdbc:h2:mem:bankdb` |
| **User Name** | `sa` |
| **Password** | *(dejar vacío)* |

Clic en **Connect**

### Paso 3 — Ver las tablas

En el panel izquierdo verás las tablas:
- `USERS` — Todos los usuarios del sistema
- `BANK_ACCOUNTS` — Cuentas bancarias
- `LOANS` — Préstamos
- `TRANSFERS` — Transferencias
- `AUDIT_LOG` — Bitácora de operaciones

### Paso 4 — Consultas SQL de ejemplo

```sql
-- Ver todos los usuarios
SELECT * FROM USERS;

-- Ver cuentas con sus saldos
SELECT ACCOUNT_NUMBER, ACCOUNT_TYPE, OWNER_ID, BALANCE, STATUS
FROM BANK_ACCOUNTS;

-- Ver préstamos pendientes
SELECT * FROM LOANS WHERE STATUS = 'UNDER_REVIEW';

-- Ver transferencias pendientes de aprobación
SELECT * FROM TRANSFERS WHERE STATUS = 'PENDING_APPROVAL';

-- Ver bitácora completa
SELECT * FROM AUDIT_LOG ORDER BY OPERATION_DATETIME DESC;

-- Ver bitácora de una cuenta específica
SELECT * FROM AUDIT_LOG WHERE AFFECTED_PRODUCT_ID = 'ACC0000000001';
```

### ⚠️ Comportamiento importante de la base de datos

> La base de datos H2 es **en memoria** (`mem:bankdb`). Esto significa:
> - ✅ Se crea automáticamente al iniciar el servidor
> - ✅ Se puebla con datos de prueba automáticamente
> - ❌ **Se borra completamente al detener el servidor**
> - ❌ Cada vez que reinicias el servidor, la base de datos vuelve a su estado inicial

### ✅ Lo que puedes hacer en H2 Console

- ✅ Ver el estado actual de todas las tablas
- ✅ Verificar que las operaciones se guardaron correctamente
- ✅ Ejecutar consultas SQL para auditoría y debugging
- ✅ Ver el contenido de la bitácora de auditoría

### ❌ Lo que NO debes hacer en H2 Console

- ❌ NO modifiques datos directamente con `UPDATE` o `DELETE` — puede corromper el estado del negocio
- ❌ NO insertes usuarios directamente con `INSERT` — usa el endpoint `/api/auth/register`
- ❌ NO modifiques saldos directamente — usa los endpoints de depósito/retiro
- ❌ NO uses XAMPP MySQL con este proyecto — usa H2 Console
- ❌ NO esperes que los datos persistan al reiniciar el servidor

---

## 🔑 CREDENCIALES DE PRUEBA

Estas credenciales se crean automáticamente al iniciar el servidor:

| Rol | ID | Contraseña | Cuenta preloaded | Saldo |
|---|---|---|---|---|
| **Internal Analyst** | `ANA001` | `analyst123` | — | — |
| **Teller** | `TEL001` | `teller123` | — | — |
| **Commercial Employee** | `COM001` | `commercial123` | — | — |
| **Client Individual 1** | `CLI001` | `client123` | `ACC0000000001` | $15,000 |
| **Client Individual 2** | `CLI002` | `client456` | `ACC0000000002` | $8,500 |
| **Company Admin** | `CORP001` | `company123` | `ACC0000000003` | $250,000 |
| **Company Supervisor** | `SUP001` | `supervisor123` | — | — |
| **Company Employee** | `EMP001` | `employee123` | — | — |

---

## 📡 TODOS LOS ENDPOINTS

### Base URL: `http://localhost:8080`

#### 🔐 Autenticación (pública — no requiere token)
| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Login — obtiene token JWT |
| POST | `/api/auth/register` | Registrar nuevo usuario |

#### 👤 Usuarios (requiere token)
| Método | Endpoint | Roles permitidos |
|---|---|---|
| GET | `/api/users` | INTERNAL_ANALYST |
| GET | `/api/users/{id}` | Todos (solo propios para clientes) |
| GET | `/api/users/by-identification/{id}` | Todos |
| PATCH | `/api/users/{id}/status` | INTERNAL_ANALYST |

#### 🏦 Cuentas (requiere token)
| Método | Endpoint | Roles permitidos |
|---|---|---|
| POST | `/api/accounts` | TELLER, COMMERCIAL, ANALYST, CLIENTS |
| GET | `/api/accounts` | ANALYST, TELLER, COMMERCIAL |
| GET | `/api/accounts/{accountNumber}` | Todos (solo propias para clientes) |
| GET | `/api/accounts/owner/{idNumber}` | Todos (solo propias para clientes) |
| POST | `/api/accounts/{number}/deposit` | TELLER, ANALYST |
| POST | `/api/accounts/{number}/withdraw` | TELLER, ANALYST |
| PATCH | `/api/accounts/{number}/block` | ANALYST |
| PATCH | `/api/accounts/{number}/unblock` | ANALYST |

#### 💳 Préstamos (requiere token)
| Método | Endpoint | Roles permitidos |
|---|---|---|
| POST | `/api/loans` | Clientes, COMMERCIAL, ANALYST |
| GET | `/api/loans` | ANALYST, COMMERCIAL |
| GET | `/api/loans/status/{status}` | ANALYST, COMMERCIAL |
| GET | `/api/loans/{id}` | Todos (solo propios para clientes) |
| GET | `/api/loans/client/{idNumber}` | Todos (solo propios para clientes) |
| POST | `/api/loans/{id}/approve` | ANALYST |
| POST | `/api/loans/{id}/reject` | ANALYST |
| POST | `/api/loans/{id}/disburse` | ANALYST |

#### 💸 Transferencias (requiere token)
| Método | Endpoint | Roles permitidos |
|---|---|---|
| POST | `/api/transfers` | Clientes, COMPANY_EMPLOYEE, ANALYST |
| GET | `/api/transfers` | ANALYST |
| GET | `/api/transfers/pending` | SUPERVISOR, COMPANY_ADMIN, ANALYST |
| GET | `/api/transfers/{id}` | Todos |
| GET | `/api/transfers/account/{number}` | Todos (solo propias para clientes) |
| POST | `/api/transfers/{id}/approve` | SUPERVISOR, COMPANY_ADMIN, ANALYST |
| POST | `/api/transfers/{id}/reject` | SUPERVISOR, COMPANY_ADMIN, ANALYST |
| POST | `/api/transfers/process-expired` | ANALYST |

#### 📋 Bitácora (requiere token)
| Método | Endpoint | Roles permitidos |
|---|---|---|
| GET | `/api/audit-log` | ANALYST |
| GET | `/api/audit-log/product/{productId}` | Todos |
| GET | `/api/audit-log/user/{userId}` | ANALYST |

---

## 🔄 FLUJOS DE NEGOCIO PASO A PASO

### Flujo 1 — Solicitar y aprobar un préstamo

```
1. Login como CLI001/client123
2. POST /api/loans
   Body: { "loanType": "Personal", "requestedAmount": 5000, "currency": "USD",
           "termMonths": 12, "disbursementAccountNumber": "ACC0000000001" }
   → Estado: UNDER_REVIEW

3. Login como ANA001/analyst123
4. GET /api/loans/status/UNDER_REVIEW   ← Ver todos los pendientes
5. POST /api/loans/1/approve
   Body: { "approvedAmount": 4500, "currency": "USD",
           "interestRate": 8.5, "termMonths": 12 }
   → Estado: APPROVED

6. POST /api/loans/1/disburse           ← Sin body
   → Estado: DISBURSED
   → Saldo de ACC0000000001 aumenta en $4,500
```

### Flujo 2 — Transferencia empresarial con aprobación

```
1. Login como EMP001/employee123
2. POST /api/transfers
   Body: { "sourceAccount": "ACC0000000003",
           "destinationAccount": "ACC0000000001", "amount": 10000 }
   → Estado: PENDING_APPROVAL (supera el umbral de $5,000)

3. Login como SUP001/supervisor123
4. GET /api/transfers/pending           ← Ver transferencias pendientes
5. POST /api/transfers/1/approve        ← Sin body
   → Estado: EXECUTED
   → Saldo de ACC0000000003 disminuye en $10,000
   → Saldo de ACC0000000001 aumenta en $10,000
```

### Flujo 3 — Transferencia directa entre clientes

```
1. Login como CLI001/client123
2. POST /api/transfers
   Body: { "sourceAccount": "ACC0000000001",
           "destinationAccount": "ACC0000000002", "amount": 500 }
   → Estado: EXECUTED inmediatamente (monto < $5,000 y no es empresa)
```

### Flujo 4 — Vencimiento automático de transferencia

```
Las transferencias en PENDING_APPROVAL que no se aprueben
en 60 minutos cambian automáticamente a EXPIRED.

El scheduler revisa cada 5 minutos.
También puedes forzarlo manualmente:

1. Login como ANA001/analyst123
2. POST /api/transfers/process-expired
   → Todas las transferencias vencidas pasan a estado EXPIRED
   → Se registra en la bitácora con motivo "Expired due to lack of approval"
```

---

## 📏 REGLAS DE NEGOCIO

### ✅ Lo que SÍ se puede hacer

| Operación | Condición |
|---|---|
| Registrar un cliente | Edad mínima 18 años para CLIENT_INDIVIDUAL |
| Abrir cuenta | Cliente debe estar en estado ACTIVE |
| Solicitar préstamo | Cliente activo con cuenta destino válida |
| Aprobar préstamo | Solo si está en estado UNDER_REVIEW |
| Desembolsar préstamo | Solo si está en estado APPROVED |
| Transferencia directa | Cuenta origen activa y saldo suficiente |
| Transferencia con aprobación | Empleados de empresa con monto > $5,000 |
| Aprobar transferencia | Solo PENDING_APPROVAL y dentro de 60 minutos |

### ❌ Lo que NO se puede hacer (el sistema lo bloquea)

| Operación bloqueada | Motivo |
|---|---|
| Transferir de cuenta BLOQUEADA | La cuenta no está operativa |
| Transferir sin saldo suficiente | Fondos insuficientes |
| Transferir hacia uno mismo | Origen y destino no pueden ser iguales |
| Aprobar un préstamo ya aprobado | Solo se aprueba desde UNDER_REVIEW |
| Desembolsar sin aprobar | El préstamo debe estar en APPROVED |
| Aprobar transferencia vencida | Pasaron más de 60 minutos |
| Ver cuentas de otros clientes | Un cliente solo ve las suyas |
| Aprobar transferencia de otra empresa | El supervisor solo aprueba las de su empresa |
| Crear usuario con ID duplicado | El número de identificación es único |
| Registrar email sin @ | Formato inválido de email |
| Registrar teléfono fuera de 7-15 dígitos | Longitud inválida |

---

## 👥 PERMISOS POR ROL

| Operación | Analyst | Teller | Commercial | Client Ind. | Client Comp. | Co. Employee | Supervisor |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Ver todos los usuarios | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Ver su propio perfil | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Abrir cuenta | ✅ | ✅ | ✅ | ✅ (propia) | ✅ (propia) | ❌ | ❌ |
| Depositar/Retirar | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Bloquear cuenta | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Solicitar préstamo | ✅ | ❌ | ✅ | ✅ (propio) | ✅ (propio) | ❌ | ❌ |
| Aprobar préstamo | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Rechazar préstamo | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Desembolsar préstamo | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Crear transferencia | ✅ | ❌ | ❌ | ✅ (propia) | ✅ (propia) | ✅ (empresa) | ❌ |
| Aprobar transferencia | ✅ | ❌ | ❌ | ❌ | ✅ (empresa) | ❌ | ✅ (empresa) |
| Ver bitácora completa | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Ver bitácora propia | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## 🏗️ ARQUITECTURA DEL PROYECTO

Este proyecto implementa **Arquitectura Hexagonal (Ports and Adapters)** con principios **DDD (Domain-Driven Design)**:

```
┌─────────────────────────────────────────────────────┐
│  INTERFACE LAYER  (adapter/in/web)                   │
│  REST Controllers ← reciben requests HTTP            │
└──────────────────────┬──────────────────────────────┘
                       │ llama a Input Ports
┌──────────────────────▼──────────────────────────────┐
│  APPLICATION LAYER  (application/)                   │
│  Use Cases: orquestan operaciones                    │
│  Input Ports: interfaces de entrada                  │
│  Output Ports: interfaces de salida                  │
└──────────────────────┬──────────────────────────────┘
                       │ usa el dominio
┌──────────────────────▼──────────────────────────────┐
│  DOMAIN LAYER  (domain/)  ← SIN Spring, SIN JPA     │
│  Entities: User, AuditLog                            │
│  Aggregates: BankAccount, Loan, Transfer             │
│  Value Objects: Money, Email, PhoneNumber            │
│  Domain Services: TransferDomainService              │
└──────────────────────┬──────────────────────────────┘
                       │ implementado por
┌──────────────────────▼──────────────────────────────┐
│  INFRASTRUCTURE LAYER  (adapter/out/persistence)     │
│  JPA Entities + Spring Data Repositories             │
│  Mappers: Domain ↔ JPA Entity                        │
└─────────────────────────────────────────────────────┘
```

### Conceptos DDD aplicados

| Concepto | Clase en el proyecto |
|---|---|
| **Entity** | `User`, `AuditLog` — tienen identidad (ID) |
| **Aggregate Root** | `BankAccount`, `Loan`, `Transfer` — entrada única para modificar estado |
| **Value Object** | `Email`, `PhoneNumber`, `Money` — inmutables, sin identidad |
| **Domain Service** | `TransferDomainService`, `LoanDisbursementDomainService` |
| **Input Port** | `UserInputPort`, `AccountInputPort`, etc. |
| **Output Port** | `UserRepositoryPort`, `BankAccountRepositoryPort`, etc. |
| **Driving Adapter** | `AuthController`, `AccountController`, etc. |
| **Driven Adapter** | `UserPersistenceAdapter`, `BankAccountPersistenceAdapter`, etc. |

---

## ⚙️ CONFIGURACIÓN (application.properties)

```properties
server.port=8080                    # Puerto del servidor
spring.h2.console.enabled=true      # Activa la consola H2
app.jwt.expiration-ms=86400000      # Token expira en 24 horas
app.transfer.approval-threshold=5000.00  # Umbral de aprobación: $5,000
```

---

## 🚀 INICIO RÁPIDO (resumen en 3 pasos)

```bash
# 1. Entrar a la carpeta correcta
cd C:\Users\foxli\OneDrive\Escritorio\CSoftware2\CSoftware2SantiagoMacias\SantiagoMacias

# 2. Iniciar el servidor
mvn spring-boot:run

# 3. Abrir en el navegador
# http://localhost:8080/swagger-ui.html  ← Probar endpoints
# http://localhost:8080/h2-console       ← Ver base de datos
```

En Postman: `POST http://localhost:8080/api/auth/login` con `ANA001` / `analyst123`
