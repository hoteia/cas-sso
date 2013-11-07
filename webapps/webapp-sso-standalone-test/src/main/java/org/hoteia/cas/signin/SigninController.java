package org.hoteia.cas.signin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class SigninController {

    @Value("${cas.url}/login")
    private String casLoginUrl;

    @Value("${cas.service.url}")
    private String casServiceUrl;

    @RequestMapping(value = "signin", method = GET)
    public void signin(ModelMap model) {
        model.addAttribute("casLoginUrl", casLoginUrl);
        model.addAttribute("casServiceUrl", casServiceUrl);
    }
}
