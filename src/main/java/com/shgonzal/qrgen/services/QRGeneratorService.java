package com.shgonzal.qrgen.services;

public interface QRGeneratorService {

     byte[] generateQrCode(String text);

     byte[] generateQrCodeV2();
}
