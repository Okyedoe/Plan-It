package com.example.demo.src.plan;

import com.example.demo.src.journey.JourneyDao;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.plan.model.PostPlanRes;
import com.example.demo.src.planet.PlanetDao;
import com.example.demo.src.planet.PlanetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PlanService {
    private final PlanDao planDao;
    private final PlanProvider planProvider;
    private final JwtService jwtService;


    @Autowired
    public PlanService(PlanDao planDao, PlanProvider planProvider, JwtService jwtService) {
        this.planDao = planDao;
        this.planProvider = planProvider;
        this.jwtService = jwtService;

    }

    //행성세부계획추가
    @Transactional
    public PostPlanRes createPlan (PostPlanReq postPlanReq, int planet_id) throws BaseException
    {
        try{
            return planDao.createPlan(postPlanReq,planet_id);
        }catch (BaseException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e2)
        {
            e2.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //세부계획 완료처리
    @Transactional
    public String completePlan (int detailed_plan_id)throws BaseException
    {
        try{
            return planDao.completePlan(detailed_plan_id);

        }
        catch (BaseException e2)
        {
            e2.printStackTrace();
            throw e2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //세부계획 삭제처리
    @Transactional
    public String deletePlan (int detailed_plan_id)throws BaseException
    {
        try{
            return planDao.deletePlan(detailed_plan_id);

        }
        catch (BaseException e2)
        {
            e2.printStackTrace();
            throw e2;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
