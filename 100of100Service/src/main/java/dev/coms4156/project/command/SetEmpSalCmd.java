package dev.coms4156.project.command;

import dev.coms4156.project.Employee;
import dev.coms4156.project.HrDatabaseFacade;
import dev.coms4156.project.exception.InternalServerErrorException;
import dev.coms4156.project.exception.NotFoundException;

/**
 * A command to set the salary of an employee.
 */
public class SetEmpSalCmd implements Command {
  private final int clientId;
  private final int employeeId;
  private final double salary;

  /**
   * Constructs a command to set the salary of an employee.
   *
   * @param clientId the client ID
   * @param employeeId the employee ID
   * @param salary the salary to set
   */
  public SetEmpSalCmd(int clientId, int employeeId, double salary) {
    this.clientId = clientId;
    this.employeeId = employeeId;
    this.salary = salary;
  }

  /**
   * Executes the command.
   *
   * @return the result of the command
   */
  @Override
  public Object execute() {
    HrDatabaseFacade db = HrDatabaseFacade.getInstance(this.clientId);
    Employee emp = db.getEmployee(this.employeeId);
    if (emp == null) {
      throw new NotFoundException("Employee [" + this.employeeId + "] not found");
    }
    emp.setSalary(this.salary);
    boolean result = db.updateEmployee(emp);
    if (!result) {
      throw new InternalServerErrorException("Failed to update employee [" + this.employeeId + "]");
    }

    return "Successfully set salary for employee [" + this.employeeId + "] to " + this.salary;
  }
}
