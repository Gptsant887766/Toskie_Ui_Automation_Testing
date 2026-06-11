# Toskie UI Automation Framework

A robust end-to-end test automation framework for the Toskie web application, built with **Java 21**, **Playwright**, and **TestNG**, following the Page Object Model design pattern.

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21 | Core language |
| Maven | 3.x | Build & dependency management |
| Playwright | 1.53.0 | Browser automation |
| TestNG | 7.9.0 | Test execution & reporting |
| ExtentReports | 5.0.9 | HTML test reports |
| iText PDF | 7.2.5 | PDF report generation |
| JFreeChart | 1.5.3 | Chart-based reporting |
| Lombok | 1.18.38 | Boilerplate reduction |
| SLF4J | 2.0.13 | Logging |

---

## Project Structure

```
src/
â”œâ”€â”€ main/java/com/toskie/
â”‚   â”œâ”€â”€ pages/                  # Page Object classes
â”‚   â”‚   â”œâ”€â”€ WelcomePage.java
â”‚   â”‚   â”œâ”€â”€ LoginPage.java
â”‚   â”‚   â”œâ”€â”€ HomePage.java
â”‚   â”‚   â”œâ”€â”€ ProfileCreationPage.java
â”‚   â”‚   â”œâ”€â”€ BookingPage.java
â”‚   â”‚   â”œâ”€â”€ ChatPage.java
â”‚   â”‚   â””â”€â”€ SearchPage.java
â”‚   â”œâ”€â”€ utils_Layer/            # Core framework utilities
â”‚   â”‚   â”œâ”€â”€ BrowserManager.java
â”‚   â”‚   â”œâ”€â”€ ConfigManager.java
â”‚   â”‚   â”œâ”€â”€ ReportManager.java
â”‚   â”‚   â”œâ”€â”€ WaitManager.java
â”‚   â”‚   â”œâ”€â”€ ApiUtils.java
â”‚   â”‚   â”œâ”€â”€ RetryConfig.java
â”‚   â”‚   â””â”€â”€ UtilLayer.java
â”‚   â”œâ”€â”€ utils/                  # Specialized validators & helpers
â”‚   â”‚   â”œâ”€â”€ AccessibilityUtils.java
â”‚   â”‚   â”œâ”€â”€ AssertionHelper.java
â”‚   â”‚   â”œâ”€â”€ DatabaseValidator.java
â”‚   â”‚   â”œâ”€â”€ NetworkValidator.java
â”‚   â”‚   â”œâ”€â”€ PerformanceUtils.java
â”‚   â”‚   â”œâ”€â”€ RedisValidator.java
â”‚   â”‚   â”œâ”€â”€ SecurityUtils.java
â”‚   â”‚   â”œâ”€â”€ TestDataManager.java
â”‚   â”‚   â””â”€â”€ WebSocketValidator.java
â”‚   â”œâ”€â”€ AuthenticationPages/    # Auth flow page objects
â”‚   â”œâ”€â”€ locators/               # Element locator constants
â”‚   â””â”€â”€ BaseTest_Layer/         # Base test configuration
â”‚
â””â”€â”€ test/java/com/toskie/
    â””â”€â”€ tests/                  # Test classes
```

---

## Prerequisites

- Java 21+
- Maven 3.8+
- Chromium / Firefox / WebKit (installed via Playwright)

---

## Setup

**1. Clone the repository**
```bash
git clone https://github.com/Gptsant887766/Toskie_Ui_Automation_Testing.git
cd Toskie_Ui_Automation_Testing
```

**2. Install dependencies**
```bash
mvn clean install -DskipTests
```

**3. Install Playwright browsers**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

**4. Configure environment**

Edit `config.properties` with your environment settings:
```properties
base.url=https://your-toskie-app-url
browser=chromium
headless=false
```

---

## Running Tests

### Run all tests
```bash
mvn test
```

### Run a specific suite
```bash
mvn test -DsuiteXmlFile=testng-smoke.xml
mvn test -DsuiteXmlFile=testng-regression.xml
mvn test -DsuiteXmlFile=testng-e2e.xml
```

### Available Test Suites

| Suite File | Description |
|------------|-------------|
| `testng-master.xml` | Full test suite (default) |
| `testng-smoke.xml` | Smoke tests |
| `testng-regression.xml` | Regression tests |
| `testng-e2e.xml` | End-to-end flows |
| `testng-login.xml` | Login tests |
| `testng-auth.xml` | Authentication tests |
| `testng-api.xml` | API validation tests |
| `testng-security.xml` | Security tests |
| `testng-accessibility.xml` | Accessibility tests |
| `testng-performance.xml` | Performance tests |
| `testng-negative.xml` | Negative / edge case tests |
| `testng-createprofile.xml` | Profile creation tests |

---

## Reports

After execution, reports are generated in:

```
test-output/
â”œâ”€â”€ ExtentReport.html     # Interactive HTML report
â”œâ”€â”€ report.pdf            # PDF summary report
â””â”€â”€ charts/               # Chart-based visual reports
```

Open `ExtentReport.html` in a browser to view detailed test results with screenshots, logs, and pass/fail metrics.

---

## Branching Strategy

```
main                        # Production â€” protected, linear history required
â”‚
develop                     # Integration branch â€” default branch
â”‚
â”œâ”€â”€ feature/login-automation
â”œâ”€â”€ feature/profile-automation
â”œâ”€â”€ feature/api-automation
â”œâ”€â”€ feature/websocket-automation
â”‚
â”œâ”€â”€ release/v1.0
â”‚
â””â”€â”€ hotfix/critical-fix
```

All branches are protected â€” PRs require 1 approver, stale reviews are dismissed, force pushes and deletions are blocked.

---

## Contributing

1. Branch off `develop` for features: `git checkout -b feature/your-feature develop`
2. Commit your changes with a clear message
3. Push and open a Pull Request targeting `develop`
4. Ensure all tests pass before requesting review
