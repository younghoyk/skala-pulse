-- V11이 total_count를 1 -> 30으로 올리면서, 이미 끝난 반들의 submitted_count(V10에서
-- 1로 맞춰뒀던 값)가 새 총원 대비 "1/30"처럼 이제 막 시작한 것처럼 보이게 됐다.
-- "이미 끝난 수업은 설문 완료로 본다"는 원래 취지(V2 시드 당시 사용자 지시)를
-- 새 총원 기준으로 다시 맞춘다 — 아직 안 끝난 반은 그대로 0(정직하게 미제출).
UPDATE classes SET submitted_count = total_count WHERE end_date < CURRENT_DATE;
