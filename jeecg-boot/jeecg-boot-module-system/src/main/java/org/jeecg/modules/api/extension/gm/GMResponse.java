package org.jeecg.modules.api.extension.gm;

import lombok.Data;

@Data
public class GMResponse {
	private int code;
	private String msg;
	private String payurl;
}
