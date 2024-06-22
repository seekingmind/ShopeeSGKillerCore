package com.shopee.killer.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Result {
	private int code;
	
	private String msg;
	
	private Object data;
}
