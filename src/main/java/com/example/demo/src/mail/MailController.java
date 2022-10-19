package com.example.demo.src.mail;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.mail.model.MailAuthReq;
import com.example.demo.src.mail.model.PostMailreq;
import com.example.demo.utils.ValidationRegex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.EMPTY_CODE;

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




