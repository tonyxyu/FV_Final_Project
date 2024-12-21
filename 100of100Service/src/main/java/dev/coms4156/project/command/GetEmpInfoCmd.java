package dev.coms4156.project.command;

import dev.coms4156.project.Employee;
import dev.coms4156.project.HrDatabaseFacade;
import dev.coms4156.project.exception.NotFoundException;

/**
 * A command to get the information of an employee.
 */
public class GetEmpInfoCmd implements Command {
  private final int clientId;
  private final int employeeId;

  /**
   * Constructs a command to get employee information.
   *
   * @param clientId the client ID
   * @param employeeId the employee ID
   */
  public GetEmpInfoCmd(int clientId, int employeeId) {
    this.clientId = clientId;
    this.employeeId = employeeId;
  }

  @Override
  public Object execute() {
    HrDatabaseFacade db = HrDatabaseFacade.getInstance(this.clientId);
    Employee employee = db.getEmployee(this.employeeId);
    if (employee == null) {
      throw new NotFoundException("Employee [" + this.employeeId + "] not found");
    }
    return employee.toJson();
  }
}
