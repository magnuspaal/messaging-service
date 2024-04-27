package com.magnuspaal.messagingservice.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.magnuspaal.messagingservice.chat.Chat;
import com.magnuspaal.messagingservice.image.dto.AESEncryptionData;
import com.magnuspaal.messagingservice.image.dto.CompressedImage;
import com.magnuspaal.messagingservice.utils.AESUtil;
import com.magnuspaal.messagingservice.utils.RandomStringGenerator;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Objects;

public class ImageUtils {

  public static AESEncryptionData getSymmetricalEncryptionData() {
    try {
      SecretKey key = AESUtil.generateKey();
      IvParameterSpec iv = AESUtil.generateIv();
      return new AESEncryptionData(key, iv);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] encryptImage(byte[] bytes, AESEncryptionData encryptionData) {
    try {
      return AESUtil.encrypt(bytes, encryptionData.key(), encryptionData.iv());
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
             InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    }
  }

  public static BufferedImage compressImage(MultipartFile file) throws ImageProcessingException, IOException {
    Scalr.Rotation rotation = getImageRotation(file);

    BufferedImage inputImage = ImageIO.read(file.getInputStream());

    if (rotation != null) {
      inputImage = Scalr.rotate(inputImage, rotation, Scalr.OP_ANTIALIAS);
    }

    return Scalr.resize(inputImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 500);
  }

  public static String getImageFilename() {
    return RandomStringGenerator.getRandomString() + ".jpg";
  }

  public static int getImageRatio(BufferedImage image) {
    return Math.round((float) image.getWidth() / image.getHeight() * 100000);
  }

  public static byte[] getImageBytes(BufferedImage image, String imageType) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, imageType, baos);
    return baos.toByteArray();
  }

  private static Scalr.Rotation getImageRotation(MultipartFile file) throws IOException, ImageProcessingException {
    Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());

    int orientation;

    try {
      ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
      if (exifIFD0 != null) {
        orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
      } else {
        return null;
      }
    } catch (MetadataException exception) {
      return null;
    }

    switch (orientation) {
      case 6 -> { return Scalr.Rotation.CW_90; }
      case 3 -> { return Scalr.Rotation.CW_180; }
      case 8 -> { return Scalr.Rotation.CW_270; }
      default -> { return null; }
    }
  }

  public static CompressedImage processImage(MultipartFile image, Chat chat) {
    try {
      BufferedImage bufferedImage = compressImage(image);
      String filename = getImageFilename();
      int imageRatio = getImageRatio(bufferedImage);

      String imageType = Objects.requireNonNull(image.getContentType()).split("/")[1];
      byte[] compressedImage = getImageBytes(bufferedImage, imageType);

      return new CompressedImage(filename, compressedImage, imageRatio);
    } catch (ImageProcessingException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
