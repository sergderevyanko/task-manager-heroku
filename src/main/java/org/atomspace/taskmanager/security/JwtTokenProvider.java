package org.atomspace.taskmanager.security;

import io.jsonwebtoken.*;
import org.atomspace.taskmanager.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.atomspace.taskmanager.security.SecurityConstants.SECRET;
import static org.atomspace.taskmanager.security.SecurityConstants.TOKEN_EXPIRATION_TIME;

@Component
public class JwtTokenProvider {

    //Generate the token
    public String generateToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + TOKEN_EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", user.getUsername());
        claims.put("fullname", user.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    //Validate the token
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            System.out.println("SignatureException");
        }catch (MalformedJwtException ex){
            System.out.println("MalformedJwtException");
        }catch (ExpiredJwtException ex){
            System.out.println("ExpiredJwtException");
        }catch (UnsupportedJwtException ex){
            System.out.println("UnsupportedJwtException");
        }catch (IllegalArgumentException ex){
            System.out.println("IllegalArgumentException");
        }
        return false;
    }

    // Get user id from token
    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        Long id = Long.parseLong((String)claims.get("id"));
        return id;
    }
}
