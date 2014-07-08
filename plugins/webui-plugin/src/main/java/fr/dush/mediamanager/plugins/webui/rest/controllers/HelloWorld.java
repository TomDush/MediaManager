package fr.dush.mediamanager.plugins.webui.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api", method = {RequestMethod.GET, RequestMethod.POST})
public class HelloWorld {

    @RequestMapping("hello")
    @ResponseBody
    public String hello() {
        return "<h1>Medima REST Service</h1><p>Powered by Spring Framework</p>";
    }

}
