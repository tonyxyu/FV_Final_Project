package dev.coms4156.project;

import dev.coms4156.project.exception.NotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Test properties (safety, liveness, etc.) for HrDatabaseFacade and underlying data connections.
 */
public class HrDatabaseFacadePropertiesTest {

  /**
   * 1) Liveness Property: If the database connection is null before calling getInstance(),
   * then an exception will eventually be thrown.
   *
   * Formally: "(dbConnection == null) at the time of getInstance() => EVENTUALLY exception"
   */
  public static void testDbConnectionNotNull() {
    HrDatabaseFacade.setConnection(null);

    boolean thrown = false;
    try {
      HrDatabaseFacade.getInstance(1);
    } catch (IllegalStateException e) {
      thrown = true;
    }
    assert thrown : "Expected IllegalStateException when dbConnection is null!";
  }

  /**
   * 2) Safety Property: Whenever two threads call getInstance(orgId) concurrently,
   * they must always receive the same object for that orgId.
   *
   * Formally: "ALWAYS (concurrent getInstance(orgId) => same facade)."
   */
  public static void testConcurrentGetInstance() throws InterruptedException {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    final int orgId = 1;
    final HrDatabaseFacade[] results = new HrDatabaseFacade[2];

    Thread t1 = new Thread(() -> {
      results[0] = HrDatabaseFacade.getInstance(orgId);
    });
    Thread t2 = new Thread(() -> {
      results[1] = HrDatabaseFacade.getInstance(orgId);
    });
    t1.start();
    t2.start();
    t1.join();
    t2.join();

    assert results[0] == results[1] :
        "Concurrency check failed: got two different facades for the same org!";
  }

  /**
   * 3) Liveness Property: Once an organization is removed, subsequent calls to
   * getInstance(removedOrgId) must eventually fail with a NotFoundException.
   *
   * Formally: "EVENTUALLY (removeOrganization(orgId) => getInstance(orgId) throws NotFoundException)."
   */
  public static void testRemoveOrganization() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    Organization newOrg = new Organization(999, "TestOrg");
    conn.getTestOrganizations().put(999, newOrg);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(999);
    assert facade != null : "Should be able to create facade for org #999";

    boolean removed = HrDatabaseFacade.removeOrganization(999);
    assert removed : "Expected removeOrganization(...) to succeed for org #999";

    boolean notFound = false;
    try {
      HrDatabaseFacade.getInstance(999);
      assert false : "Should not retrieve a facade after org #999 is removed!";
    } catch (NotFoundException e) {
      notFound = true;
    }
    assert notFound : "Expected NotFoundException for a removed organization!";
  }

  /**
   * 4) Liveness Property (bounded): After calling updateEmployee(emp),
   * subsequent calls to getEmployee(emp.id) will eventually reflect the updated data.
   *
   * Formally: "EVENTUALLY (updateEmployee(emp) => getEmployee(emp.id) has new data)."
   */
  public static void testEmployeeUpdateIsVisible() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    Employee emp = facade.getEmployee(1);
    assert emp != null : "Employee #1 must exist in test data";

    double oldSalary = emp.getSalary();
    double newSalary = oldSalary + 1000.0;
    emp.setSalary(newSalary);

    boolean success = facade.updateEmployee(emp);
    assert success : "Expected updateEmployee(...) to succeed for Employee #1";

    Employee updatedEmp = facade.getEmployee(1);
    assert updatedEmp != null : "Employee #1 must remain after update";
    assert updatedEmp.getSalary() == newSalary :
        "Updated salary must be seen in subsequent getEmployee(...)!";
  }

  /**
   * 5) Safety Property: The system must never crash or enter an erroneous state if
   * invalid IDs (e.g., negative, zero) are used. Instead, it should return null
   * or a safe result.
   *
   * Formally: "ALWAYS (invalid ID => safe result, no uncaught exception)."
   */
  public static void testInvalidIDsDoNotCrash() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    // Query negative/zero employee ID
    Employee e = facade.getEmployee(-1);
    assert e == null : "getEmployee(-1) should return null, not crash or non-null!";

    // Query negative/zero department ID
    Department d = facade.getDepartment(-100);
    assert d == null : "getDepartment(-100) should return null!";
  }

  /**
   * 6) Safety Property: If two threads update the same
   * employee concurrently with different salaries, the final salary must be
   * either the first or the second update (no corruption).
   *
   * Formally: "ALWAYS (concurrent update => final state is last-writer-wins)."
   */
  public static void testConcurrentEmployeeUpdates() throws InterruptedException {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    Employee original = facade.getEmployee(1);
    assert original != null : "Employee #1 must exist to test concurrency";

    double originalSalary = original.getSalary();

    Employee emp1 = new Employee(
        1, original.getName(), original.getHireDate(),
        original.getPosition(), originalSalary + 500, original.getPerformance()
    );
    Employee emp2 = new Employee(
        1, original.getName(), original.getHireDate(),
        original.getPosition(), originalSalary + 1000, original.getPerformance()
    );

    Thread t1 = new Thread(() -> {
      boolean ok = facade.updateEmployee(emp1);
      assert ok : "Thread1 updateEmployee(...) must succeed for Employee #1";
    });
    Thread t2 = new Thread(() -> {
      boolean ok = facade.updateEmployee(emp2);
      assert ok : "Thread2 updateEmployee(...) must succeed for Employee #1";
    });

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    Employee finalEmp = facade.getEmployee(1);
    double finalSalary = finalEmp.getSalary();

    assert finalSalary == originalSalary + 500 || finalSalary == originalSalary + 1000 :
        "Concurrent updates are not atomic or consistent! final=" + finalSalary
            + ", expected " + (originalSalary + 500) + " or " + (originalSalary + 1000);
  }

  /**
   * 7) Safety Property: Two departments within the same organization never
   * share the same department ID.
   *
   * Formally: "ALWAYS (distinct department IDs within same org)."
   */
  public static void testDepartmentIdsUnique() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var departments = conn.getDepartments(1);
    var seenIds = new HashSet<Integer>();
    for (var department : departments) {
      assert !seenIds.contains(department.getId()) :
          "Duplicate department ID found: " + department.getId();
      seenIds.add(department.getId());
    }
  }

  /**
   * 8) Safety Property: The system must never throw a NullPointerException during
   * normal execution flows.
   *
   * Formally: "ALWAYS (no NullPointerException)."
   */
  public static void testNoNullPointerExceptions() {
    try {
      InmemConnection conn = InmemConnection.getInstance();
      HrDatabaseFacade.setConnection(conn);
      var departments = conn.getDepartments(1);
      for (var department : departments) {
        var head = department.getHead();
        if (head != null) {
          assert head.getName() != null : "Department head name is null!";
        }
      }
      var employees = conn.getEmployees(1);
      for (var employee : employees) {
        assert employee.getName() != null : "Employee name is null!";
        assert employee.getPosition() != null : "Employee position is null!";
      }
    } catch (NullPointerException e) {
      assert false : "NullPointerException encountered during execution!";
    }
  }

  /**
   * 9) Safety Property: The organization's name is never null or empty.
   *
   * Formally: "ALWAYS (orgName != null and orgName != "")."
   */
  public static void testOrganizationNameValidity() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var organization = conn.getOrganization(1);
    assert organization != null : "Organization is null!";
    assert organization.getName() != null && !organization.getName().isEmpty() :
        "Invalid organization name!";
  }

  /**
   * 10) Liveness Property: Every employee in a department eventually obtains a valid position.
   *
   * Formally: "EVENTUALLY (for each employee, position != null and position != \"\")."
   */
  public static void testEmployeePositionAssignment() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var departments = conn.getDepartments(1);
    for (var department : departments) {
      for (var employee : department.getEmployees()) {
        assert employee.getPosition() != null && !employee.getPosition().isEmpty() :
            "Employee with unassigned position: " + employee.getId();
      }
    }
  }

  /**
   * 11) Safety Property: Employees never have negative salaries.
   *
   * Formally: "ALWAYS (employee.salary >= 0)."
   */
  public static void testEmployeeSalaryNonNegative() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var employees = conn.getEmployees(1);
    for (var employee : employees) {
      assert employee.getSalary() >= 0 : "Employee with negative salary: " + employee.getId();
    }
  }

  /**
   * 12) Liveness Property: Eventually, every employee obtains a valid performance score
   * in the range [0, 100].
   *
   * Formally: "EVENTUALLY (0 <= employee.performance <= 1)."
   */
  public static void testEmployeePerformanceAssignment() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var departments = conn.getDepartments(1);
    for (var department : departments) {
      for (var employee : department.getEmployees()) {
        assert employee.getPerformance() >= 0 && employee.getPerformance() <= 100 :
            "Invalid performance value for employee: " + employee.getId();
      }
    }
  }

  /**
   * 13) Safety Property: If a department has a head, that head must have a valid, non-null name.
   *
   * Formally: "ALWAYS (department.head != null => department.head.name != null)."
   */
  public static void testDepartmentHeadValidity() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    var departments = conn.getDepartments(1);
    for (var department : departments) {
      if (department.getHead() != null) {
        assert department.getHead().getName() != null :
            "Department head with null name: " + department.getId();
      }
    }
  }

  /**
   * 14) Liveness Property: Once an employee is added to a department, that employee
   * eventually appears in the department records.
   *
   * Formally: "EVENTUALLY (employee added => employee appears in department listing)."
   */
  public static void testEmployeeAdditionReflects() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);
    Employee newEmployee = new Employee(999, "Test Employee", new Date());
    conn.addEmployeeToDepartment(1, 1, newEmployee);

    boolean found = false;
    var employees = conn.getEmployees(1);
    for (var employee : employees) {
      if (employee.getId() == 999) {
        found = true;
        break;
      }
    }
    assert found : "Newly added employee not found in records!";
  }

  /**
   * 15) Safety Property: The behavior of `getEmployee` must be consistent across
   * `InmemConnection` and `MysqlConnection`.
   *
   * Formally: "ALWAYS (getEmployee(conn1, orgId, empId) == getEmployee(conn2, orgId, empId))."
   */
  public static void testGetEmployeeConsistency() {
    InmemConnection inmemConn = InmemConnection.getInstance();
    MysqlConnection mysqlConn = MysqlConnection.getInstance();

    // Initialize InmemConnection
    inmemConn.resetTestData();
    HrDatabaseFacade.setConnection(inmemConn);

    // Test with employee ID 1 in organization 1
    Employee inmemEmployee = inmemConn.getEmployee(1, 1);
    Employee mysqlEmployee = mysqlConn.getEmployee(1, 1);

    assert (inmemEmployee == null && mysqlEmployee == null) ||
        (inmemEmployee != null && mysqlEmployee != null &&
            inmemEmployee.getId() == mysqlEmployee.getId() &&
            inmemEmployee.getName().equals(mysqlEmployee.getName())) :
        "getEmployee result mismatch between InmemConnection and MysqlConnection!";
  }

  /**
   * 16) Safety Property: The behavior of `getDepartments` must be consistent across
   * `InmemConnection` and `MysqlConnection`.
   *
   * Formally: "ALWAYS (getDepartments(conn1, orgId) == getDepartments(conn2, orgId))."
   */
  public static void testGetDepartmentsConsistency() {
    InmemConnection inmemConn = InmemConnection.getInstance();
    MysqlConnection mysqlConn = MysqlConnection.getInstance();

    // Initialize InmemConnection
    inmemConn.resetTestData();
    HrDatabaseFacade.setConnection(inmemConn);

    // Get departments from both connections for organization 1
    List<Department> inmemDepartments = inmemConn.getDepartments(1);
    List<Department> mysqlDepartments = mysqlConn.getDepartments(1);

    assert inmemDepartments.size() == mysqlDepartments.size() :
        "getDepartments size mismatch between InmemConnection and MysqlConnection!";

    for (int i = 0; i < inmemDepartments.size(); i++) {
      Department inmemDept = inmemDepartments.get(i);
      Department mysqlDept = mysqlDepartments.get(i);

      assert inmemDept.getId() == mysqlDept.getId() &&
          inmemDept.getName().equals(mysqlDept.getName()) :
          "getDepartments result mismatch at index " + i + " between InmemConnection and MysqlConnection!";
    }
  }

}