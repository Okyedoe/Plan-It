
package com.example.demo.src.mail;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;


    private final RedisUtil redisUtil; //redis 관련

    @Autowired
    public MailService(JavaMailSender javaMailSender,RedisUtil redisUtil )
    {
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }




    private MimeMessage createMessage(String code, String email) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("Planet 인증 번호입니다.");
        message.setText("이메일 인증코드: "+code);

        message.setFrom("dkrak3212@naver.com"); //보내는사람.

        return  message;
    }

    public void sendMail(String code, String email) throws Exception{
        try{
            MimeMessage mimeMessage = createMessage(code, email);
            javaMailSender.send(mimeMessage);
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw   new IllegalAccessException();
        }
    }

    public String sendCertificationMail(String email)  throws BaseException {
//        if(userProvider.checkEmail(email) == 1){
//            throw new BaseException(BaseResponseStatus.DUPLICATED_EMAIL);
//        }
        try{
            String code = UUID.randomUUID().toString().substring(0, 6); //랜덤 인증번호 uuid를 이용!
            sendMail(code,email);

            redisUtil.setDataExpire(code,email,60*5L); // {key,value} 5분동안 저장.

            return  code;
        }catch (Exception exception){
            exception.printStackTrace();
            throw   new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
