package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.report.model.GetTodayInfoREs;
import com.example.demo.src.report.model.ReportRes;
import com.example.demo.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;
import static com.example.demo.config.BaseResponseStatus.NOT_EXISTS_DATE_DATA;

@Api(tags = "리포트 api")
@RestController
@RequestMapping("/report")
public class ReportController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final ReportProvider reportProvider;
    @Autowired
    private final JwtService jwtService;

    public ReportController(ReportProvider reportProvider, JwtService jwtService) {
        this.reportProvider = reportProvider;
        this.jwtService = jwtService;
    }

    @ApiOperation(value = "달성률보는 api", notes = "유저번호를 받아와 달성률을 보여줍니다.")

    @ApiResponses(
        {
            @ApiResponse(responseCode = "200", description = "코드200은 사용되지않습니다!"),
            @ApiResponse(responseCode = "1000", description = "요청에 성공하였습니다."),
            @ApiResponse(responseCode = "4000", description = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(responseCode = "4001", description = "서버와의 연결에 실패하였습니다."),
            @ApiResponse(responseCode = "2001", description = "JWT를 입력해주세요."),
            @ApiResponse(responseCode = "2002", description = "유효하지 않은 JWT입니다."),
            @ApiResponse(responseCode = "2003", description = "권한이 없는 유저의 접근입니다.")
        }
    )
    @ApiImplicitParams(

        {
            @ApiImplicitParam(name = "user_id", value = "유저아이디")
        }

    )
    @Transactional
    @ResponseBody
    @GetMapping("/{user_id}")
    public BaseResponse<ReportRes> getReport(@PathVariable("user_id") int user_id)
        throws BaseException {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            ReportRes reportRes = reportProvider.getReport(user_id);
            return new BaseResponse<>(reportRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }


    @ResponseBody
    @GetMapping("/today/{user_id}")
    public BaseResponse<GetTodayInfoREs> getTodayInfo(@PathVariable("user_id") int user_id) {



        try {


            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //오늘 데이터가 있는지 체크
            if (reportProvider.checkDataExist(user_id) == 0) {
                return new BaseResponse<>(NOT_EXISTS_DATE_DATA);
            }

            GetTodayInfoREs getTodayInfoREs = reportProvider.getTodayData(user_id);
            return new BaseResponse<>(getTodayInfoREs);




        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


    }
}