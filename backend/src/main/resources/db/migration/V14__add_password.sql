-- 진짜 이메일+비밀번호 로그인으로 전환 — 원클릭 데모 로그인을 걷어낸다.
-- 기존 실제 회원 185명(학생 150 + 교수자 34 + 관리자 1)은 비밀번호가 없었으니
-- 전부 같은 임시 기본 비밀번호(skala1234!)의 bcrypt 해시로 채운 뒤 NOT NULL로 잠근다.
-- 이 해시는 BCryptPasswordEncoder로 직접 생성하고 matches()로 검증까지 확인한 값이다.
ALTER TABLE members ADD COLUMN password_hash VARCHAR(60);
ALTER TABLE pending_signups ADD COLUMN password_hash VARCHAR(60);

UPDATE members SET password_hash = '$2a$10$d5f/M0wqKou1K5Gq1LsrLeVZAmwFSIU3Fv3lWY9pmqfFIt/qGe60q';
-- 이 마이그레이션 이전에 신청된, 아직 승인 안 된 가입 요청도 같은 기본 비밀번호로
-- 채워둔다 — 안 그러면 나중에 승인할 때 members.password_hash NOT NULL을 못 채워서 깨진다.
UPDATE pending_signups SET password_hash = '$2a$10$d5f/M0wqKou1K5Gq1LsrLeVZAmwFSIU3Fv3lWY9pmqfFIt/qGe60q'
WHERE password_hash IS NULL;

ALTER TABLE members ALTER COLUMN password_hash SET NOT NULL;
