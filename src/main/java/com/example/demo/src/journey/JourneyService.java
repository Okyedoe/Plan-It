package com.example.demo.src.journey;
import com.example.demo.src.journey.model.PostJourneyReq;
import com.example.demo.src.journey.model.PostJourneyRes;
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
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class JourneyService {
    private final JourneyDao journeyDao;
    private final JourneyProvider journeyProvider;
    private final JwtService jwtService;


    @Autowired
    public JourneyService(JourneyDao journeyDao, JourneyProvider journeyProvider, JwtService jwtService) {
        this.journeyDao = journeyDao;
        this.journeyProvider = journeyProvider;
        this.jwtService = jwtService;

    }

    //여정 생성 api
    @Transactional
    public PostJourneyRes createJourney (PostJourneyReq postJourneyReq) throws BaseException
    {
        try{
            return journeyDao.createJourney(postJourneyReq);

        }catch (Exception exception)
        {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }




}
