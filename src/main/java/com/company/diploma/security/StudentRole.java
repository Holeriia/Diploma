package com.company.diploma.security;

import com.company.diploma.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "StudentRole", code = StudentRole.CODE)
public interface StudentRole {
    String CODE = "student-role";

    @MenuPolicy(menuIds = "StudentProfileView")
    @ViewPolicy(viewIds = "StudentProfileView")
    void screens();

    @EntityAttributePolicy(entityClass = Student.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Student.class, actions = EntityPolicyAction.ALL)
    void student();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.ALL)
    void user();

    @EntityPolicy(entityClass = Group.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void group();

    @EntityPolicy(entityClass = Department.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void department();

    @EntityPolicy(entityClass = Interest.class, actions = EntityPolicyAction.ALL)
    void interest();
}