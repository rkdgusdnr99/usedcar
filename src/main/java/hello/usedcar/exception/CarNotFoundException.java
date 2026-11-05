package hello.usedcar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 화면 요청에서 이 예외가 밖으로 나가면 404로 바꿔서 오류 페이지(error/404.html)를 보여준다
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(Long carId) {
        super("차량을 찾을 수 없습니다. id=" + carId);
    }
}

