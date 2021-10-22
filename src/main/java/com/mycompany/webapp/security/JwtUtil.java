package com.mycompany.webapp.security;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
	// *비밀키(노출이 되면 안됨)
	// *서버가 자기 자신이 발행한 JWT인지 아닌지 검사할때만 사용 (토큰을 해석하기 위한 키가 아님)
	private static final String secretKey = "qwer";
	
	// <JWT 생성 ( 개인정보는 JWT에 저장하면 안됨)>
	public static String createToken(String mid, String authority) {
		log.info("실행");
		
		String result = null;
		try {
			String token = Jwts.builder()
					// <헤더에 알고리즘 종류 지정>
					.setHeaderParam("alg", "HS256")
					// <헤더에 토큰의 타입 지정>
					.setHeaderParam("typ", "JWT")
					// <토큰의 유효 기간> : 1년간 유효한 토큰 생성
					.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60 * 24))
					// <페이로드(클레임) 설정>
					.claim("mid", mid)
					.claim("authority", authority)
					// <서명 설정>
					.signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8")) // 비밀키에 한국어를 넣을 경우를 위해 인코딩 타입 설정
					// <토큰 생성>
					.compact();
			result = token;
		} catch (Exception e) { }
		return result;
	}
	
	// <JWT 유효성 검사 (클라이언트에게 받은 토큰 검사)>
	public static Claims validateToken(String token) {
		log.info("실행");
		
		Claims result = null;
		try {
			Claims claims = Jwts.parser()
					// <비밀키 입력>
					.setSigningKey(secretKey.getBytes("UTF-8"))
					// <claims 파싱>
					// Jws : 서명이 된 JWT 토큰
					.parseClaimsJws(token)
					.getBody();
			log.info("시간 체크를 하는지 안하는지 확인"); // 일부러 유효기간을 만료되게 만든 테스트 환경에서 출력 안됨. 밑의 코드 사용할 필요 없음. 이미 시간 체크를 자체적으로 함 <- 무슨말..?
			// <만료되었는지 검사>
			/*boolean validate = claims.getExpiration().after(new Date());
			if (validate)
				result = claims;*/
			result = claims;
		} catch (Exception e) { }
		return result;
	}
	
	// <JWT에서 정보 얻기>
	public static String getMid(Claims claims) {
		log.info("실행");
		// 문자열 타입으로 리턴
		return claims.get("mid", String.class);
	}
	
	// <JWT에서 정보 얻기>
	public static String getAuthority(Claims claims) {
		log.info("실행");
		// 문자열 타입으로 리턴
		return claims.get("authority", String.class);
	}
	
	/*	// 확인
		public static void main(String[] args) throws Exception {
			// 토큰 생성
			String mid = "user";
			String authority = "ROLE_USER";
			String jwt = createToken(mid, authority);
			log.info(jwt);
			
			// 토큰 유효성 검사
			Claims claims = validateToken(jwt);
			if (claims != null) {
				log.info("유효한 토큰");
				log.info("mid: " + getMid(claims));
				log.info("authority: " + getAuthority(claims));
			} else {
				log.info("유효하지 않은 토큰");
			}
		}*/
}
