
package com.example.demo.src.mail;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailProvider {
    private final RedisUtil redisUtil;
    @Autowired
    public MailProvider(RedisUtil redisUtil){
        this.redisUtil=redisUtil;
    }

    public String checkCode(String auth) throws BaseException{
        try{
            return redisUtil.getData(auth);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }



}
