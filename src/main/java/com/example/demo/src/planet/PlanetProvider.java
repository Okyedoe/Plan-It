package com.example.demo.src.planet;
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



}
