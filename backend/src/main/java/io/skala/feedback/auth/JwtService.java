package io.skala.feedback.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.skala.feedback.common.Role;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {

	private final SecretKey signingKey;
	private final long expirationMinutes;

	public JwtService(JwtProperties properties) {
		this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes());
		this.expirationMinutes = properties.expirationMinutes();
	}

	public String issueToken(String displayName, Role role, String className) {
		Instant now = Instant.now();
		var builder = Jwts.builder()
				.subject(displayName)
				.claim("role", role.name());
		if (className != null) {
			builder.claim("className", className);
		}
		return builder
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
				.signWith(signingKey)
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
