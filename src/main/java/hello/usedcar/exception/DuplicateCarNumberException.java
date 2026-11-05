package hello.usedcar.exception;

public class DuplicateCarNumberException extends RuntimeException {

    public DuplicateCarNumberException(String carNumber) {
        super("이미 등록된 차량번호입니다. carNumber=" + carNumber);
    }
}
