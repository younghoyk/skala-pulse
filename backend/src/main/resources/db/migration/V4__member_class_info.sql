-- 교육생은 실제로 반(1반~5반) 하나에 소속돼있고 이미 배정된 일련번호가 있다.
-- classes 테이블은 "과정×반" 조합(170행)이라 학생의 반 소속과는 다른 단위라서,
-- 여기서는 FK가 아니라 안정적인 문자열로 둔다(교수자/관리자는 해당 없음, nullable).
ALTER TABLE members ADD COLUMN class_name VARCHAR(20);
ALTER TABLE members ADD COLUMN serial_number VARCHAR(50);

ALTER TABLE pending_signups ADD COLUMN class_name VARCHAR(20);
ALTER TABLE pending_signups ADD COLUMN serial_number VARCHAR(50);
