package Obsidian.demo.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import Obsidian.demo.apiPayload.code.BaseErrorCode;
import Obsidian.demo.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// For test
	TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

	// 가장 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	MARKDOWN_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "MARKDOWN404", "요청한 마크다운 파일을 찾을 수 없습니다."),
	MARKDOWN_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5001", "마크다운 파일을 읽는 도중 오류가 발생했습니다."),
	MARKDOWN_CONVERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5002", "마크다운을 HTML로 변환하는 도중 오류가 발생했습니다."),
	HTML_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5003", "변환된 HTML 파일을 저장하는 도중 오류가 발생했습니다."),
	DIRECTORY_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5004", "배포 경로 디렉토리를 생성하는 데 실패했습니다."),
	PUBLIC_DIRECTORY_CLEAR_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5005", "배포 경로(publicPath)를 정리하는 도중 오류가 발생했습니다."),
	MARKDOWN_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MARKDOWN5006", "마크다운 파일 저장 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDTO getReason() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}


	@Override
	public ErrorReasonDTO getReasonHttpStatus() {
		return ErrorReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build();
	}
}
