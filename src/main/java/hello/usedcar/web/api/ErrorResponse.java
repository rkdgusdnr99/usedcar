package hello.usedcar.web.api;

import java.util.List;

/**
 * API 오류 응답 형식. 모든 API 오류는 이 모양으로 나간다.
 * { "code": "VALIDATION", "message": "...", "errors": [ { "field": "price", "message": "..." } ] }
 */
public record ErrorResponse(String code, String message, List<FieldErrorDetail> errors) {

    public ErrorResponse(String code, String message) {
        this(code, message, List.of());
    }

    public record FieldErrorDetail(String field, String message) {
    }
}