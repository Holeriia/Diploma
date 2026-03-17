package com.company.diploma.security;

import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "StudentRole", code = StudentRole.CODE)
public interface StudentRole {
    String CODE = "student-role";

    @MenuPolicy(menuIds = "StudentProfileView")
    @ViewPolicy(viewIds = "StudentProfileView")
    void screens();
}