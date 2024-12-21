package dev.coms4156.project.command;

import dev.coms4156.project.HrDatabaseFacade;
import dev.coms4156.project.Organization;
import dev.coms4156.project.exception.NotFoundException;

/**
 * A command to get the information of an organization.
 */
public class GetOrgInfoCmd implements Command {
  private final int clientId;

  public GetOrgInfoCmd(int clientId) {
    this.clientId = clientId;
  }

  @Override
  public Object execute() {
    HrDatabaseFacade db = HrDatabaseFacade.getInstance(this.clientId);
    Organization organization = db.getOrganization();
    if (organization == null) {
      throw new NotFoundException("Organization [" + this.clientId + "] not found");
    }
    return organization.toJson();
  }
}