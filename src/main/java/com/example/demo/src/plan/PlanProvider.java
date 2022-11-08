package com.example.demo.src.plan;
import com.example.demo.src.journey.JourneyDao;
import com.example.demo.src.plan.model.GetTodayPlanRes;
import com.example.demo.src.plan.model.PatchPlanReviseRes;
import com.example.demo.src.planet.PlanetDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PlanProvider {
    private final PlanDao planDao;
    private final JwtService jwtService;

    @Autowired
    public PlanProvider(PlanDao planDao , JwtService jwtService)
    {
        this.planDao = planDao;
        this.jwtService = jwtService;

    }

    //행성아이디를 이용하여 해당 행성의 주인 ( 유저아이디)를 가져옴.
    public int getUser_id_from_planet_id (int planet_id)throws BaseException{
        try{
            return planDao.getUser_id_from_planet_id(planet_id);

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //세부계획아이디를 이용하여 해당 유저아이디를 가져옴.
    public int getUser_id_from_detailed_plan_id (int detailed_plan_id)throws BaseException{
        try{
            return planDao.getUser_id_from_detailed_plan_id(detailed_plan_id);

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //오늘 세부계획 가져옴
    public List<GetTodayPlanRes> getTodayPlans (int journey_id) throws BaseException
    {
        try{
            List<GetTodayPlanRes> getTodayPlanRes = planDao.getTodayPlans(journey_id);
            Deque<GetTodayPlanRes> dq = new LinkedList<>();
            for(GetTodayPlanRes tmp : getTodayPlanRes){
                if(planDao.getPlantName(tmp.getPlanet_id()).equals("해당없음")){
                    dq.addFirst(tmp);
                }else{
                    dq.addLast(tmp);
                }

            }

            List<GetTodayPlanRes> result = new ArrayList<>();
            for(int i = 0 ; i<getTodayPlanRes.size();i++){
                result.add(dq.pollFirst());
            }

            for(int i = 0; i<result.size();i++){
            if(planDao.getIsCompleterd(result.get(i).getDetailed_plan_id())==1){
                GetTodayPlanRes temp = new GetTodayPlanRes();
                temp = result.get(i);
                result.remove(i);
                result.add(temp);
            }
            }

            return result;


        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PatchPlanReviseRes getInfo (int detailed_plan_id) throws BaseException
    {
        try{
            return planDao.getPlanInfo(detailed_plan_id);

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }



//    //해당 행성에 같은 세부계획이 있는지 체크함.
//    public int CheckDuplicatedPlan (int planet_id,)throws BaseException{
//        try{
//            return planDao.getUser_id_from_planet_id(planet_id);
//
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

}
