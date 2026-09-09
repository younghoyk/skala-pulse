package io.skala.feedback.auth;

import io.jsonwebtoken.JwtException;
import io.skala.feedback.common.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring("Bearer ".length());
			try {
				var claims = jwtService.parse(token);
				String displayName = claims.getSubject();
				Role role = Role.valueOf(claims.get("role", String.class));
				Long memberId = claims.get("memberId", Long.class);
				var principal = new AuthenticatedUser(memberId, displayName, role);
				var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
				var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (JwtException | IllegalArgumentException ignored) {
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}
}
