package com.example.demo.src.planet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/planets")
public class PlanetController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PlanetProvider planetProvider;
    @Autowired
    private final PlanetService planetService;
    @Autowired
    private final JwtService jwtService;

    public PlanetController (PlanetProvider planetProvider ,PlanetService planetService ,JwtService jwtService )
    {
        this.planetProvider = planetProvider;
        this.planetService = planetService;
        this.jwtService = jwtService;
    }


    /**
     *
     * */



}
