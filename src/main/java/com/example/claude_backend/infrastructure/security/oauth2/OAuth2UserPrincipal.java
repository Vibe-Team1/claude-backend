package com.example.claude_backend.infrastructure.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.claude_backend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Spring Security의 OAuth2User와 UserDetails를 구현하는 Principal 클래스
 * JWT 토큰과 OAuth2 인증 모두에서 사용
 *
 * @author AI Assistant
 * @since 2025-01-20
 */
@Getter
@AllArgsConstructor
public class OAuth2UserPrincipal implements OAuth2User, UserDetails {

    private UUID id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    /**
     * User 엔티티로부터 OAuth2UserPrincipal 생성
     *
     * @param user User 엔티티
     * @param attributes OAuth2 속성
     * @return OAuth2UserPrincipal
     */
    public static OAuth2UserPrincipal create(User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new OAuth2UserPrincipal(
                user.getId(),
                user.getEmail(),
                null, // OAuth2 로그인은 비밀번호 없음
                authorities,
                attributes
        );
    }

    /**
     * User 엔티티로부터 OAuth2UserPrincipal 생성 (JWT용)
     *
     * @param user User 엔티티
     * @return OAuth2UserPrincipal
     */
    public static OAuth2UserPrincipal create(User user) {
        return create(user, Collections.emptyMap());
    }

    // OAuth2User 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    // UserDetails 구현
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}