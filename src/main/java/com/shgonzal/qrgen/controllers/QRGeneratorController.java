package com.shgonzal.qrgen.controllers;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.shgonzal.qrgen.services.QRGeneratorService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/api/qrgen")
public class QRGeneratorController {

    @Autowired
    private QRGeneratorService qrGeneratorService;

    @GetMapping("/generateQR")
    public ResponseEntity<byte[]> generateQrCode(@RequestParam("text") @NotNull String text) {

        try {

            byte[] qrCodeBytes = qrGeneratorService.generateQrCode(text);

            // Devuelve el código QR como una respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCodeBytes.length);

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






}
