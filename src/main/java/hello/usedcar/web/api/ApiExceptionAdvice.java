package hello.usedcar.web.api;

import hello.usedcar.exception.CarNotFoundException;
import hello.usedcar.exception.DuplicateCarNumberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * hello.usedcar.web.api 패키지의 컨트롤러(API)에서 던진 예외만 여기서 JSON으로 바꾼다.
 * 화면 컨트롤러의 예외는 건드리지 않는다 → 스프링 부트의 오류 페이지로 간다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "hello.usedcar.web.api") // @RestControllerAdvice = 여러 컨트롤러의 @ExceptionHandler를 한곳에 모은 것 + @ResponseBody, 컨트롤러마다 try-catch 할 필요 없음
@RequiredArgsConstructor
public class ApiExceptionAdvice {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse notFound(CarNotFoundException e) {
        return new ErrorResponse("NOT_FOUND", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse duplicate(DuplicateCarNumberException e) {
        return new ErrorResponse("DUPLICATE", e.getMessage());
    }

    // @Valid 검증 실패
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse invalid(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldErrorDetail> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldErrorDetail(
                        fe.getField(),
                        messageSource.getMessage(fe, LocaleContextHolder.getLocale()))) // messages.properties 재사용
                .toList();
        return new ErrorResponse("VALIDATION", "입력값을 확인해주세요.", errors);
    }

    // JSON 문법이 틀렸거나, 숫자 칸에 문자가 오는 등 객체로 바꿀 수 없는 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse unreadable(HttpMessageNotReadableException e) {
        return new ErrorResponse("BAD_JSON", "요청 본문(JSON)을 읽을 수 없습니다.");
    }

    // 위에서 못 잡은 나머지 전부
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse unexpected(Exception e) {
        log.error("[API 처리 중 예외]", e);
        return new ErrorResponse("INTERNAL", "서버 오류가 발생했습니다.");
    }
}

