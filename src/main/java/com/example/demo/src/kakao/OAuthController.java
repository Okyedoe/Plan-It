package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.kakao.model.AccessToken;
import com.example.demo.src.kakao.model.PostOAuthRes;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.ACCESSTOKEN_ERROR;

@Api(tags = "카카오 로그인 API")
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
    @ApiOperation(value = "카카오 테스트용 api(신경쓰지 않으셔도 됩니다.)")
    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) {
        System.out.println(code);
        System.out.println(oAuthService.getKakaoAccessToken(code));
    }






    @ApiOperation(value = "카카오 로그인 api",notes = "액세스 토큰을 받아와서 로그인 합니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2504", description = "액세서토큰을 입력해주세요."),
            }
    )
    @ResponseBody
    @PostMapping("/create/kakao")
    public BaseResponse<PostOAuthRes> kakaoLogin(@RequestBody AccessToken accessToken) throws BaseException{
            if(accessToken.getAccess_token().equals("")){
                return new BaseResponse<>(ACCESSTOKEN_ERROR);
            }
            PostOAuthRes postOAuthRes = oAuthService.createKakaoUser(accessToken.getAccess_token());
            return new BaseResponse<>(postOAuthRes);

    }
}