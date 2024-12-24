# CSEE 6863 Formal Verification Final Project

## Java HR System with JBMC Verification

This project provides a Java-based HR system (`HrDatabaseFacade`, `InmemConnection`, etc.) 
and a set of bounded model-checking tests (using JBMC) to verify certain safety and liveness properties. 
Maven is used to manage dependencies like Spring Boot and SLF4J.

The code we model checked on is a project from COMS 4156 Advanced Software Engineering. The repo to the 
original project can be found at: https://github.com/Alex-XJK/100-of-100-service-team

## Key Highlights

- **`HrDatabaseFacadePropertiesTest.java`**: Encodes various assert-based property checks.
- **JBMC (Java Bounded Model Checker)**: Analyzes compiled `.class` files to detect property violations across all possible execution paths (up to bounded loop unrolling and concurrency exploration).

---

## Step-by-Step Instructions

### 1. Project Structure

```
my-project/
├── pom.xml
├── README.md  <-- (this file)
├── src
│   └── main
│       └── java
│           └── dev
│               └── coms4156
│                   └── project
│                       ├── HrDatabaseFacade.java
│                       ├── InmemConnection.java
│                       ├── NotFoundException.java
│                       ├── Employee.java
│                       ├── Organization.java
│                       ├── ...
│                       ├── HrDatabaseFacadePropertiesTest.java  <-- test code for JBMC
│                       └── ...
└── ...
```

**Notes:**
- All code (including the "test" methods) is in `src/main/java`.
- Maven will compile everything into `target/classes/`.

---

### 2. Prerequisites

- **Maven**: Ensure Maven (version >= 3.0) is installed.
- **Java**: Install Java 17 JDK.
- **JBMC**: Install JBMC binary (e.g., version 6.4.1).
    - Download it from [JBMC GitHub Releases](https://github.com/diffblue/cbmc/releases) or compile from source.

---

### 3. Building the Project

1. Navigate to this project’s root (where `pom.xml` resides).
2. Run:

   ```bash
   mvn clean compile
   ```

3. Maven will:
- Download dependencies (Spring Boot, etc.).
- Compile all `.java` files from `src/main/java/` into `target/classes/`.

4. Confirm that the class files are generated, e.g.,

   ```bash
   target/classes/dev/coms4156/project/HrDatabaseFacadePropertiesTest.class
   ```

---

### 4. Running the JBMC Checks

JBMC takes a fully qualified class name (in dot-notation) and an optional `--function` to identify the exact static method to use as the “enter point” for analysis.

1. Navigate to the project root.
2. For each property method in `HrDatabaseFacadePropertiesTest`, run a JBMC command like:

   ```bash
   jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
     --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.METHOD_NAME \
     --classpath target/classes \
     --unwind 10 \
     --java-threading \
     --stack-trace
   ```

#### Command Options

- **`METHOD_NAME`**: Replace with the property-checking method name (e.g., `testDbConnectionNotNull`).
- **`--classpath target/classes`**: Tells JBMC where to find the compiled classes.
- **`--unwind 10`**: Sets the maximum loop unrolling depth.
- **`--java-threading`**: Explores possible concurrency interleavings.
- **`--stack-trace`**: Prints a counterexample stack trace if the property fails.

---

### Example Commands

#### Property 1: `dbConnection` must not be null

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testDbConnectionNotNull \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 2: Concurrency safety in `getInstance()`

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testConcurrentGetInstance \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 3: Removing an organization invalidates facade

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testRemoveOrganization \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 4: `updateEmployee` → Subsequent reads see updated data

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testEmployeeUpdateIsVisible \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 5: Invalid IDs do not crash

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testInvalidIDsDoNotCrash \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 6: Non-existent employee → Consistent behavior

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testNonExistentEmployee \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 7: Atomic concurrency updates to the same employee

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testConcurrentEmployeeUpdates \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 8: Department head validity

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testDepartmentHeadValidity \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 9: Organization name validity

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testOrganizationNameValidity \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 10: Employee salary non-negativity

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testEmployeeSalaryNonNegative \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 11: Employees never have invalid performance scores

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testEmployeePerformanceAssignment \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 12: Newly added employees are reflected in records

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testEmployeeAdditionReflects \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 13: Consistency of `getEmployee` across connections

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testGetEmployeeConsistency \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 14: Consistency of `getDepartments` across connections

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testGetDepartmentsConsistency \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 15: Departments do not share IDs within the same organization

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testDepartmentIdsUnique \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

#### Property 16: No NullPointerExceptions during normal execution

```bash
jbmc dev.coms4156.project.HrDatabaseFacadePropertiesTest \
  --function dev.coms4156.project.HrDatabaseFacadePropertiesTest.testNoNullPointerExceptions \
  --classpath target/classes \
  --unwind 10 \
  --java-threading \
  --stack-trace
```

---

### 5. Running SpotBugs Checks

After compilation, run SpotBugs using:

```bash
mvn spotbugs:spotbugs
```

This generates a `spotbugsXml.xml` in the `target` directory, containing the bugs SpotBugs found.

To run SpotBugs in GUI mode, use:

```bash
mvn spotbugs:gui
```

To set more SpotBugs checking rules, add them to `spotbugs_include.xml`.

---

### Notes

- Adjust the `--unwind` depth if necessary for your testing scenarios.
- Use `--stack-trace` for detailed error tracing in JBMC.

