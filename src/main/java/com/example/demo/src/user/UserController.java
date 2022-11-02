package com.example.demo.src.user;

import com.example.demo.src.mail.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
@Api(tags = "회원관리 API")
@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final MailService mailService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, MailService mailService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    /*
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try{
            if(Email == null){
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


     */
    /**
     * 마이페이지
     * [GET] /users/{user_id}
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ApiOperation(value = "마이페이지 API",notes = "유저아이디를 받아와 유저의 정보를 가져옵니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
                    @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
            }
    )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "user_id",value = "유저아이디")
            }

    )
    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<GetUserRes> getUser(@PathVariable("user_id") int user_id) {
        // Get Users
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetUserRes getUserRes = userProvider.getUser(user_id);
            return new BaseResponse<>(getUserRes);

        }   catch(BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body

    @ApiOperation(value = "일반 회원가입 API",notes = "유저의 정보를 받아와 회원가입을 진행합니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2015", description = "이메일을 입력해주세요."),
                    @ApiResponse(responseCode = "2016", description = "이메일 형식을 확인해주세요."),
                    @ApiResponse(responseCode = "2017", description = "중복된 이메일입니다."),
                    @ApiResponse(responseCode = "4011", description = "비밀번호 암호화에 실패하였습니다.")
            }
    )
    @Transactional
    @ResponseBody
    @PostMapping("/create")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */

    @ApiOperation(value = "일반 로그인 API",notes = "유저의 이메일 비밀번호를 받아와 로그인합니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2015", description = "이메일을 입력해주세요."),
                    @ApiResponse(responseCode = "2016", description = "이메일 형식을 확인해주세요."),
                    @ApiResponse(responseCode = "4012", description = "비밀번호 복호화에 실패하였습니다.")
            }
    )
    @Transactional
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if(postLoginReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        try{
            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ApiOperation(value = "유저 정보 수정 API",notes = "유저가 수정할 정보를 받아옵니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "4012", description = "비밀번호 복호화에 실패하였습니다.")
            }
    )
    @Transactional
    @ResponseBody
    @PostMapping("/modification/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @ModelAttribute PatchUserReq patchUserReq){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            userService.modifyUserName(patchUserReq);

            String result = "유저 정보 수정 성공";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ApiOperation(value = "회원탈퇴 API",notes = "유저의 id를 받아와 회원탈퇴를 합니다. ")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
                    @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
                    @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
                    @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")

            }
    )
    @ResponseBody
    @DeleteMapping("/{user_id}")
    public BaseResponse<String> deleteUser(@PathVariable("user_id") int user_id) throws BaseException{
        try{
            String result = "회원탈퇴 실패";
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user_id != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            int success = userService.deleteUser(user_id);
            if(success == 1){
                result = "회원탈퇴 성공";
            }
            return new BaseResponse<>(result);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
