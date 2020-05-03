package org.jeecg.modules.api.extension.gm.entity;

import lombok.Data;

@Data
public class GMResponse {
	private int code;
	private String msg;
	private String payurl;
}
