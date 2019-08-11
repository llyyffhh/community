package com.duo.community.controller;

import com.duo.community.dto.AccessTokenDTO;
import com.duo.community.dto.GithubUser;
import com.duo.community.mapper.UserMapper;
import com.duo.community.model.User;
import com.duo.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private AccessTokenDTO accessTokenDTO;

    @Autowired
    private UserMapper userMapper;

    @Value(value = "${github.client.id}")
    private String client_id;

    @Value(value = "${github.client.secret}")
    private String client_secret;

    @Value(value = "${github.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state, HttpSession session,
                           HttpServletResponse response) {
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(client_id);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null) {
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.insert(user);
            Cookie cookie = new Cookie("token",token);
            response.addCookie(cookie);

            return "redirect:/";
        } else {
            return "redirect:/";
        }
    }
}
