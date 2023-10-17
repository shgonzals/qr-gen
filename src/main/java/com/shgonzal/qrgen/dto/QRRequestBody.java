package com.shgonzal.qrgen.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class QRRequestBody {

	@NotNull
	@Size(max = 500)
	private String content;

	@NotNull
	private int[] rgb;

	@NotNull
	private int type;
}
