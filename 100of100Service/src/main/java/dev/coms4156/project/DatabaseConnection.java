package dev.coms4156.project;

import java.util.List;

/**
 * Interface for interacting with the database.
 * It provides methods for CRUD operations on employees, departments, and organizations.
 * Designed under Strategy Pattern & Data Access Object Pattern
 */
public interface DatabaseConnection {

  String connectionName();

  // Employee CRUD methods //

  /**
   * Adds a new employee to a department in the database.
   *
   * @param organizationId the organization id
   * @param departmentId the internal department id
   * @param employee the employee to add
   * @return the internal employee ID if successful, -1 if failed
   */
  int addEmployeeToDepartment(int organizationId, int departmentId, Employee employee);

  /**
   * Returns an employee in a given organization by external ID.
   *
   * @param organizationId the organization id (clientId)
   * @param externalEmployeeId the external employee id
   * @return the employee if found, null otherwise
   */
  Employee getEmployee(int organizationId, int externalEmployeeId);

  /**
   * Returns a list of employees in a given organization.
   *
   * @param organizationId the organization id
   * @return a list of employees in the organization
   */
  List<Employee> getEmployees(int organizationId);

  /**
   * Updates an employee in the database.
   *
   * @param organizationId the organization id
   * @param employee the employee to update
   * @return true if update successful, false otherwise
   */
  boolean updateEmployee(int organizationId, Employee employee);

  /**
   * Removes an employee from a department in the database.
   *
   * @param organizationId the organization id
   * @param departmentId the internal department id
   * @param employeeId the internal employee id
   * @return true if removal successful, false otherwise
   */
  boolean removeEmployeeFromDepartment(int organizationId, int departmentId, int employeeId);


  // Department CRUD methods //

  /**
   * Inserts a new department into the database.
   *
   * @param organizationId the organization id
   * @param department the department to insert
   * @return the department if successful, null otherwise
   */
  Department insertDepartment(int organizationId, Department department);


  /**
   * Returns a department in a given organization by external ID.
   *
   * @param organizationId the organization id (clientId)
   * @param externalDepartmentId the external department id
   * @return the department if found, null otherwise
   */
  Department getDepartment(int organizationId, int externalDepartmentId);

  /**
   * Returns a list of departments in a given organization.
   *
   * @param organizationId the organization id
   * @return a list of departments in the organization
   */
  List<Department> getDepartments(int organizationId);

  /**
   * Updates a department's information in the database.
   *
   * @param organizationId the organization id
   * @param department the department to update
   * @return true if update successful, false otherwise
   */
  boolean updateDepartment(int organizationId, Department department);

  /**
   * Removes a department from the database.
   *
   * @param organizationId the organization id
   * @param externalDepartmentId the external department id
   * @return true if removal successful, false otherwise
   */
  boolean removeDepartment(int organizationId, int externalDepartmentId);


  // Organization CRUD methods //

  /**
   * Inserts a new organization into the database.
   *
   * @param organization the organization to insert
   * @return the organization if successful, null otherwise
   */
  Organization insertOrganization(Organization organization);

  /**
   * Returns an organization with the given organization id.
   *
   * @param organizationId the organization id
   * @return the organization with the given organization id
   */
  Organization getOrganization(int organizationId);

  /**
   * Updates an organization's information in the database.
   *
   * @param organization the organization to update
   * @return true if update successful, false otherwise
   */
  boolean updateOrganization(Organization organization);

  /**
   * Removes an organization from the database.
   *
   * @param organizationId the organization id
   * @return true if removal successful, false otherwise
   */
  boolean removeOrganization(int organizationId);
}
