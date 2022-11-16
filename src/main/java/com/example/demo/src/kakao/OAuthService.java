package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.kakao.model.PostOAuthReq;
import com.example.demo.src.kakao.model.PostOAuthRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class OAuthService {
    @Autowired
    private OAuthProvider oAuthProvider;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private JourneyProvider journeyProvider;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OAuthService(UserProvider userProvider, OAuthProvider oAuthProvider, JwtService jwtService,UserDao userDao,JourneyProvider journeyProvider){
        this.jwtService=jwtService;
        this.oAuthProvider=oAuthProvider;
        this.userDao=userDao;
        this.journeyProvider=journeyProvider;
    }

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=b3d53b327cb6484abaf16c5e66420e6d"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9001/oauth/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }
    public PostOAuthRes createKakaoUser(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String id = element.getAsJsonObject().get("id").getAsString();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            String name  = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();

            br.close();

            if(oAuthProvider.isKakaoUser(id)==0){
                if(userProvider.checkEmail(email)==1){
                    userDao.connKakao(id,email);
                    int user_id = userDao.KakaoUserInfo(id);
                    String jwt = jwtService.createJwt(user_id);
                    int journey_id = journeyProvider.getCurrentJourneyId(user_id);
                    PostOAuthRes postOAuthRes = new PostOAuthRes(user_id,jwt,journey_id);
                    return postOAuthRes;
                }else {
                    PostOAuthReq postOAuthReq = new PostOAuthReq(id, email, name);
                    int user_id = userDao.createKakao(postOAuthReq);
                    String jwt = jwtService.createJwt(user_id);
                    int journey_id = journeyProvider.getCurrentJourneyId(user_id);
                    PostOAuthRes postOAuthRes = new PostOAuthRes(user_id, jwt,journey_id);
                    return postOAuthRes;
                }
            }
            else {
                int user_id = userDao.KakaoUserInfo(id);
                String jwt = jwtService.createJwt(user_id);
                int journey_id = journeyProvider.getCurrentJourneyId(user_id);
                PostOAuthRes postOAuthRes = new PostOAuthRes(user_id,jwt,journey_id);
                return postOAuthRes;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}
