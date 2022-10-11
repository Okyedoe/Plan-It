package com.example.demo.src.plan;
import com.example.demo.src.journey.JourneyDao;
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

}
