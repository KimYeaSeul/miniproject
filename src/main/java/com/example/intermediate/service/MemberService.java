package com.example.intermediate.service;

import com.example.intermediate.controller.request.*;
import com.example.intermediate.controller.response.MemberResponseDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.MemberRepository;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;


  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
      return ResponseDto.fail("400",
          "Password and Confirm Password do not match");
    }

    Member member = Member.builder()
            .username(requestDto.getUsername())
            .nickname(requestDto.getNickname())
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .kakaoId(null)
            .build();
    memberRepository.save(member);
    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .username(member.getUsername())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMemberByUsername(requestDto.getUsername());
    if (null == member) {
      return ResponseDto.fail("400",
          "User not found");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("400", "Password error");
    }

    if(member.getKakaoId()!=null){
      return ResponseDto.fail("403", "Please log in with Kakao");
    }

    if(member.getGoogleId()!=null){
      return ResponseDto.fail("403", "Please log in with Google");
    }

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .username(member.getUsername())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }


  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
      return ResponseDto.fail("403", "Token is not valid");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("400",
          "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }

  @Transactional(readOnly = true)
  public Member isPresentMemberByNickname(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }

  @Transactional(readOnly = true)
  public Member isPresentMemberByUsername(String username) {
    Optional<Member> optionalMember = memberRepository.findByUsername(username);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("RefreshToken", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

  public ResponseDto<?> checkUsername(String username){
    Member member = isPresentMemberByUsername(username);
    if(member == null) return ResponseDto.success(true);
    else return ResponseDto.fail("400","ID already exists");
  }

  public ResponseDto<?> checkNickname(String nickname){
    Member member = isPresentMemberByNickname(nickname);
    if(member == null) return ResponseDto.success(true);
    else return ResponseDto.fail("400","Nickname already exists");
  }

  public ResponseDto<?> kakaoLogin(String code, HttpServletResponse response) {
    try {
      // 1. "인가 코드"로 "액세스 토큰" 요청
      String accessToken = getKakaoAccessToken(code);
      // 2. 토큰으로 카카오 API 호출
      KakoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

      // DB 에 중복된 Kakao Id 가 있는지 확인
      Long kakaoId = kakaoUserInfo.getId();
      Member kakaoUser = memberRepository.findByKakaoId(kakaoId)
              .orElse(null);
      if(kakaoUser != null){
        TokenDto tokenDto = tokenProvider.generateTokenDto(kakaoUser);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(kakaoUser.getId())
                        .username(kakaoUser.getUsername())
                        .nickname(kakaoUser.getNickname())
                        .createdAt(kakaoUser.getCreatedAt())
                        .modifiedAt(kakaoUser.getModifiedAt())
                        .build()
        );
      }
      else{
        // 회원가입
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .nickname(kakaoUserInfo.getNickname())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .kakaoId(kakaoId)
                .build();
        memberRepository.save(member);
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
      }
    }
    catch (JsonProcessingException e){
      return ResponseDto.fail("403","Failed to log in with Kakao");
    }
  }

  private String getKakaoAccessToken(String code) throws JsonProcessingException {
    // HTTP Header 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP Body 생성
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", "2b986d1b574416a7d6d064619545aaff");//내 api키
    body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
    body.add("code", code);//카카오로부터 받은 인가코드

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
            new HttpEntity<>(body, headers);//httpentity객체를 만들어서 보냄
    RestTemplate rt = new RestTemplate();//서버 대 서버 요청을 보냄
    ResponseEntity<String> response = rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String.class
    );//리스폰스 받기

    // HTTP 응답 (JSON) -> 액세스 토큰 파싱
    String responseBody = response.getBody();//바디부분
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);//json형태를 객체형태로 바꾸기
    return jsonNode.get("access_token").asText();
  }

  private KakoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
    // HTTP Header 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
    RestTemplate rt = new RestTemplate();//서버 대 서버 요청을 보냄
    ResponseEntity<String> response = rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoUserInfoRequest,
            String.class
    );

    String responseBody = response.getBody();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);
    Long id = jsonNode.get("id").asLong();
    String nickname = jsonNode.get("properties")
            .get("nickname").asText();

    return new KakoUserInfoDto(id,nickname);
  }
  @Value("${google.client.id}")
  private String CLIENT_ID;
  @Value("${google.client.pw}")
  private String CLIENT_SECRET;
  @Value("${google.redirect.url}")
  private String REDIRECT_URI;
  public ResponseDto<?> googleLogin(String code, HttpServletResponse response) {
    try {
      // 1. "인가 코드"로 "액세스 토큰" 요청
      String accessToken = getGoogleAccessToken(code);
      // 2. 토큰으로 카카오 API 호출
      GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(accessToken);

      // DB 에 중복된 Kakao Id 가 있는지 확인
      String googleId = googleUserInfo.getId();
      Member googleUser = memberRepository.findByGoogleId(googleId)
              .orElse(null);
      if(googleUser != null){
        TokenDto tokenDto = tokenProvider.generateTokenDto(googleUser);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(googleUser.getId())
                        .username(googleUser.getUsername())
                        .nickname(googleUser.getNickname())
                        .createdAt(googleUser.getCreatedAt())
                        .modifiedAt(googleUser.getModifiedAt())
                        .build()
        );
      }
      else{
        // 회원가입
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .nickname(googleUserInfo.getName())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .googleId(googleId)
                .build();
        memberRepository.save(member);
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .nickname(member.getNickname())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
      }


    }catch (JsonProcessingException e){
      return ResponseDto.fail("403","Failed to log in with Google");
    }
  }

  private String getGoogleAccessToken(String code) throws JsonProcessingException {
    String url = "https://oauth2.googleapis.com/token";

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("client_id", CLIENT_ID);
    params.add("client_secret", CLIENT_SECRET);
    params.add("redirect_uri", REDIRECT_URI);
    params.add("grant_type", "authorization_code");

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/x-www-form-urlencoded");


    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
    RestTemplate rt = new RestTemplate();//서버 대 서버 요청을 보냄
    ResponseEntity<String> accessTokenResponse = rt.exchange(url, HttpMethod.POST, httpEntity, String.class);

    // HTTP 응답 (JSON) -> 액세스 토큰 파싱
    String responseBody = accessTokenResponse.getBody();//바디부분
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(responseBody);//json형태를 객체형태로 바꾸기
    return jsonNode.get("access_token").asText();
  }

  private GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
    String url = "https://www.googleapis.com/oauth2/v1/userinfo";
    // HTTP Header 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(headers);
    RestTemplate rt = new RestTemplate();//서버 대 서버 요청을 보냄
    ResponseEntity<String> response = rt.exchange(url, HttpMethod.GET, request, String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(response.getBody());
    String id = jsonNode.get("id").toString();
    String name = jsonNode.get("name").toString();
    return new GoogleUserInfoDto(id,name);
  }
}
