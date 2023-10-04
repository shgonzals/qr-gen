package com.shgonzal.qrgen.services;

import org.springframework.stereotype.Service;


public interface QRGeneratorService {

     byte[] generateQrCode(String text);
}
