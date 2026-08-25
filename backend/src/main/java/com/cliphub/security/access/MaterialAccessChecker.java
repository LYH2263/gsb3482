package com.cliphub.security.access;

import com.cliphub.entity.Material;
import com.cliphub.entity.User;
import com.cliphub.mapper.UserMapper;
import com.cliphub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 素材访问校验组件。
 *
 * <p>统一封装素材可见性（PUBLIC / PRIVATE / TEAM）判定，供各业务服务复用，
 * 避免可见性规则在多处重复实现而产生行为漂移。</p>
 */
@Component
@RequiredArgsConstructor
public class MaterialAccessChecker {

    private final UserMapper userMapper;

    /**
     * 判断当前登录用户是否可以访问指定素材。
     *
     * <p>规则：管理员与素材所有者始终可访问；PUBLIC 素材对所有人开放；
     * TEAM 素材仅对与所有者同一团队的用户开放；其余（含 PRIVATE）不可访问。</p>
     */
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
