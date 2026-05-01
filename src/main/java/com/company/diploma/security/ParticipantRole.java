package com.company.diploma.security;

import com.company.diploma.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "ParticipantRole", code = ParticipantRole.CODE)
public interface ParticipantRole {
    String CODE = "participant-role";

    @MenuPolicy(menuIds = "MyWorkspace.list")
    @ViewPolicy(viewIds = {"MyWorkspace.list", "WorkspaceDashboardView", "Request.create", "Request.detail", "Request.approval", "RequestComment.detail", "RequestPriority.detail", "Participant.list", "AssignmentApprovalView", "Assignment.list", "Assignment.detail", "Topic.list", "Topic.detail", "Interest.detail", "Interest.list", "TopicAssignmentView", "RequestCommentView"})
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

    @EntityAttributePolicy(entityClass = Participant.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Participant.class, actions = EntityPolicyAction.ALL)
    void participant();

    @EntityAttributePolicy(entityClass = User.class, attributes = {"firstName", "lastName", "patronymic"}, action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @EntityAttributePolicy(entityClass = Assignment.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Assignment.class, actions = EntityPolicyAction.ALL)
    void assignment();

    @EntityAttributePolicy(entityClass = Topic.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Topic.class, actions = EntityPolicyAction.ALL)
    void topic();

    @EntityAttributePolicy(entityClass = Interest.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Interest.class, actions = EntityPolicyAction.ALL)
    void interest();
}