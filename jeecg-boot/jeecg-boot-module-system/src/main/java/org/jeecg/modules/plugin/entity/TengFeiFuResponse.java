package org.jeecg.modules.plugin.entity;

import lombok.Data;

@Data
public class TengFeiFuResponse {
	private String code;
	private String message;
	private String data;
	private String payurl;
}
