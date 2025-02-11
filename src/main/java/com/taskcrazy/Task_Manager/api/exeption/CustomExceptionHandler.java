package com.taskcrazy.Task_Manager.api.exeption;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex ,
                                            WebRequest webRequest) throws Exception {
        log.error( "Exception during exception of apllication ",ex);

        return handleException(ex ,webRequest);
    }
}
