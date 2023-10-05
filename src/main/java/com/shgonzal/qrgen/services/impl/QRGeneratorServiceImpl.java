package com.shgonzal.qrgen.services.impl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.shgonzal.qrgen.services.QRGeneratorService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;


@Service
public class QRGeneratorServiceImpl implements QRGeneratorService {

    @Override
    public byte[] generateQrCode(String text) {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream outputStream = null;
        try {

            bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            outputStream = new ByteArrayOutputStream();

            //TODO: Implementar un selector RGB
            Color offColor = Color.WHITE;
            Color customColor = new Color(255, 51, 178);

            //background and logo
            //https://aboullaite.me/generate-qrcode-with-logo-image-using-zxing/

            MatrixToImageConfig config = new MatrixToImageConfig(customColor.getRGB(), offColor.getRGB());

            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream, config);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return outputStream.toByteArray();
    }
}
