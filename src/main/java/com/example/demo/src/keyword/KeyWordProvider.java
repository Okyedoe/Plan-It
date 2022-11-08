package com.example.demo.src.keyword;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.journey.JourneyDao;
import com.example.demo.src.keyword.model.GetRandomKeyWordRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class KeyWordProvider {

    private final KeyWordDao keyWordDao;
    private final JourneyDao journeyDao;
    private final UserDao userDao;
    @Autowired
    public KeyWordProvider(KeyWordDao keyWordDao, JourneyDao journeyDao, UserDao userDao) {
        this.keyWordDao = keyWordDao;
        this.journeyDao = journeyDao;
        this.userDao=userDao;
    }

    public GetRandomKeyWordRes getKeyWord(int journey_id) throws BaseException {
        try{

            List<String> keywords = keyWordDao.getKeyWordList(journey_id);
            String keyword = keywords.get((int)(Math.random()*keywords.size()));


            String name = journeyDao.getNameByJourneyId(journey_id);
            GetRandomKeyWordRes getRandomKeyWordRes = new GetRandomKeyWordRes(keyword+" "+name);

            return getRandomKeyWordRes;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }
}
