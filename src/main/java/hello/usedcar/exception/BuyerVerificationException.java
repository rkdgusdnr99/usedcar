package hello.usedcar.exception;

/**
 * 구매자 본인인증 실패. 일부러 체크 예외(Exception 상속)로 만들었다.
 * → 스프링 트랜잭션은 체크 예외에서 기본적으로 롤백하지 않는다! (8강)
 */
public class BuyerVerificationException extends Exception {

    public BuyerVerificationException(String message) {
        super(message);
    }
}