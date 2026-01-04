package com.enterprise.document.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/css")
public class StaticController {

    @GetMapping("/bootstrap-local.css")
    public void bootstrapLocalCss(HttpServletResponse response) throws IOException {
        Resource resource = new ClassPathResource("static/css/bootstrap-local.css");
        if (!resource.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        response.setContentType("text/css;charset=UTF-8");
        try (InputStream inputStream = resource.getInputStream()) {
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }
}
