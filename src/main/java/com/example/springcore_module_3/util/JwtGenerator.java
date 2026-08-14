package com.example.springcore_module_3.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

@Component
public class JwtGenerator {

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtGenerator(@Value("${certificate.path}") String path, @Value("${certificate.password}") char[] password,
                        @Value("{certificate.alias}") String alias) throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(new FileInputStream(path), password);

        this.privateKey = (PrivateKey) keyStore.getKey(alias, password);
        this.publicKey = keyStore.getCertificate(alias).getPublicKey();
    }

    public String createToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(privateKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
