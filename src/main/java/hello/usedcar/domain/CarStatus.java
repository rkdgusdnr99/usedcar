package hello.usedcar.domain;

public enum CarStatus {

    SALE("판매중"),
    SOLD("판매완료");

    private final String description;

    CarStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
