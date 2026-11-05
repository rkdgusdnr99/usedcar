package hello.usedcar.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * 모든 요청에 요청 ID를 붙이고, 처리 시간과 결과를 로그로 남긴다.
 * 요청 ID는 응답 헤더(X-Request-Id)에도 넣어서, 고객 문의가 오면 로그를 바로 찾을 수 있게 한다.
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID = "requestId";
    private static final String START_TIME = "startTime";

    // 컨트롤러 호출 전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(REQUEST_ID, requestId);
        request.setAttribute(START_TIME, System.currentTimeMillis());
        response.setHeader("X-Request-Id", requestId);

        log.info("REQUEST  [{}] {} {}", requestId, request.getMethod(), request.getRequestURI());
        return true; // false를 돌려주면 여기서 멈추고 컨트롤러를 호출하지 않는다 (로그인 체크 등에 사용)
    }

    // 뷰 렌더링까지 모두 끝난 뒤 (예외가 나도 호출된다)
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        long elapsed = System.currentTimeMillis() - (long) request.getAttribute(START_TIME);

        log.info("RESPONSE [{}] {} {} -> {} ({}ms)", requestId, request.getMethod(),
                request.getRequestURI(), response.getStatus(), elapsed);
        if (ex != null) {
            log.error("[{}] 처리 중 예외", requestId, ex);
        }
    }
}