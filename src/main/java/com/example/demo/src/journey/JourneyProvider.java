package com.example.demo.src.journey;
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
public class JourneyProvider {
    private final JourneyDao journeyDao;
    private final JwtService jwtService;

    @Autowired
    public JourneyProvider(JourneyDao journeyDao , JwtService jwtService)
    {
        this.journeyDao = journeyDao;
        this.jwtService = jwtService;

    }

    //활성화되어있는 유저인지체크
    public int checkUser (int user_id) throws BaseException{
        try{
            return journeyDao.checkUser(user_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getUserIdByJourneyId (int journey_id) throws BaseException
    {
        try
        {
            return journeyDao.getUserIdByJourneyId(journey_id);
        }catch (Exception e)
        {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR  );
        }
    }




}
