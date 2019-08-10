package com.duo.community.controller;

import com.duo.community.dto.AccessTokenDTO;
import com.duo.community.dto.GithubUser;
import com.duo.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    AccessTokenDTO accessTokenDTO;

    @Value(value = "${github.client.id}")
    private String client_id;

    @Value(value = "${github.client.secret}")
    private String client_secret;

    @Value(value = "${github.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           Model model) {
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(client_id);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index.html";
    }
}
