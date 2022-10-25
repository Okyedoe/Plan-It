package com.example.demo.src.planet;

import com.example.demo.src.planet.model.GetPlanetsRes;
import com.example.demo.src.planet.model.PostNewPlanetReq;
import com.example.demo.src.planet.model.PostNewPlanetRes;
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


    @Autowired
    public PlanetService(PlanetDao planetDao, PlanetProvider planetProvider, JwtService jwtService) {
        this.planetDao = planetDao;
        this.planetProvider = planetProvider;
        this.jwtService = jwtService;

    }


    //새 행성 추가
    @Transactional
    public PostNewPlanetRes createNewPlanet(PostNewPlanetReq postNewPlanetReq , int journey_id) throws BaseException
    {
        try{
            return planetDao.createNewPlanet(postNewPlanetReq,journey_id);

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






}
