package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.kakao.model.AccessToken;
import com.example.demo.src.kakao.model.PostOAuthRes;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    /**
     * 카카오 callback
     * [GET] /oauth/kakao/callback
     */
    @Autowired
    private final OAuthService oAuthService;
    @Autowired
    private final OAuthProvider oAuthProvider;

    public OAuthController(OAuthService oAuthService, OAuthProvider oAuthProvider){
        this.oAuthService=oAuthService;
        this.oAuthProvider=oAuthProvider;
    }
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) {
        System.out.println(code);
        System.out.println(oAuthService.getKakaoAccessToken(code));
    }







    @ResponseBody
    @PostMapping("/create/kakao")
    public BaseResponse<PostOAuthRes> kakaoLogin(@RequestBody AccessToken accessToken) throws BaseException{

            PostOAuthRes postOAuthRes = oAuthService.createKakaoUser(accessToken.getAccess_token());
            return new BaseResponse<>(postOAuthRes);

    }
}