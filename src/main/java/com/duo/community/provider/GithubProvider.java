package com.duo.community.provider;

import com.alibaba.fastjson.JSON;
import com.duo.community.dto.AccessTokenDTO;
import com.duo.community.dto.GithubUser;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String json = com.alibaba.fastjson.JSON.toJSONString(accessTokenDTO);
        RequestBody body = RequestBody.create(mediaType,json);
        Request request = new Request.Builder().url("https://github.com/login/oauth/access_token").post(body).build();
        try (Response response = client.newCall(request).execute()){
            String string = response.body().string();
            String[] split = string.split("&");
            String tokenStr = split[0];
            String token = tokenStr.split("=")[1];
            return token;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public GithubUser getUser(String accessToken){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.github.com/user?access_token=" + accessToken).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
