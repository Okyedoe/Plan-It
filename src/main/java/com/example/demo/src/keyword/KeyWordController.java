package com.example.demo.src.keyword;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.diary.DiaryProvider;
import com.example.demo.src.diary.DiaryService;
import com.example.demo.src.journey.JourneyDao;
import com.example.demo.src.journey.JourneyProvider;
import com.example.demo.src.keyword.model.GetRandomKeyWordRes;
import com.example.demo.src.planet.PlanetProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/keyword")
public class KeyWordController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final KeyWordProvider keyWordProvider;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final JourneyProvider journeyProvider;

    public KeyWordController(KeyWordProvider keyWordProvider, JwtService jwtService, JourneyProvider journeyProvider) {
        this.jwtService = jwtService;
        this.keyWordProvider = keyWordProvider;
        this.journeyProvider =journeyProvider;
    }

    @ResponseBody
    @GetMapping("/{journey_id}")
    public BaseResponse<GetRandomKeyWordRes> getKeyWord(@PathVariable("journey_id") int journey_id) throws BaseException {
        try {
            //jwt에서 idx 추출.
            int user_id = journeyProvider.getUserIdByJourneyId(journey_id);
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (user_id != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            return new BaseResponse<>(keyWordProvider.getKeyWord(journey_id));

        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }

    }
}