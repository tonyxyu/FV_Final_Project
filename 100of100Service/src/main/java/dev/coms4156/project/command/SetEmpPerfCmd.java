package dev.coms4156.project.command;

import dev.coms4156.project.Employee;
import dev.coms4156.project.HrDatabaseFacade;
import dev.coms4156.project.exception.InternalServerErrorException;
import dev.coms4156.project.exception.NotFoundException;

/**
 * A command to set the performance of an employee.
 */
public class SetEmpPerfCmd implements Command {
  private final int clientId;
  private final int employeeId;
  private final double performance;

  /**
   * Constructs a command to set the salary of an employee.
   *
   * @param clientId the client ID
   * @param employeeId the employee ID
   * @param performance the salary to set
   */
  public SetEmpPerfCmd(int clientId, int employeeId, double performance) {
    this.clientId = clientId;
    this.employeeId = employeeId;
    this.performance = performance;
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
    emp.setPerformance(this.performance);
    if (!db.updateEmployee(emp)) {
      throw new InternalServerErrorException("Failed to update employee [" + this.employeeId + "]");
    }
    return "Employee [" + this.employeeId + "] performance set to " + this.performance;
  }
}
