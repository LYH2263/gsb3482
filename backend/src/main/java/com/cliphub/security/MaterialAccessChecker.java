package com.cliphub.security;

import com.cliphub.entity.Material;
import com.cliphub.entity.User;
import com.cliphub.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 素材访问校验组件，统一判断用户对素材的可见性（PUBLIC / PRIVATE / TEAM）。
 */
@Component
@RequiredArgsConstructor
public class MaterialAccessChecker {

    private final UserMapper userMapper;

    public boolean canAccess(UserPrincipal principal, Material material) {
        if ("ADMIN".equals(principal.getRole())) {
            return true;
        }
        if (Objects.equals(material.getOwnerId(), principal.getId())) {
            return true;
        }
        if ("PUBLIC".equalsIgnoreCase(material.getVisibility())) {
            return true;
        }
        if ("TEAM".equalsIgnoreCase(material.getVisibility()) && principal.getTeamId() != null) {
            User owner = userMapper.selectById(material.getOwnerId());
            return owner != null && Objects.equals(owner.getTeamId(), principal.getTeamId());
        }
        return false;
    }
}
