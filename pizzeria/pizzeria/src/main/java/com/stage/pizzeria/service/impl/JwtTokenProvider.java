package com.stage.pizzeria.service.impl;

import com.stage.pizzeria.model.ExpirationRisk;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Secret Key for signing tokens
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    @Value("${jwt.refreshExpiration}")
    private int jwtRefreshExpirationInMs;

    /**
     * Generate a JWT token from authentication
     *
     * @param authentication Authentication object containing user details
     * @return String Jwt token
     */
    public String generateToken(Authentication authentication){
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .issuer("pizzeria-app")
                .subject(userPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();

    }

    /**
     * Generate a JWT Token from the user's email
     *
     * @param userEmail String
     * @return String JWT token
     */
    public String generateTokenFromEmail(String userEmail) {
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return Jwts.builder()
                .issuer("pizzeria-app")
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get the user's email from the token
     *
     * @param token String
     * @return String email
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Get the expiration Date from the token
     *
     * @param token String
     * @return Date expiration date
     */
     public Date getExpirationDateFromToken(String token) {
        Claims claims = (Claims) Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
     }


    /**
     * Validate JWT Token
     *
     * @param authToken Token to validate
     * @return boolean
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getSigningKey())
                    .requireIssuer("pizzeria-app")
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }catch(SecurityException e) {
            logger.error("Signature JWT invalide: {}", e.getMessage());
        }catch(MalformedJwtException e) {
            logger.error("Token JWT malformé: {}", e.getMessage());
        }catch(ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
        }catch(UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
        }catch(IllegalArgumentException e) {
            logger.error("Chaîne de claims JWT vide: {}", e.getMessage());
        }catch(IncorrectClaimException e) {
            logger.error("Claim invalide: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Gets the time remaining before token expiration in milliseconds
     *
     * @param token JWT Token
     * @return Date temps avant expiration en ms
     */
    public long getTimeToExpirationMs(String token) {
        try {
            Date expirationDate = getExpirationDateFromToken(token);
            long timeRemaining = expirationDate.getTime() - System.currentTimeMillis();
            return Math.max(0, timeRemaining);
        }catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get the time remaining before expiration in seconds
     *
     * @param token String
     * @return long
     */
    public long getTimeToExpirationSeconds(String token) {
        return getTimeToExpirationMs(token) / 1000;
    }

    /**
     * Get the time remaining before expiration in minutes
     *
     * @param token String
     * @return long
     */
    public long getTimeToExpirationMinutes(String token) {
        return getTimeToExpirationSeconds(token) / 60;
    }

    /**
     * Get the token from the Authorization header
     *
     * @param bearerToken String
     * @return String
     */
    public String getTokenFromBearerString(String bearerToken) {
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * remaining time formatted for display
     *
     * @param token String
     * @return String
     */
    public String getFormattedTimeToExpiration(String token) {
        long totalSeconds = getTimeToExpirationSeconds(token);

        if(totalSeconds <= 0) {
            return "Expiré";
        }
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if(hours > 0) {
            return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        } else if(minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds);
        }else {
            return String.format("%ds", seconds);
        }
    }

    /**
     * checks if the token expires in x minutes
     *
     * @param token
     * @param thresholdMinutes
     * @return
     */
    public boolean isTokenExpiringSoon(String token, int thresholdMinutes) {
        long minutesToRemaining = getTimeToExpirationMinutes(token);
        return minutesToRemaining > 0 && minutesToRemaining <= thresholdMinutes;
    }

    public ExpirationRisk getExpirationRisk(String token) {
        long minuutesRemaining = getTimeToExpirationMinutes(token);

        if(minuutesRemaining <= 0) {
            return ExpirationRisk.EXPIRED;
        } else if (minuutesRemaining <= 5) {
            return ExpirationRisk.CRITICAL;
        } else if (minuutesRemaining <= 30) {
            return ExpirationRisk.WARNING;
        }else {
            return ExpirationRisk.SAFE;
        }
    }

    /**
     * Generate a secure signing key
     *
     * @return secret key for sign tokens
     */
    private Key getSigningKey() {
       if(jwtSecret.length() < 64) {
           throw new IllegalArgumentException("JwtSecret doit faire au moins 64 caractères pour HS512");
       }
       return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
