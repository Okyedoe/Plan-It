package com.example.demo.src.planet;

import com.example.demo.src.plan.PlanDao;
import com.example.demo.src.plan.model.PostPlanReq;
import com.example.demo.src.planet.model.GetPlanetsRes;
import com.example.demo.src.planet.model.PatchRevisePlanetInforReq;
import com.example.demo.src.planet.model.PostNewPlanetReq;
import com.example.demo.src.planet.model.PostNewPlanetReq.Plan_detail;
import com.example.demo.src.planet.model.PostNewPlanetRes;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PlanetService {
    private final PlanetDao planetDao;
    private final PlanetProvider planetProvider;
    private final JwtService jwtService;
    private final PlanDao planDao;


    @Autowired
    public PlanetService(PlanetDao planetDao, PlanetProvider planetProvider, JwtService jwtService,PlanDao planDao) {
        this.planetDao = planetDao;
        this.planetProvider = planetProvider;
        this.jwtService = jwtService;
        this.planDao = planDao;

    }
    //새행성추가, 여정만들기, 행성 수정, 행성 가져오기.

    //새 행성 추가
    @Transactional
    public PostNewPlanetRes createNewPlanet(PostNewPlanetReq postNewPlanetReq , int journey_id) throws BaseException
    {
        try{
            if (postNewPlanetReq.getPlan_list() == null) {
                PostNewPlanetRes postNewPlanetRes =planetDao.createNewPlanet(postNewPlanetReq,journey_id);
                List<Plan_detail> plan_list = new ArrayList<>();
                postNewPlanetRes.setDetailed_plans(plan_list);
                return postNewPlanetRes;
            }
            PostNewPlanetRes postNewPlanetRes = planetDao.createNewPlanet(postNewPlanetReq,journey_id);
            int current_planet_id = postNewPlanetRes.getPlanet_id();
            List<Plan_detail> plan_list = postNewPlanetReq.getPlan_list();
            for (Plan_detail p : plan_list) {
                String currentType = p.getType();
                String currentContent = p.getPlan_content();
                planDao.createPlan(new PostPlanReq(currentContent, currentType),current_planet_id);
            }
            postNewPlanetRes.setDetailed_plans(plan_list);
            return postNewPlanetRes;


        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //행성삭제
    @Transactional
    public String deletePlanet(int planet_id)throws BaseException
    {
        try{
            return planetDao.deletePlanet(planet_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //행성 정보 수정
    @Transactional
    public String revisePlanetInfo(int planet_id, PatchRevisePlanetInforReq patchRevisePlanetInforReq)throws BaseException
    {
        try{
            return planetDao.revisePlanetInfo(planet_id, patchRevisePlanetInforReq);

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
