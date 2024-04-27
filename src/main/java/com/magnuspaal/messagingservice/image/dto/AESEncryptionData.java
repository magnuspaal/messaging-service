package com.magnuspaal.messagingservice.image.dto;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public record AESEncryptionData(SecretKey key, IvParameterSpec iv) {
}
