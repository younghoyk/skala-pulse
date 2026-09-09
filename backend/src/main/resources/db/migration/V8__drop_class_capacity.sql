-- 총원(capacity)은 더 이상 관리자가 입력하는 값이 아니라 실제 회원가입 인원
-- (members.class_name 기준)으로 계산된다 — total_count 하나로 충분하다.
ALTER TABLE classes DROP COLUMN capacity;
