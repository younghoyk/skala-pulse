-- 회원가입에 반/일련번호가 생기기 전에 시드된 데모 학생 5명 — 데모 로그인을 실제
-- 회원과 연결하려면 이 사람들도 반이 있어야 한다. 1반~5반에 한 명씩 배정.
UPDATE members SET class_name = '1반', serial_number = '01' WHERE email = 'soyul.han@skala.io';
UPDATE members SET class_name = '2반', serial_number = '02' WHERE email = 'taeyang.oh@skala.io';
UPDATE members SET class_name = '3반', serial_number = '03' WHERE email = 'nayoon.kang@skala.io';
UPDATE members SET class_name = '4반', serial_number = '04' WHERE email = 'jiho.yoon@skala.io';
UPDATE members SET class_name = '5반', serial_number = '05' WHERE email = 'seojun.im@skala.io';
