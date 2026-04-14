package com.company.diploma.security;

import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "HeadRole", code = HeadRole.CODE)
public interface HeadRole {
    String CODE = "head-role";

    @MenuPolicy(menuIds = "HeadDasboardView")
    @ViewPolicy(viewIds = "HeadDasboardView")
    void screens();
}