package com.shgonzal.qrgen.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
public class QRRequestBody {

	public QRRequestBody(String content, int[] rgb, int type) {
		this.content = content;
		this.rgb = rgb;
		this.type = type;
	}

	@NotNull
	@Size(max = 500)
	private String content;

	@NotNull
	private int[] rgb;

	@NotNull
	private int type;
}
