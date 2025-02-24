package Obsedian.demo.apiPayload.exception.handler;

import Obsedian.demo.apiPayload.code.status.ErrorStatus;

public class TempHandler extends RuntimeException {
	public TempHandler(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
	}
}
