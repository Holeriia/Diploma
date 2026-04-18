package com.company.diploma.security;

import com.company.diploma.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "HeadRole", code = HeadRole.CODE)
public interface HeadRole {
    String CODE = "head-role";

    @MenuPolicy(menuIds = "HeadDasboardView")
    @ViewPolicy(viewIds = {"HeadDasboardView", "MyWorkspace.detail"})
    void screens();

    @EntityPolicy(entityClass = Degree.class, actions = EntityPolicyAction.READ)
    void degree();

    @EntityPolicy(entityClass = Assignment.class, actions = EntityPolicyAction.READ)
    void assignment();

    @EntityPolicy(entityClass = Department.class, actions = EntityPolicyAction.READ)
    void department();

    @EntityAttributePolicy(entityClass = Group.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Group.class, actions = EntityPolicyAction.ALL)
    void group();

    @EntityPolicy(entityClass = Interest.class, actions = EntityPolicyAction.READ)
    void interest();

    @EntityPolicy(entityClass = Participant.class, actions = EntityPolicyAction.READ)
    void participant();

    @EntityPolicy(entityClass = RequestPriority.class, actions = EntityPolicyAction.READ)
    void requestPriority();

    @EntityPolicy(entityClass = Teacher.class, actions = EntityPolicyAction.READ)
    void teacher();

    @EntityPolicy(entityClass = Topic.class, actions = EntityPolicyAction.READ)
    void topic();

    @EntityPolicy(entityClass = Workspace.class, actions = EntityPolicyAction.READ)
    void workspace();

    @EntityPolicy(entityClass = Title.class, actions = EntityPolicyAction.READ)
    void title();

    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.READ)
    void user();

    @EntityPolicy(entityClass = RequestComment.class, actions = EntityPolicyAction.READ)
    void requestComment();

    @EntityPolicy(entityClass = Student.class, actions = EntityPolicyAction.READ)
    void student();

    @EntityPolicy(entityClass = Position.class, actions = EntityPolicyAction.READ)
    void position();

    @EntityPolicy(entityClass = Request.class, actions = EntityPolicyAction.READ)
    void request();
}