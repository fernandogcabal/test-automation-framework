
# 🚀 Multi-Layer Test Automation Framework

Un framework de automatización de pruebas de nivel empresarial, robusto y escalable construido con **Java 21** y **Gradle**. Este proyecto implementa una arquitectura híbrida diseñada para validar la interfaz de usuario (UI) y APIs REST de manera aislada, rápida y confiable.

---

## 🏗️ Arquitectura e Infraestructura

El framework sigue los principios de diseño limpio y patrones de arquitectura estándar en la industria:

* **Page Object Model (POM):** Desacopla la lógica de interacción con la UI de las aserciones de prueba mediante clases dedicadas por página/componente.
* **ThreadLocal Driver Lifecycle & Grid Ready:** Garantiza el aislamiento completo del `WebDriver` por hilo en ejecuciones paralelas. Incluye un `DriverFactory` dinámico capaz de alternar entre ejecución local y `RemoteWebDriver` para contenedores **Docker** o **Selenium Grid**.
* **API Client & Java 21 Records:** Integración con **RestAssured** utilizando Java 21 `records` como DTOs inmutables para request/response payloads, junto con validación de esquemas JSON y gestión centralizada de headers.
* **Mecanismo de Resiliencia (Retry Analyzer):** Implementación de `IRetryAnalyzer` de TestNG para reejecutar de forma controlada pruebas intermitentes (*flaky tests*) causadas por latencia o fluctuaciones de red.
* **Gestión & Limpieza de Datos:** Estrategia de generación de datos aleatorios e independientes utilizando **DataFaker**, complementada con hooks de teardown vía API para evitar contaminación de base de datos en entornos concurrentes.
* **Estrategia de Esperas Explícitas:** Cero esperas implícitas o bloqueos estáticos (`Thread.sleep`), utilizando únicamente `WebDriverWait` guiado por condiciones del DOM.
* **Gestión de Entornos:** Centralización de configuraciones (URLs, timeouts, credenciales) parametrizadas y cargadas dinámicamente según el entorno objetivo (`DEV`, `STAGING`, `PROD`).
* **Observabilidad y Reportes:** Integración de listeners con **Allure Framework** para adjuntar evidencia dinámica (capturas de pantalla en fallos, logs de red y metadatos) junto con **SLF4J** para trazabilidad detallada.

---

## 🛠️ Tech Stack & Herramientas

| Componente | Tecnología | Propósito |
| --- | --- | --- |
| **Language** | Java 21 | Lenguaje base (soporte para Records, Pattern Matching) |
| **Build Tool** | Gradle | Gestión de dependencias, automatización y tareas de ejecución |
| **Test Runner** | TestNG | Orquestador de pruebas, paralelismo, grupos (`smoke`, `api`, `regression`) y retries |
| **UI Automation** | Selenium WebDriver | Manejo y automatización de navegadores (local / remote) |
| **API Automation** | RestAssured | Pruebas de integración REST y validación de contratos JSON |
| **Reporting** | Allure Framework | Visualización ejecutiva de resultados, evidencias y adjuntos |
| **Data Generation** | DataFaker | Generación de datos de prueba dinámicos e independientes |
| **CI/CD** | GitHub Actions | Pipeline de ejecución automatizada en Pull Requests |

---

## 📁 Estructura del Proyecto

```text
api-sistema-biblioteca-automation/
├── .github/
│   └── workflows/                # Pipeline de CI/CD para GitHub Actions
├── src/
│   ├── main/java/com/library/automation/
│   │   ├── api/                  # Clientes RestAssured, Endpoints y DTOs (Java 21 Records)
│   │   ├── config/               # Lectores de variables de entorno y propiedades
│   │   ├── drivers/              # Factory de WebDriver (Local/Remote) y ThreadLocal
│   │   ├── pages/                # Clases Page Object (UI)
│   │   └── utils/                # DataFaker, listeners de TestNG, RetryAnalyzer
│   └── test/java/com/library/automation/
│       ├── api/                  # Cobertura de pruebas de API
│       ├── ui/                   # Cobertura de pruebas de Interfaz de Usuario
│       └── e2e/                  # Flujos híbridos E2E (API Setup + UI Assertion)
├── src/test/resources/
│   ├── schemas/                  # Contratos .json para validación de JSON Schema
│   ├── config.properties        # Propiedades y parámetros base del entorno
│   └── testng.xml                # Suite XML para orquestación de ejecuciones
├── build.gradle                  # Configuración de dependencias y plugins de Gradle
└── README.md

```

---

## ⚡ Guía de Inicio Rápido

### Prerrequisitos

* **JDK 21** instalado y configurado en las variables de entorno (`JAVA_HOME`).
* **Google Chrome** (o navegador objetivo).
* **Allure Commandline** (para visualización local de reportes).

### Configuración e Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/fernandogcabal/api-sistema-biblioteca-automation.git
cd api-sistema-biblioteca-automation

```


2. Instala dependencias y verifica la compilación:
```bash
./gradlew compileTestJava

```



---

## 🧪 Ejecución de Pruebas

El framework permite filtrado y ejecución mediante tareas de Gradle o grupos de TestNG:

```bash
# Ejecutar la suite completa
./gradlew test

# Ejecutar únicamente la suite de humo (Smoke)
./gradlew test -Dgroups=smoke

# Ejecutar únicamente las pruebas de API
./gradlew test -Dgroups=api

# Ejecutar sobre un entorno específico
./gradlew test -Denv=staging

```

---

## 📊 Generación de Reportes

Para generar y abrir el reporte gráfico interactivo de **Allure** localmente:

```bash
./gradlew allureServe

```