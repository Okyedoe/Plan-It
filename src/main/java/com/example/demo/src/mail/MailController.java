package com.example.demo.src.mail;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mail.model.PostCodeReq;
import com.example.demo.src.mail.model.PostMailreq;
import com.example.demo.utils.ValidationRegex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.EMPTY_CODE;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private final MailService mailService;

    @Autowired
    private final MailProvider mailProvider;

    public MailController (MailService mailService,MailProvider mailProvider)
    {
        this.mailService =mailService;
        this.mailProvider = mailProvider;
    }




    //인증메일 발송 api
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

//            mailService.sendCertificationMail(email);

            String verifyCodeId = mailService.sendCertificationMail(postMailreq.getEmail());

            return new BaseResponse<>(verifyCodeId);
        }
        catch (BaseException baseException){
            return  new BaseResponse<>(baseException.getStatus());
        }

    }

    //인증번호를 보내서 인증확인받는 api
    @ResponseBody
    @PostMapping("/auth")
    public BaseResponse<String> checkCode(@RequestBody PostCodeReq postCodeReq)
    {
        if(postCodeReq.getCode().equals(null))
        {
            return new BaseResponse<>(EMPTY_CODE);
        }
        try{
            String code = postCodeReq.getCode();
            String email = postCodeReq.getEmail();
            String email_by_redis_code = mailProvider.checkCode(code); // redis서버에서 키값(코드)를이용하여 가져온 이메일과 입력된 이메일이 같은지 체크
            String result = "실패";
            if(email.equals(email_by_redis_code))
            {
                result = "성공";
            }
            return new BaseResponse<>(result);
        }
        catch (BaseException e)
        {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }


    }


}
