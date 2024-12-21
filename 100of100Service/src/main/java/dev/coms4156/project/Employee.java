package dev.coms4156.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an employee in the organization.
 * Designed under the Composite Design Pattern.
 */
public class Employee implements OrganizationComponent {
  private final int id;
  private final String name;
  private final Date hireDate;
  private String position;
  private double salary;
  private double performance;

  /**
   * Constructs an employee with the given ID, name, and hire date.
   *
   * @param id the ID of the employee (external ID)
   * @param name the name of the employee
   * @param hireDate the hire date of the employee
   */
  public Employee(int id, String name, Date hireDate) {
    this.id = id;
    this.name = name;
    if (hireDate == null) {
      this.hireDate = new Date();
    } else {
      this.hireDate = new Date(hireDate.getTime());
    }
    this.position = "Other";
    this.salary = 0;
    this.performance = 0;
  }

  /**
   * Full constructor for an employee with ID, name, hire date, position, salary, and performance.
   * Primarily used by the database.
   *
   * @param id the ID of the employee (external ID)
   * @param name the name of the employee
   * @param hireDate the hire date of the employee
   * @param position the position of the employee
   * @param salary the current salary of the employee
   * @param performance the performance of the employee
   */
  public Employee(
      int id, String name, Date hireDate, String position, double salary, double performance
  ) {
    this.id = id;
    this.name = name;
    if (hireDate == null) {
      this.hireDate = new Date();
    } else {
      this.hireDate = new Date(hireDate.getTime());
    }
    if (position == null || position.isEmpty()) {
      this.position = "Other";
    } else {
      this.position = position;
    }
    this.salary = salary;
    this.performance = performance;
  }

  /**
   * Returns the ID of the employee.
   *
   * @return the ID of the employee (external ID)
   */
  @Override
  public int getId() {
    return this.id;
  }

  /**
   * Returns the name of the employee.
   *
   * @return the name of the employee
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Returns the type name of the employee.
   *
   * @return the type name of the employee
   */
  @Override
  public String getTypeName() {
    return "Employee";
  }

  /**
   * Returns the child structure of the employee.
   *
   * @return an empty list
   */
  @Override
  public List<OrganizationComponent> getChildren() {
    return new ArrayList<>();
  }

  /**
   * Returns the hire date of the employee.
   *
   * @return the hire date of the employee
   */
  public Date getHireDate() {
    return new Date(this.hireDate.getTime());
  }

  /**
   * Returns the position of the employee.
   *
   * @return the position of the employee
   */
  public String getPosition() {
    return this.position;
  }

  /**
   * Sets the position of the employee.
   *
   * @param position the position of the employee
   */
  public void setPosition(String position) {
    this.position = position;
  }

  /**
   * Returns the salary of the employee.
   *
   * @return the salary of the employee
   */
  public double getSalary() {
    return this.salary;
  }

  /**
   * Sets the salary of the employee.
   *
   * @param salary the new salary of the employee
   */
  public void setSalary(double salary) {
    this.salary = salary;
  }

  /**
   * Returns the performance of the employee.
   *
   * @return the performance of the employee
   */
  public double getPerformance() {
    return this.performance;
  }

  /**
   * Sets the performance of the employee.
   *
   * @param performance the new performance of the employee
   */
  public void setPerformance(double performance) {
    this.performance = performance;
  }

  /**
   * Report all the information of the employee in a JSON format.
   *
   * @return a Map of the employee information that can be easily converted to JSON
   */
  public Map<String, Object> toJson() {
    Map<String, Object> result = new HashMap<>();
    result.put("ID", this.id);
    result.put("name", this.name);
    result.put("hireDate", this.hireDate);
    result.put("position", this.position);
    result.put("salary", this.salary);
    result.put("performance", this.performance);
    result.put("representation", this.toString());
    return result;
  }

  /**
   * Returns the basic information of the employee, including the name and ID.
   *
   * @return the string representation of the employee
   */
  @Override
  public String toString() {
    return "Employee: " + this.name + " (ID: " + this.id + ")" + " Hired at: " + this.hireDate;
  }
}
