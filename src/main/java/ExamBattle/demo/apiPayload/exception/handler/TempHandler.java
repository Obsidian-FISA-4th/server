package ExamBattle.demo.apiPayload.exception.handler;

import ExamBattle.demo.apiPayload.code.status.ErrorStatus;

public class TempHandler extends RuntimeException {
	public TempHandler(ErrorStatus errorStatus) {
		super(errorStatus.getMessage());
	}
}
