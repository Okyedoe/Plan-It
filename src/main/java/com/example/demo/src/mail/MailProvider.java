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
public class MailProvider {
    private final MailDao mailDao;

    private final RedisUtil redisUtil; //redis 관련

    @Autowired
    public MailProvider(MailDao mailDao,RedisUtil redisUtil )
    {
        this.mailDao = mailDao;
        this.redisUtil = redisUtil;
    }



    public String checkCode(String code) throws BaseException
    {
        try{
            return redisUtil.getData(code);
        }catch (Exception e )
        {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}
