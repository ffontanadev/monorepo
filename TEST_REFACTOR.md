# Java Test Suite Refactoring Guide

## Overview

This document describes the refactoring of the Java test infrastructure from inheritance-based to composition-based design.

**Refactoring Completed:** November 21, 2025

---

## What Changed

### Before: Inheritance-Based Design
```
DAOTest (abstract base class)
    └── DAOImplTest extends DAOTest

ServiceTest (abstract base class)
    └── FileServiceTest extends ServiceTest
```

### After: Composition-Based Design
```
TestDataFactory (static utility class)
    ├── Used by DAOImplTest
    ├── Used by FileServiceTest
    └── Used by ServicesTest
```

---

## Files Modified

| File | Action | Description |
|------|--------|-------------|
| `commons/TestDataFactory.java` | **CREATED** | Central factory with all shared test data builders |
| `services/FileServiceTest.java` | **REFACTORED** | No longer extends ServiceTest; uses TestDataFactory |
| `services/ServicesTest.java` | **CREATED** | Template for future service tests |
| `dao/DAOImplTest.java` | **CREATED** | Template for future DAO tests |
| `commons/DAOTest.java` | **DELETED** | Functionality moved to TestDataFactory |
| `commons/ServiceTest.java` | **DELETED** | Functionality moved to TestDataFactory |

---

## How to Run Tests

### Prerequisites
Your project needs a build tool configuration. Add one of:

**Option A: Maven (pom.xml)**
```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Option B: Gradle (build.gradle)**
```gradle
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.5.0'
}
```

### Running Tests

**Maven:**
```bash
mvn test
mvn test -Dtest=FileServiceTest
```

**Gradle:**
```bash
gradle test
gradle test --tests FileServiceTest
```

**IDE (IntelliJ/Eclipse):**
- Right-click on test class → Run 'FileServiceTest'
- Run all tests in package: right-click `services` → Run Tests

---

## Migration Guide for Future Tests

### Writing New Service Tests

**OLD WAY (deprecated):**
```java
class MyServiceTest extends ServiceTest {
    // Inherited mocks and methods
}
```

**NEW WAY:**
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock
    private DAO dao;

    @Mock
    private LogUtils logUtils;

    @InjectMocks
    private MyService myService;

    @Test
    void testMethod() {
        CustomerData data = TestDataFactory.mockCustomerData(true);
        // ... test logic
    }
}
```

### Writing New DAO Tests

**NEW WAY:**
```java
@ExtendWith(MockitoExtension.class)
class MyDAOTest {
    @Mock
    private ManagerDataAccessAs400 manager;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DAOImpl dao;

    @Test
    void testQuery() throws SQLException {
        TestDataFactory.setupMockResultSetForBusinessData(resultSet, true);
        // ... test logic
    }
}
```

---

## TestDataFactory API Reference

### Data Builders

| Method | Returns | Purpose |
|--------|---------|---------|
| `createNonBusinessIdDatatype()` | NonBusinessIdDatatype | Business & person identification |
| `createAccountIdDatatype()` | AccountIdDatatype | Account identification |
| `createCustomerIdDatatype()` | CustomerIdDatatype | Customer identification |
| `createMetaData()` | MetaData | Complete document metadata |
| `createFileData()` | FileData | Base64-encoded test file |
| `mockCustomerData(boolean valid)` | CustomerData | Customer with valid/invalid expiration |
| `mockDocumentData(String typeId)` | MetaData | Document with specific type |
| `specificMetadata(name, type, value)` | SpecificMetadata | Single metadata field |
| `getMapTemplateByCardProduct()` | MapTemplateByCardProduct | Template mappings |

### ResultSet Mock Helpers

| Method | Purpose |
|--------|---------|
| `setupMockResultSetForBusinessData(rs, valid)` | Mocks AS400 business query results |
| `setupMockResultSetForAddressData(rs)` | Mocks AS400 address query results |
| `setupMockResultSetForPersonData(rs, valid, spouse)` | Mocks AS400 person query results |

### Service Setup Helpers

| Method | Purpose |
|--------|---------|
| `setupContractGenerationServices(service, dao)` | Configures all contract generation sub-services |

### Constants

| Constant | Value |
|----------|-------|
| `DOCUMENT_DB_NAME` | "documents" |
| `FILENET_DB_NAME` | "filenet" |

---

## What to Watch Out For

### ⚠️ Critical Warnings

1. **Test Behavior Preservation**
   - FileServiceTest's two test methods (`testCreateFileWithoutMultipartFile_success` and `testCreateFileWithoutMultipartFile_NosuccesswithSpecificMetadta`) must behave **identically** to before
   - Any failures indicate the refactor broke something

2. **@BeforeEach Merge**
   - FileServiceTest now has a merged `@BeforeEach` combining:
     - ServiceTest's `beforeEach()` logic (spy initialization, contract service setup)
     - FileServiceTest's original `setup()` (ReflectionTestUtils configuration)
   - **Order matters!** The spy must be initialized before being injected into fileApiService

3. **Mock Lifecycle**
   - `@Mock` fields are now in concrete classes, not inherited
   - `@ExtendWith(MockitoExtension.class)` is required on every test class
   - Mockito initializes mocks before `@BeforeEach` runs

4. **Static Context**
   - All TestDataFactory methods are static
   - Call with `TestDataFactory.methodName()`, not `this.methodName()`

5. **Spy Reassignment**
   - `generateContractApiService` is created as a spy in `@BeforeEach`, overriding the `@Spy` annotation
   - This pattern was inherited from ServiceTest.beforeEach() and must be preserved

### 🔍 Common Migration Mistakes

| Mistake | Correct Approach |
|---------|-----------------|
| Forgetting `@ExtendWith(MockitoExtension.class)` | Add annotation to every test class |
| Calling `mockCustomerData()` directly | Call `TestDataFactory.mockCustomerData()` |
| Missing mock declarations | Copy all mocks from base class to concrete test |
| Wrong `@BeforeEach` order | Initialize spy → setup services → inject fields |
| Importing wrong TestDataFactory | Use `uy.com.bbva.services.documents.commons.TestDataFactory` |

### 📦 Compilation Requirements

All test classes require these dependencies:

**JUnit 5:**
- `org.junit.jupiter:junit-jupiter-api`
- `org.junit.jupiter:junit-jupiter-engine`

**Mockito:**
- `org.mockito:mockito-core`
- `org.mockito:mockito-junit-jupiter`

**Spring Test Utils:**
- `org.springframework:spring-test` (for ReflectionTestUtils)

**Domain Classes:**
- All `uy.com.bbva.*` packages must be on classpath

---

## Verification Checklist

After applying this refactor to your project:

- [ ] All Java files compile without errors
- [ ] No "cannot find symbol" errors for TestDataFactory
- [ ] FileServiceTest still has exactly 2 test methods
- [ ] Both test methods throw ServiceException as expected
- [ ] No abstract base test classes remain in `commons/`
- [ ] TestDataFactory contains all 14 helper methods
- [ ] ServicesTest and DAOImplTest have proper structure for future tests
- [ ] Git history shows clear commit message about refactoring

---

## Rollback Instructions

If you need to revert this refactor:

```bash
# Reset to commit before refactor
git log --oneline  # Find the commit hash before refactor
git revert <commit-hash>

# Or restore from branch
git checkout <branch-before-refactor> -- commons/ services/ dao/
```

---

## Benefits of This Refactor

✅ **Explicit Dependencies:** Each test declares exactly which mocks it uses
✅ **Composition over Inheritance:** Tests are easier to understand and modify
✅ **No Hidden State:** All test data creation is visible in TestDataFactory
✅ **Easier Testing:** Can unit test TestDataFactory methods if needed
✅ **IDE Support:** Better autocomplete and refactoring in IDEs
✅ **Reduced Coupling:** Tests don't depend on abstract base class lifecycle

---

## Architecture Diagram

```
┌─────────────────────────────────────┐
│      TestDataFactory.java           │
│  (Static utility class)             │
│                                     │
│  + createXXXDatatype()              │
│  + mockCustomerData()               │
│  + setupMockResultSetForXXX()       │
│  + setupContractGenerationServices()│
└─────────────┬───────────────────────┘
              │ uses (static calls)
              ├──────────────┬──────────────┐
              │              │              │
┌─────────────▼──┐ ┌─────────▼──┐ ┌────────▼────────┐
│ FileServiceTest│ │ServicesTest│ │  DAOImplTest    │
│ (refactored)   │ │(template)  │ │  (template)     │
└────────────────┘ └────────────┘ └─────────────────┘
```

---

## Questions & Support

**Q: Can I still use inheritance for test organization?**
A: Yes, but use concrete base classes specific to your domain, not generic ones.

**Q: What if I need test-specific setup that doesn't fit TestDataFactory?**
A: Add private helper methods directly in your test class.

**Q: Should TestDataFactory be final?**
A: It doesn't need to be; it has only static methods and can't be instantiated meaningfully.

**Q: Can I add new methods to TestDataFactory?**
A: Yes! Follow the pattern: public static methods, clear Javadoc, descriptive names.

---

**End of Refactoring Guide**
