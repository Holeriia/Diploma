package com.company.diploma.security;

import com.company.diploma.entity.Request;
import com.company.diploma.entity.RequestComment;
import com.company.diploma.entity.RequestPriority;
import com.company.diploma.entity.Workspace;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "ParticipiantRole", code = ParticipiantRole.CODE)
public interface ParticipiantRole {
    String CODE = "participiant-role";

    @MenuPolicy(menuIds = "MyWorkspace.list")
    @ViewPolicy(viewIds = {"MyWorkspace.list", "WorkspaceDashboardView"})
    void screens();

    @EntityAttributePolicy(entityClass = Request.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Request.class, actions = EntityPolicyAction.ALL)
    void request();

    @EntityAttributePolicy(entityClass = RequestComment.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = RequestComment.class, actions = EntityPolicyAction.ALL)
    void requestComment();

    @EntityAttributePolicy(entityClass = RequestPriority.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = RequestPriority.class, actions = EntityPolicyAction.ALL)
    void requestPriority();

    @EntityAttributePolicy(entityClass = Workspace.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Workspace.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void workspace();
}