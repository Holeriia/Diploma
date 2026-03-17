package com.company.diploma.security;

import com.company.diploma.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "TeacherRole", code = TeacherRole.CODE)
public interface TeacherRole {
    String CODE = "teacher-role";

    @EntityAttributePolicy(entityClass = Degree.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Degree.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE})
    void degree();

    @EntityAttributePolicy(entityClass = Department.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Department.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE})
    void department();

    @EntityAttributePolicy(entityClass = Interest.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Interest.class, actions = EntityPolicyAction.ALL)
    void interest();

    @EntityAttributePolicy(entityClass = Position.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Position.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE})
    void position();

    @EntityAttributePolicy(entityClass = Teacher.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Teacher.class, actions = EntityPolicyAction.ALL)
    void teacher();

    @EntityAttributePolicy(entityClass = Title.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Title.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.DELETE})
    void title();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.ALL)
    void user();

    @MenuPolicy(menuIds = "TeacherProfileView")
    @ViewPolicy(viewIds = "TeacherProfileView")
    void screens();
}