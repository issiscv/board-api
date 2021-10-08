package com.example.boardapi.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "balladang";
    private final UserDetailsService userDetailsService;
    
    //토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        //Base64로 인코딩 하고 문자열로 sign 에 저장한다.
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    //토큰 생성 -> 로그인 시 반환 해줌
    public String createToken(String loginId, List<String> roles) {
        //{
        //  "sub": "test",
        //  "roles": [
        //    "ROLE_USER"
        //  ],
        //  "iat": 1633675151,
        //  "exp": 1633676951
        //} 위와 같이 저장된다. payload 에 저장되는 claim 이다.

        Claims claims = Jwts.claims().setSubject(loginId); //payload 에 저장될 claim 에 subject 값 세팅
        claims.put("roles", roles);                        //claim 에 추가적인 값 세팅

        Date now = new Date();        //현재 시간을 나타내주기 위해서

        String jwt = Jwts.builder()
                .setClaims(claims)// payload 에 저장될 claim
                .setIssuedAt(now)// jwt 생성 시간
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)//사용할 알고리즘과(header 에 저장됨) signature 에 들어갈 secret 값 세팅
                .compact();//jwt 생성

        return jwt;
    }

    public Authentication getAuthentication(String token) {
        String tokenSubject = getTokenSubject(token); // 토큰을 파싱해서 payload 에 저장되있는 subject 를 받는다

        //userDetailsService 를 상속 받아서 쓰는 이유는 인증 객체를 만들어 줄때 userDetails 객체로 넣어야 하니깐.
        UserDetails userDetails = userDetailsService.loadUserByUsername(tokenSubject); // Member 엔티티를 받아야 한다.
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //토큰의 payload 에 저장되어 있는 subject 가져옴
    public String getTokenSubject(String token) {
        String subject = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody()//payload 의 claim
                .getSubject();
        return subject;
    }

    //필터에서 X-AUTH-TOKEN 의 헤더 값을 받아오기 위해서
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    // 필터에서 사용 됨
    public boolean validateToken(String jwtToken) {
        try {
            //토큰을 해석하여 정보 단위로 바꿈 (secretKey 는 서명)
            //payload 에 저장되는 정보단위
            Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).getBody().getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
