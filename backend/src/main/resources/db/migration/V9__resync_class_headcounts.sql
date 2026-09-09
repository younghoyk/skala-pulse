-- V8에서 capacity를 없앴지만 기존 total_count 값(전부 20짜리 placeholder)은 그대로
-- 남아있었다 — CourseService.resyncClassHeadcounts()는 회원 승인/역할변경/삭제 같은
-- 이벤트가 있어야 도는데, 기존 데이터는 그런 이벤트 없이 이미 들어가 있었기 때문.
-- 한 번 실제 가입 인원으로 맞춰준다(이후로는 이벤트가 있을 때마다 애플리케이션이 동기화).
UPDATE classes c
SET total_count = COALESCE(
    (SELECT count(*) FROM members m WHERE m.role = 'STUDENT' AND m.class_name = c.name),
    0
);
