package com.suntrustbank.transactions.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suntrustbank.transactions.core.enums.ErrorCode;
import com.suntrustbank.transactions.core.errorhandling.exceptions.GenericErrorCodeException;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;


@Slf4j
@Getter
@Configuration
public class AESUtil {

    @Value("${spring.encryption.passphrase}")
    private String passphrase;

    @Value("${spring.encryption.salt}")
    private String salt;

    private static final String AES_ALGORITHM = "AES";
    private static final String PWH_ALGORITHM = "PBKDF2WithHmacSHA256";


    private char[] getPassphrase() {
        if (StringUtils.isBlank(passphrase)) {
            log.info("No passphrase parsed");
            return null;
        }
        log.info("Passphrase parsed");
        return passphrase.toCharArray();
    }

    // Derive AES Key from a Passphrase
    private SecretKey deriveKey() throws GenericErrorCodeException {
        try {
            PBEKeySpec spec = new PBEKeySpec(getPassphrase(), salt.getBytes(), 65536, 128);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(PWH_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, AES_ALGORITHM);
        } catch (Exception e) {
            log.info("Error Generating key:: {}", e.getMessage(), e);
            throw new GenericErrorCodeException("failed to derive key", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }

    // Encrypt data
    public String encrypt(String data) throws GenericErrorCodeException {
        SecretKey key = deriveKey();
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.info("Error Encrypting data:: {}", e.getMessage(), e);
            throw new GenericErrorCodeException("encryption failed", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }

    public String encrypt(Object data) throws GenericErrorCodeException {
        SecretKey key = deriveKey();
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(data);
            objectStream.flush();

            byte[] serializedData = byteStream.toByteArray();

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(serializedData);

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.info("Error Encrypting data:: {}", e.getMessage(), e);
            throw new GenericErrorCodeException("encryption failed", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }

    // Decrypt data
    public Object decrypt(String encryptedData) throws GenericErrorCodeException {
        SecretKey key = deriveKey();
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] serializedData = cipher.doFinal(encryptedBytes);

            ByteArrayInputStream byteStream = new ByteArrayInputStream(serializedData);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            return objectStream.readObject();
        } catch (Exception e) {
            log.info("Error Decrypting data:: {}", e.getMessage(), e);
            throw new GenericErrorCodeException("decryption failed", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }


    public boolean validatePin(String inputPin, String encryptedStoredPin) {
        try {
            String encryptedInputPin = encrypt(inputPin);
            return encryptedInputPin.equals(encryptedStoredPin);
        } catch (GenericErrorCodeException e) {
            log.info("Error Validating PIN:: {}", e.getMessage(), e);
            return false;
        }
    }
}