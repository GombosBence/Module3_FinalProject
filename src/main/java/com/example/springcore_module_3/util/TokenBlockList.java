package com.example.springcore_module_3.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TokenBlockList {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    private final JwtGenerator jwtGenerator;

    public TokenBlockList(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    public boolean isRevoked(String token) {
        return revokedTokens.contains(token);
    }

    public int size(){
        return revokedTokens.size();
    }

    @Scheduled(fixedRate = 60000)
    public void pruneTokens() {

        Iterator<String> iterator = revokedTokens.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            String token = iterator.next();
            try{
                jwtGenerator.extractClaims(token);
            }catch(JwtException e){
                iterator.remove();
                count++;
            }
        }
        if(count > 0) {
            log.info("Pruned {} tokens", count);
        }
    }
}
