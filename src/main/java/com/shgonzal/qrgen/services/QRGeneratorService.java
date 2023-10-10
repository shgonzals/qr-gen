package com.shgonzal.qrgen.services;

public interface QRGeneratorService {

     byte[] generateQrCode(String content, int[] colorRGB);

     byte[] generateDottedQrCode (String content, int[] colorRGB);
}
