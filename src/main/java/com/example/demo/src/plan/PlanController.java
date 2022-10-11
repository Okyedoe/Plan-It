package com.example.demo.src.plan;

import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.JourneyService;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import com.example.demo.src.planet.PlanetProvider;
import com.example.demo.src.planet.PlanetService;
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
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Api(tags = "행성의 세부게획 관련 api들")
@RestController
@RequestMapping("/plans")
public class PlanController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlanProvider planProvider;
    @Autowired
    private final PlanService planService;
    @Autowired
    private final JwtService jwtService;

    public PlanController (PlanProvider planProvider ,PlanService planService ,JwtService jwtService )
    {
        this.planProvider = planProvider;
        this.planService = planService;
        this.jwtService = jwtService;
    }


    /**
     * 행성 세부계획 추가
     * */
    @Transactional
    @ResponseBody
    @PostMapping("/{planet_id}")
    public BaseResponse<PostPlanRes> createPlan (@PathVariable("planet_id")int planet_id, @RequestBody PostPlanReq postPlanReq)
    {
        //삭제된 행성인지 체크
        //행성세부계획이 같은게 있는지 체크
        //입력값이 빈값인지,잘못됬는지 체크 등등

        try{
            //입력받은 jwt로 추출한 유저아이디를 이용하여 해당 행성의 주인인지 체크하는부분.
            int user_id_jwt = jwtService.getUserIdx();
            int user_id_planet = planProvider.getUser_id_from_planet_id(planet_id);

            if(user_id_planet != user_id_jwt)
            {//jwt로 받은 유저아이디와 행성아이디로 받은 유저아이디가 다르다면
                return new BaseResponse<>(PLANET_JWT_CHECK_ERROR);
            }

            PostPlanRes postPlanRes = planService.createPlan(postPlanReq,planet_id);
            return new BaseResponse<>(postPlanRes);






        }catch (BaseException exception)
        {
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }



}
