package com.joaovitorsb.dscatalog.components;

import com.joaovitorsb.dscatalog.entities.User;
import com.joaovitorsb.dscatalog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        User user = userRepository.findByEmail(oAuth2Authentication.getName());
        Map<String, Object> map = new HashMap<>();
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken;

        map.put("userFirstName", user.getFirstName());
        map.put("userId", user.getId());

        token.setAdditionalInformation(map);

        return token;
    }
}
