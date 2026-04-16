package com.company.diploma.security;

import com.company.diploma.entity.Workspace;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "WorkspaceAccessRole", code = WorkspaceAccessRole.CODE)
public interface WorkspaceAccessRole {
    String CODE = "workspace-access-role";

    @JpqlRowLevelPolicy(
            entityClass = Workspace.class,
            where = "{E}.id in (select p.workspace.id from Participant p where p.user.id = :current_user_id) " +
                    "or exists (select g from {E}.groups g where g in " +
                    "(select s.group from Student s where s.user.id = :current_user_id))"
    )
    void workspace();
}