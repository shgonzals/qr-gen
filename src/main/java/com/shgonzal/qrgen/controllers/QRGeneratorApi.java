package com.shgonzal.qrgen.controllers;

import com.shgonzal.qrgen.commons.Constants;
import com.shgonzal.qrgen.dto.QRRequestBody;
import com.shgonzal.qrgen.services.QRGeneratorService;
import io.swagger.annotations.*;
import javax.validation.constraints.NotNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@Api(value = Constants.QR_GEN_API_DESC, tags = Constants.QR_GEN_API)
@Slf4j
public class QRGeneratorApi {

    private final static String QR_API_VALUE = "Genera un código QR";
    private final static String QR_API_NOTES = "Genera un código QR a partir del texto proporcionado, pasando un color y seleccionando una forma.";

    @Autowired
    private QRGeneratorService qrGeneratorService;

    @PostMapping(value = "generateQR")
    @ApiOperation(value = QR_API_VALUE, notes = QR_API_NOTES)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = Constants.MESSAGE_200),
            @ApiResponse(code = 400, message = Constants.MESSAGE_400),
            @ApiResponse(code = 401, message = Constants.MESSAGE_401),
            @ApiResponse(code = 403, message = Constants.MESSAGE_403),
            @ApiResponse(code = 404, message = Constants.MESSAGE_404),
            @ApiResponse(code = 500, message = Constants.MESSAGE_500)
    })
    //@Deprecated
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<byte[]> generateQrCodeByType(@ApiParam(value = "Texto para el código QR", required = true)
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
