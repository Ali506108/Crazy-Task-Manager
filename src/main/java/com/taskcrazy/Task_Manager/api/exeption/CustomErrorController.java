package com.taskcrazy.Task_Manager.api.exeption;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
@RequiredArgsConstructor
@Controller
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    // Declare as final so that Lombok's @RequiredArgsConstructor injects it
    private final ErrorAttributes errorAttributes;

    @RequestMapping(PATH)
    public ResponseEntity<ErrorDTO> error(WebRequest webRequest) {
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE)
        );

        Integer status = (Integer) attributes.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        String error = (String) attributes.getOrDefault("error", "Internal Server Error");
        String message = (String) attributes.getOrDefault("message", "An error occurred");

        return ResponseEntity.status(status)
                .body(ErrorDTO.builder()
                        .error(error)
                        .errorDescription(message)
                        .build());

    }


}
