package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.UserDao;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuthProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;

    @Autowired
    public OAuthProvider(UserDao userDao){
        this.userDao=userDao;
    }

    public int isKakaoUser(String id) throws BaseException{
    try{
        int result = userDao.isKakaoUser(id);
        return result;
    }catch(Exception exception){
        exception.printStackTrace();
        throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
    }
    }
}
