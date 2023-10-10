package com.shgonzal.qrgen.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QRRequestBody {

	@NotNull
	private String content;

	@NotNull
	private int[] rgb;

	@NotNull
	private int type;
}
