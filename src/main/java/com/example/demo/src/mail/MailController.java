
package com.example.demo.src.mail;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mail.model.MailAuthReq;
import com.example.demo.src.mail.model.PostMailreq;
import com.example.demo.utils.ValidationRegex;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.EMPTY_CODE;
@Api(tags = "이메일 인증관련 API")
@RestController
@RequestMapping("/mail")
public class MailController {
    @Autowired
    private final MailService mailService;
    @Autowired
    private final MailProvider mailProvider;

    public MailController(MailService mailService, MailProvider mailProvider){
        this.mailService=mailService;
        this.mailProvider = mailProvider;
    }



    @ApiOperation(value = "인증메일 보내는 api",notes = "유저 이메일을 받아와서 인증 메일을 보냅니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2015", description = "이메일을 입력해주세요."),
                    @ApiResponse(responseCode = "2016", description = "이메일 형식을 확인해주세요.")
            }
    )
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> mailAuthentication (@RequestBody PostMailreq postMailreq) throws BaseException
    {

        if(postMailreq.getEmail() == null){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }

        if(!ValidationRegex.isRegexEmail(postMailreq.getEmail())){
            return  new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }

        try{

            String verifyCodeId = mailService.sendCertificationMail(postMailreq.getEmail());

            return new BaseResponse<>(verifyCodeId);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }


    }
    @ApiOperation(value = "이메일 인증 확인하는 api",notes = "유저 이메일과 인증 코드를 확인하여 인증합니다.")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "성공"),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2032", description = "인증번호가 비어있습니다.")
            }
    )
    @ResponseBody
    @GetMapping("/auth")
    public BaseResponse<String> mailAuth(@RequestBody MailAuthReq mailAuthReq) throws BaseException{

        if(mailAuthReq.getAuth().equals(null)){
            return new BaseResponse<>(EMPTY_CODE);
        }
    try{
        String email = mailAuthReq.getEmail();
        String auth = mailAuthReq.getAuth();
        String result = "실패";
        String authEmail = mailProvider.checkCode(auth);

        if(email.equals(authEmail)){
            result = "성공";
        }
        return new BaseResponse<>(result);
    }
    catch (BaseException exception)
    {
        exception.printStackTrace();
        return new BaseResponse<>(exception.getStatus());
    }
    }

}
