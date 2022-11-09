package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;
    private final JourneyProvider journeyProvider;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService,JourneyProvider journeyProvider) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.journeyProvider=journeyProvider;
    }
    /*
    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
                    }


     */
    @Transactional
    public GetUserRes getUser(int user_id) throws BaseException {
        try {
            GetUserRes getUserRes = userDao.getUser(user_id);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(user.getPassword().equals(encryptPwd)){
            int userIdx = user.getUser_id();
            String jwt = jwtService.createJwt(userIdx);
            int journeyId = journeyProvider.getCurrentJourneyId(user.getUser_id());
            return new PostLoginRes(userIdx,jwt,journeyId);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }


    public User getUserInfo(int userIdx) throws BaseException {
        User user = userDao.getUserInfo(userIdx);
        return user;
    }
}
