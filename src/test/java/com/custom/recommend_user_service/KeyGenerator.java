package com.custom.recommend_user_service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) throws Exception {
        // 1. EC(Elliptic Curve) 알고리즘 선택
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        
        // 2. ES256에 표준으로 사용되는 P-256 (secp256r1) 곡선 설정
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        keyPairGenerator.initialize(ecSpec, new SecureRandom());
        
        // 3. 키 쌍 생성
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // 4. Base64로 인코딩하여 출력 (이 값을 설정 파일에 복사해서 사용하세요)
        System.out.println("PRIVATE_KEY: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        System.out.println("PUBLIC_KEY: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }
}
