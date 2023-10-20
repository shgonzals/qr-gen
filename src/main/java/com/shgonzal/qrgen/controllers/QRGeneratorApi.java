package com.shgonzal.qrgen.controllers;

import com.shgonzal.qrgen.commons.Constants;
import com.shgonzal.qrgen.dto.QRRequestBody;
import com.shgonzal.qrgen.services.QRGeneratorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@Tag(name = Constants.QR_GEN_API_DESC, description = Constants.QR_GEN_API)
@Slf4j
@CrossOrigin
public class QRGeneratorApi {

    private final static String QR_API_VALUE = "Genera un código QR";
    private final static String QR_API_NOTES = "Genera un código QR a partir del texto o URL proporcionado, pasando un color en formato RGB y seleccionando un tipo de generación.";

    private final static String API_PARAM = "Parámetros del proceso: \r\n- Content: Contenido del QR. Puede ser una URL o un mensaje. (Ejemplo: www.google.es)"
                                            + "\r\n- RGB: Código de colores en RGB (Ejemplo: [0,0,0])"
                                            + "\r\n- Type: Tipo de generación del QR. Valores permitidos: 1- QR básico. 2- QR punteado (Ejemplo: 1)";
    @Autowired
    private QRGeneratorService qrGeneratorService;

    @PostMapping("/generateQR")
    @Operation(summary = QR_API_VALUE, description = QR_API_NOTES, security = @SecurityRequirement(name = "bearer-key"))
    @ApiResponse(responseCode = "200", description = Constants.MESSAGE_200,
            content = @Content(mediaType = "image/png", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "400", description = Constants.MESSAGE_400)
    @ApiResponse(responseCode = "401", description = Constants.MESSAGE_401)
    @ApiResponse(responseCode = "403", description = Constants.MESSAGE_403)
    @ApiResponse(responseCode = "404", description = Constants.MESSAGE_404)
    @ApiResponse(responseCode = "500", description = Constants.MESSAGE_500)
    public ResponseEntity<byte[]> generateQrCodeByType(@Parameter(description = API_PARAM, required = true)
                                                       @RequestBody QRRequestBody body) {

        log.info("Generando QR en formato por tipo...");
        boolean valid = validateBody(body);
        if(!valid){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try {

                byte[] qrCodeBytes = null;

                switch (body.getType()){
                    case Constants.INT_1:
                        qrCodeBytes = qrGeneratorService.generateQrCode(body.getContent(), body.getRgb());
                        break;
                    case Constants.INT_2:
                        qrCodeBytes = qrGeneratorService.generateDottedQrCode(body.getContent(), body.getRgb());
                        break;
                }

                HttpHeaders headers = new HttpHeaders();
                if(qrCodeBytes.length > 0){
                    // Devuelve el código QR como una respuesta
                    headers.setContentType(MediaType.IMAGE_PNG);
                    headers.setContentLength(qrCodeBytes.length);
                }

                log.info("Se ha generado QR en formato por tipo");
                return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    private boolean validateBody(QRRequestBody body){
       boolean valid = Boolean.TRUE;
       boolean typeValid = body.getType() == Constants.INT_1 || body.getType() == Constants.INT_2;
       boolean rgbValid = body.getRgb().length == Constants.INT_3 &&
                          Arrays.stream(body.getRgb()).allMatch(code -> code >= Constants.INT_0 && code <= Constants.INT_255);

       if(body.getContent().isEmpty() || !typeValid || !rgbValid){
           valid = Boolean.FALSE;
       }

        return valid;
    }

}
