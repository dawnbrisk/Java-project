package com.blitz.springboot4.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private static final String SECRET_KEY = "yourverylongsecretkeyatleast32charslong123456123456789";

    public String generateToken(UserDetails userDetails) {

        Date now = new Date();
        // 获取第二天凌晨0点的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 1); // 加一天
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 设置小时为0
        calendar.set(Calendar.MINUTE, 0);      // 设置分钟为0
        calendar.set(Calendar.SECOND, 0);      // 设置秒为0
        calendar.set(Calendar.MILLISECOND, 0); // 设置毫秒为0
        Date expiration = calendar.getTime();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}

