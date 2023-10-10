package com.shgonzal.qrgen.controllers;

import com.shgonzal.qrgen.services.QRGeneratorService;
import io.swagger.annotations.*;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(value = "QR Generator", tags = "QRGeneratorApi")
@Slf4j
public class QRGeneratorApi {

    @Autowired
    private QRGeneratorService qrGeneratorService;

    @GetMapping(value = "generateQR")
    @ApiOperation(value = "Genera un código QR", notes = "Genera un código QR a partir del texto proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Código QR generado correctamente"),
            @ApiResponse(code = 400, message = "Solicitud incorrecta"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public ResponseEntity<byte[]> generateQrCode(@ApiParam(value = "Texto para el código QR", required = true) @RequestParam("text") @NotNull String text) {

        log.info("Generando QR en formato estandar...");
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


    //@Deprecated
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "generateQRV2")
    @ApiOperation(value = "Genera un código QR en forma de puntos", notes = "Genera un código QR a partir del texto proporcionado.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Código QR generado correctamente"),
            @ApiResponse(code = 400, message = "Solicitud incorrecta"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public ResponseEntity<byte[]> generateQrCodeV2() {

        log.info("Generando QR en formato punteado...");

        try {
            byte[] qrCodeBytes = qrGeneratorService.generateQrCodeV2();

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
