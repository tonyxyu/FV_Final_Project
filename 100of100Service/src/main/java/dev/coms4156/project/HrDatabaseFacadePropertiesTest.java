package dev.coms4156.project;

import dev.coms4156.project.exception.NotFoundException;

public class HrDatabaseFacadePropertiesTest {

  // 1) dbConnection must not be null
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

  // 2) concurrency: two calls to getInstance(...) must yield the same object
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

  // 3) removing an organization -> getInstance() fails
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

  // 4) bounded liveness: after updateEmployee(...), we see updated data
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
   * 5) Invalid IDs must not crash or cause erroneous state.
   *    E.g., calling getEmployee(-1) or getDepartment(-100) should simply return null
   *    (or a safe result), not break the code.
   */
  public static void testInvalidIDsDoNotCrash() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    // Query negative/zero employee ID
    Employee e = facade.getEmployee(-1);  // or facade.getEmployee(0)
    // We expect null, but definitely no crash/exception
    assert e == null : "getEmployee(-1) should return null, not crash or non-null!";

    // Query negative/zero department ID
    Department d = facade.getDepartment(-100);
    assert d == null : "getDepartment(-100) should return null!";
  }

  /**
   * 6) If an employee doesn't exist, getEmployee(...) is null
   *    and updateEmployee(...) returns false, preserving consistent state.
   */
  public static void testNonExistentEmployee() {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    // Let's pick an employee ID not in the inmem data. Suppose #99 doesn't exist.
    Employee e = facade.getEmployee(99);
    assert e == null : "Employee #99 should not exist in default inmem data!";

    // Try to update a null or new Employee object => facade should return false
    Employee fakeEmp = new Employee(99, "FakeName", new java.util.Date());
    fakeEmp.setSalary(9999.99);
    boolean updated = facade.updateEmployee(fakeEmp);

    // We expect false because there's no real employee #99
    assert !updated : "Facade must reject updateEmployee(...) for non-existent employees!";
  }

  /**
   * 7) Atomic update concurrency: If two threads update the same employee concurrently,
   *    we expect either the last update wins or a well-defined final state, but not corruption.
   */
  public static void testConcurrentEmployeeUpdates() throws InterruptedException {
    InmemConnection conn = InmemConnection.getInstance();
    HrDatabaseFacade.setConnection(conn);

    HrDatabaseFacade facade = HrDatabaseFacade.getInstance(1);

    Employee original = facade.getEmployee(1);
    assert original != null : "Employee #1 must exist to test concurrency";

    double originalSalary = original.getSalary();

    // We'll increment the salary in two threads, each by 500.
    // The final salary should be originalSalary + 500 or +1000, depending on last-wins or sequential update.
    // We just want to ensure no corruption (like negative or random).
    Employee emp1 = new Employee(
        1, original.getName(), original.getHireDate(),
        original.getPosition(), originalSalary + 500, original.getPerformance()
    );
    Employee emp2 = new Employee(
        1, original.getName(), original.getHireDate(),
        original.getPosition(), originalSalary + 1000, original.getPerformance()
    );

    // Two threads: each calls updateEmployee(...) with a different new salary
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

    // After concurrency, getEmployee(1) must have either +500 or +1000
    Employee finalEmp = facade.getEmployee(1);
    double finalSalary = finalEmp.getSalary();

    // We check for no weird corruption:
    assert finalSalary == originalSalary + 500 || finalSalary == originalSalary + 1000 :
        "Concurrent updates are not atomic or consistent! final=" + finalSalary
            + ", expected " + (originalSalary + 500) + " or " + (originalSalary + 1000);
  }
}