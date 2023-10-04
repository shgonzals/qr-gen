package com.shgonzal.qrgen.services.impl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.shgonzal.qrgen.services.QRGeneratorService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;


@Service
public class QRGeneratorServiceImpl implements QRGeneratorService {

    @Override
    public byte[] generateQrCode(String text) {

        // Genera el código QR
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream outputStream = null;
        try {
            bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

            // Convierte el código QR en un arreglo de bytes
            outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return outputStream.toByteArray();
    }
}
