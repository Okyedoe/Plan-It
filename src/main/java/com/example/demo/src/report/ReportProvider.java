package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.report.model.ReportRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportProvider {

    @Autowired
    private final ReportDao reportDao;
    public ReportProvider(ReportDao reportDao){
        this.reportDao=reportDao;
    }

    public ReportRes getReport(int user_id) throws BaseException {

        try{
            ReportRes reportRes = reportDao.getTotalReport(user_id);
            return reportRes;
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
