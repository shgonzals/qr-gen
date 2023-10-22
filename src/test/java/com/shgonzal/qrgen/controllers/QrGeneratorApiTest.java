package com.shgonzal.qrgen.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shgonzal.qrgen.commons.Utils;
import com.shgonzal.qrgen.dto.QRRequestBody;
import com.shgonzal.qrgen.services.QRGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class QrGeneratorApiTest {

    @InjectMocks
    private QRGeneratorApi qrGeneratorApi;

    @Mock
    @Autowired
    private QRGeneratorService qrGeneratorService;

    @Test
    public void testGenerateQrCodeByType_OK() {
        QRRequestBody requestBody = QRRequestBody.builder()
                                                 .content("http://qrgen.shgonzals.es/")
                                                 .rgb(new int[]{0, 0, 0})
                                                 .type(1)
                                                 .build();

        byte[] imageBytes = Utils.imageToByteArray("src/main/resources/static", "QR.png");

        when(qrGeneratorService.generateQrCode(requestBody.getContent(), requestBody.getRgb()))
                .thenReturn(imageBytes);

        ResponseEntity<byte[]> response = qrGeneratorApi.generateQrCodeByType(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    public void testGenerateQrCodeByType_KO() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        QRRequestBody requestBody = QRRequestBody.builder()
                                                 .content("http://qrgen.shgonzals.es/")
                                                 .rgb(new int[]{255, 255, 256})
                                                 .type(1)
                                                 .build();


        // Configura el controlador para realizar pruebas
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(qrGeneratorApi).build();

        // Crea una solicitud con datos de entrada inválidos
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // Realiza la solicitud POST al endpoint /generateQR
        mockMvc.perform(MockMvcRequestBuilders.post("/generateQR")
                                              .content(requestBodyJson)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .accept(MediaType.IMAGE_PNG))
               .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
