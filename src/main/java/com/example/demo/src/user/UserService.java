package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.example.demo.utils.image.AwsS3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AwsS3Service awsS3Service;
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService,AwsS3Service awsS3Service) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }

    //POST
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {

        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{

            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        User user = userProvider.getUserInfo(patchUserReq.getUser_id());
        String pwd = patchUserReq.getPassword();
        String phone_num = patchUserReq.getPhone_num();
        MultipartFile image = patchUserReq.getProfile_url();

        if(!pwd.isEmpty()){
            try{
                String new_pwd = new SHA256().encrypt(pwd);
                user.setPassword(new_pwd);
            } catch (Exception ignored) {
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
            if(!phone_num.isEmpty()){
                user.setPhone_num(phone_num);
                if(!image.isEmpty()){
                    String url = awsS3Service.uploadImage(image);
                    user.setProfile_url(url);
                }
            }
            else{
                if(!image.isEmpty()){
                    String url = awsS3Service.uploadImage(image);
                    user.setProfile_url(url);
                }
            }
        }
        else{
            if(!phone_num.isEmpty()){
                user.setPhone_num(phone_num);
                if(!image.isEmpty()){
                    String url = awsS3Service.uploadImage(image);
                    user.setProfile_url(url);
                }
            }
            else{
                if(!image.isEmpty()){
                    String url = awsS3Service.uploadImage(image);
                    user.setProfile_url(url);
                }
            }
        }


        try{

            int result = userDao.modifyUserName(user);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            System.out.println(user.getUser_name()+" "+user.getPhone_num()+" "+user.getUser_id()+" "+user.getPassword()+" "+user.getProfile_url());
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteUser(int user_id) throws BaseException {
        try{
            return userDao.deleteUser(user_id);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
