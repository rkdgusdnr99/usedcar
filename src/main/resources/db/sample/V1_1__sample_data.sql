-- 샘플 데이터. local/test 프로필에서만 적용된다 (운영 DB에는 들어가지 않는다)

insert into car (car_number, manufacturer, model_name, model_year, mileage, price, status) values
    ('12가3456', '현대', '쏘나타', 2021, 42000, 2150, 'SALE'),
    ('34나5678', '현대', '쏘나타', 2019, 78000, 1590, 'SALE'),
    ('45더6789', '현대', '쏘나타', 2022, 15000, 2590, 'SALE'),
    ('56다7890', '기아', 'K5', 2022, 21000, 2480, 'SALE'),
    ('78라1234', '제네시스', 'G80', 2020, 55000, 3900, 'SALE');

insert into maintenance_history (car_id, maintained_at, description, mileage) values
    (1, '2023-03-10', '엔진오일 교환', 30000),
    (1, '2024-05-21', '브레이크 패드 교체', 38000),
    (2, '2022-11-02', '타이어 4개 교체', 60000);