package com.shgonzal.qrgen.services.impl;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.shgonzal.qrgen.commons.Constants;
import com.shgonzal.qrgen.commons.Utils;
import com.shgonzal.qrgen.services.QRGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.util.HashMap;

@Service
@Slf4j
public class QRGeneratorServiceImpl implements QRGeneratorService {

    @Override
    public byte[] generateQrCode(String content, int[] colorRGB) {
        log.info("Generando QR en formato estandar...");

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream outputStream = null;
        Color color = new Color(colorRGB[0], colorRGB[1], colorRGB[2]);

        try {

            bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, Constants.SIZE, Constants.SIZE);
            outputStream = new ByteArrayOutputStream();

            Color offColor = Color.WHITE;

            //background and logo
            //https://aboullaite.me/generate-qrcode-with-logo-image-using-zxing/

            MatrixToImageConfig config = new MatrixToImageConfig(color.getRGB(), offColor.getRGB());

            MatrixToImageWriter.writeToStream(bitMatrix, Constants.FORMAT_PNG, outputStream, config);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return outputStream.toByteArray();
    }

    @Override
    public byte[] generateDottedQrCode(String content, int[] colorRGB) {
        log.info("Generando QR en formato punteado...");
        return generateQRCodeImage(content, colorRGB);
    }

    private void drawFinderPatternCircleStyle(Graphics2D graphics, int x, int y, int circleDiameter, Color rgb) {
        log.info("Modificando estilo QR...");
        final int WHITE_CIRCLE_DIAMETER = circleDiameter * 5 / 7;
        final int WHITE_CIRCLE_OFFSET = circleDiameter / 7;
        final int MIDDLE_DOT_DIAMETER = circleDiameter * 3 / 7;
        final int MIDDLE_DOT_OFFSET = circleDiameter * 2 / 7;

        graphics.setColor(rgb);
        graphics.fillOval(x, y, circleDiameter, circleDiameter);
        graphics.setColor(Color.white);
        graphics.fillOval(x + WHITE_CIRCLE_OFFSET, y + WHITE_CIRCLE_OFFSET, WHITE_CIRCLE_DIAMETER, WHITE_CIRCLE_DIAMETER);
        graphics.setColor(rgb);
        graphics.fillOval(x + MIDDLE_DOT_OFFSET, y + MIDDLE_DOT_OFFSET, MIDDLE_DOT_DIAMETER, MIDDLE_DOT_DIAMETER);
    }

    private byte[] generateQRCodeImage(String text, int[] colorRGB){
        log.info("Generando imagen QR...");

        final HashMap<EncodeHintType, Object> encodingHints = new HashMap<>();
        encodingHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        QRCode code = null;
        try {
            code = Encoder.encode(text, ErrorCorrectionLevel.H, encodingHints);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = renderQRImage(code, Constants.SIZE, Constants.SIZE, 4, colorRGB);

        try (FileOutputStream stream = new FileOutputStream(Constants.PNG_PATH)) {
            stream.write(Utils.bufferedImageToByteArray(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        deletePNG();

        return Utils.bufferedImageToByteArray(image);
    }

    private BufferedImage renderQRImage(QRCode code, int width, int height, int quietZone, int[] colorRGB) {
        log.info("Renderizando imagen QR...");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setBackground(Color.white);
        graphics.clearRect(0, 0, width, height);
        Color rgb = new Color(colorRGB[0], colorRGB[1], colorRGB[2]);
        graphics.setColor(rgb);

        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + (quietZone * 2);
        int qrHeight = inputHeight + (quietZone * 2);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;
        final int FINDER_PATTERN_SIZE = 7;
        final float CIRCLE_SCALE_DOWN_FACTOR = 21f/30f;
        int circleSize = (int) (multiple * CIRCLE_SCALE_DOWN_FACTOR);

        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
                if (input.get(inputX, inputY) == 1) {
                    if (!(inputX <= FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                            inputX >= inputWidth - FINDER_PATTERN_SIZE && inputY <= FINDER_PATTERN_SIZE ||
                            inputX <= FINDER_PATTERN_SIZE && inputY >= inputHeight - FINDER_PATTERN_SIZE)) {
                        graphics.fillOval(outputX, outputY, circleSize, circleSize);
                    }
                }
            }
        }

        int circleDiameter = multiple * FINDER_PATTERN_SIZE;
        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding, circleDiameter, rgb);
        drawFinderPatternCircleStyle(graphics, leftPadding + (inputWidth - FINDER_PATTERN_SIZE) * multiple, topPadding, circleDiameter, rgb);
        drawFinderPatternCircleStyle(graphics, leftPadding, topPadding + (inputHeight - FINDER_PATTERN_SIZE) * multiple, circleDiameter, rgb);

        return image;
    }

    private void deletePNG(){
        File fileToDelete = new File(Constants.PNG_PATH);

        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }



}
