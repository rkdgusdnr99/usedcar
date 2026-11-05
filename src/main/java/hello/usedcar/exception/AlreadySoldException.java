package hello.usedcar.exception;

public class AlreadySoldException extends RuntimeException {

    public AlreadySoldException(Long carId) {
        super("이미 판매된 차량입니다. id=" + carId);
    }
}
