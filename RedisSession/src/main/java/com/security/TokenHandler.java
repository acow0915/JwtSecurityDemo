package com.security;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenHandler {

	final long EXPIRATIONTIME = 15*60*1000; 		// 15 minutes
	final String SECRET = "ghwio#$%^&gj2otu29t%^&*230*()910491g";			// private key, better read it from an external file
	
	final public String TOKEN_PREFIX = "Bearer";			// the prefix of the token in the http header
	final public String HEADER_STRING = "Authorization";	// the http header containing the prexif + the token
	

	/**
	 * Generate a token from the username.
	 * 
	 * @param username	The subject from which generate the token.
	 * 
	 * @return			The generated token.
	 */
	public String build(String username) {
		
		Date now = new Date();
		
		String JWT = Jwts.builder()
						 .setId(UUID.randomUUID().toString())
				 		 .setSubject(username)
				 		 .setIssuedAt(now)
				 		 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				 		 .signWith(SignatureAlgorithm.HS512, SECRET)
				 		 .compact();
		
		return JWT;
		
	}
	
	/**
	 * userName
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

	/**
	 * 開始時間
	 * @param token
	 * @return
	 */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * 結束時間
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 重置token時間
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        final Date createdDate = Date.from(Clock.systemDefaultZone().instant());
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }
    
    /**
     * 計算過期時間
     * @param createdDate
     * @return
     */
    private Date calculateExpirationDate(Date createdDate) {
    	LocalDateTime expirationTime = LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault());
    	expirationTime = expirationTime.plusSeconds(600 * 1000);
        return Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(Date.from(Clock.systemDefaultZone().instant()));
    }
	
	
	public static void main(String[] args){
		TokenHandler handler = new TokenHandler();
		System.out.println(handler.build("abcde"));
		
		System.out.println(handler.getUsernameFromToken("eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJhNWQwNTA3ZC0zZjIyLTQwMDQtYjIzYi1jNmViYjlmNzI3NGIiLCJzdWIiOiJ0aW0iLCJpYXQiOjE1MTc1NDQ4NzMsImV4cCI6MTUxNzU0NTc3M30.BHcUSno9QscBX8GMDupZviLuwDJGF5KVRGLyPjt-lGhULaoKscsejJJC2aZ_4ARG6GV0IjwRNPZ_JovLg_UPGg"));
	}

	/**
	 * 驗證資料
	 * @param authToken
	 * @param userDetails
	 * @return
	 */
	public boolean validateToken(String authToken, UserDetails userDetails) {
		final String tokenUsername = getUsernameFromToken(authToken);
        final Date created = getIssuedAtDateFromToken(authToken);
        //final Date expiration = getExpirationDateFromToken(token);
        return (
        		tokenUsername.equals(userDetails.getUsername())
                    && !isTokenExpired(authToken)
                    && created.before(Date.from(Clock.systemDefaultZone().instant()))
        );
	}
}
