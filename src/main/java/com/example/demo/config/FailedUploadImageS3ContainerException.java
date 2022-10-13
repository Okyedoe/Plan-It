package com.example.demo.config;

import com.example.demo.config.ApplicationException;
import org.springframework.http.HttpStatus;

public class FailedUploadImageS3ContainerException extends ApplicationException {

    public FailedUploadImageS3ContainerException() {
        super(
                GlobalExceptionList.FAIL_UPLOAD_IMAGE.getCODE(),
                GlobalExceptionList.FAIL_UPLOAD_IMAGE.getHTTPSTATUS(),
                GlobalExceptionList.FAIL_UPLOAD_IMAGE.getMESSAGE());
    }
}
