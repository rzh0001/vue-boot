package org.jeecg.test;

import java.text.MessageFormat;

public class LocalTest {
	public static void main(String[] args) {
		String msg = MessageFormat.format("=====>{0},,,,,{1}", "asdf", "wwwww");
		System.out.println(msg);
	}
}
