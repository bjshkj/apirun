package io.apirun.dto;

import io.apirun.base.domain.Organization;
import io.apirun.base.domain.Project;
import io.apirun.base.domain.Workspace;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrganizationResource {
    private List<Organization> organizations = new ArrayList<>();
    private List<Workspace> workspaces = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
}
