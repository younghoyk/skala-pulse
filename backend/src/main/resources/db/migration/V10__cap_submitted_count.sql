-- V9가 total_count를 실제 가입 인원으로 낮추면서(20 -> 1), 이미 끝난 과정에 "설문 완료"로
-- 미리 심어뒀던 submitted_count(=20)가 새 total_count보다 커지는 반들이 생겼다
-- ("제출 20명 / 1명" 같은 말이 안 되는 표시). 제출자는 실제 인원을 넘을 수 없으니 맞춰준다.
UPDATE classes SET submitted_count = total_count WHERE submitted_count > total_count;
