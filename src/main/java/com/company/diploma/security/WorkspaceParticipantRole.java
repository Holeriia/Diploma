package com.company.diploma.security;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.Request;
import com.company.diploma.entity.Topic;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "WorkspaceParticipantRole", code = WorkspaceParticipantRole.CODE)
public interface WorkspaceParticipantRole {
    String CODE = "workspace-participant-role";

    @JpqlRowLevelPolicy(entityClass = Request.class, where = "{E}.initiator.user.id = :current_user_id")
    void request();

    @JpqlRowLevelPolicy(entityClass = Assignment.class, where = "({E}.mentor.user.id = :current_user_id or {E}.mentee.user.id = :current_user_id)")
    void assignment();

    @JpqlRowLevelPolicy(entityClass = Topic.class, where = "{E}.author.user.id = :current_user_id")
    void topic();
}