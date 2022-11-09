package com.example.demo.src.planet;
import com.example.demo.src.planet.model.GetDetailedInfoRes;
import com.example.demo.src.planet.model.GetPlanetsRes;
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
public class PlanetProvider {
    private final PlanetDao planetDao;
    private final JwtService jwtService;

    @Autowired
    public PlanetProvider(PlanetDao planetDao , JwtService jwtService)
    {
        this.planetDao = planetDao;
        this.jwtService = jwtService;

    }

    //여정아이디로 유저아이디 가져오기.
    public int getUser_id (int journey_id) throws BaseException
    {
        try{
            return planetDao.getUser_id(journey_id);

        }catch (Exception exception)
        {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //행성들가져오기
    public List<GetPlanetsRes> getPlanets (int journey_id) throws BaseException
    {
        try{
            return planetDao.getPlanets(journey_id);

        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //행성세부내용 가져오기
    public GetDetailedInfoRes getDetailedInfo (int planet_id) throws BaseException
    {
        try{
            return planetDao.getDetailedInfo(planet_id);

        }catch (Exception e )
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //행성이 삭제된건지 체크
    public int checkPlanet (int planet_id) throws BaseException
    {
        try{
            return planetDao.checkPlanet(planet_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new BaseException(DATABASE_ERROR);
        }
    }

    //해당 여정이 끝난건지 체크
    public int checkJourney (int journey_id) throws BaseException
    {
        try{
            return planetDao.checkJourney(journey_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new BaseException(DATABASE_ERROR);
        }
    }
    //행성 이름 중복 체크
    public int checkPlanetExist (String planet_name) throws BaseException
    {
        try{
            return planetDao.checkPlanetExist(planet_name);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new BaseException(DATABASE_ERROR);
        }
    }
    //해당없음 행성을 가져온다.
    public int getNotBelongPlanetId (int user_id) throws BaseException
    {
        try{
            return planetDao.getNotBelongPlanetId(user_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw  new BaseException(DATABASE_ERROR);
        }
    }

    public int checkColorExist(String color) throws BaseException{
        try {
            return planetDao.checkColorExist(color);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }






}
