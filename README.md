## API 설계 규칙
- URI는 리소스(명사), 행위는 HTTP 메서드로 표현한다.
- 컬렉션은 복수형(/cars), 하나는 식별자(/cars/{carId}), 하위 리소스는 계층(/cars/{carId}/maintenances)
- 검색 조건은 쿼리 파라미터로 받는다.
- 행위가 필요하면 리소스로 바꿔 표현한다. (구매 → POST /cars/{carId}/trades)
- 결과는 상태 코드로 알린다.
  - 200 조회·수정 / 201 등록(+ Location 헤더) / 400 검증 실패 / 404 없음 / 409 충돌(중복, 이미 판매) / 500 서버 오류
- 화면(HTML 폼)은 GET/POST만 가능해서 /add, /edit 같은 컨트롤 URI를 쓴다.

## 화면
| 메서드 | URL                | 기능
------------------------------------------------------------
| GET    | /cars              | 차량 목록 (+검색)
| GET    | /cars/{carId}      | 차량 상세
| GET    | /cars/add          | 등록 폼
| POST   | /cars/add          | 등록 처리 → 상세로 리다이렉트
| GET    | /cars/{carId}/edit | 수정 폼
| POST   | /cars/{carId}/edit | 수정 처리 → 상세로 리다이렉트

## API (JSON)
| 메서드 | URL                                    | 기능              | 성공           | 실패
------------------------------------------------------------
| GET    | /api/cars?modelName=&maxPrice=&status= | 차량 검색          | 200            | –
| GET    | /api/cars/{carId}                      | 차량 하나          | 200            | 404
| POST   | /api/cars                              | 차량 등록          | 201 + Location | 400, 409
| PATCH  | /api/cars/{carId}                      | 가격,주행거리 수정 | 200            | 400, 404
| GET    | /api/cars/{carId}/maintenances         | 정비 이력          | 200            | 404
| GET    | /api/market-prices?modelName=          | 모델별 시세        | 200            | –
| POST   | /api/cars/{carId}/trades               | 구매               | 201            | 404, 409
