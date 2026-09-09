INSERT INTO members (name, email, role) VALUES
    ('데모 관리자', 'admin@skala.io', 'ADMIN'),
    ('정민지', 'minji.jung@skala.io', 'INSTRUCTOR'),
    ('최유준', 'yujun.choi@skala.io', 'INSTRUCTOR'),
    ('한소율', 'soyul.han@skala.io', 'STUDENT'),
    ('오태양', 'taeyang.oh@skala.io', 'STUDENT'),
    ('강나윤', 'nayoon.kang@skala.io', 'STUDENT'),
    ('윤지호', 'jiho.yoon@skala.io', 'STUDENT'),
    ('임서준', 'seojun.im@skala.io', 'STUDENT');

INSERT INTO pending_signups (name, email, requested_role) VALUES
    ('김도윤', 'doyoon.kim@skala.io', 'STUDENT'),
    ('이서연', 'seoyeon.lee@skala.io', 'STUDENT'),
    ('박현우', 'hyunwoo.park@skala.io', 'INSTRUCTOR');

INSERT INTO courses (name, lead_instructor_id) VALUES
    ('Vue.js 실전 프로젝트', (SELECT id FROM members WHERE email = 'minji.jung@skala.io')),
    ('데이터베이스 설계', (SELECT id FROM members WHERE email = 'yujun.choi@skala.io')),
    ('알고리즘 기초', (SELECT id FROM members WHERE email = 'yujun.choi@skala.io'));

INSERT INTO classes (course_id, name, capacity, format, lab_instructor_id, start_date, end_date, submitted_count, total_count) VALUES
    ((SELECT id FROM courses WHERE name = 'Vue.js 실전 프로젝트'), 'A반', 23, 'ZOOM',
        (SELECT id FROM members WHERE email = 'yujun.choi@skala.io'), '2026-08-24', '2026-09-17', 19, 23),
    ((SELECT id FROM courses WHERE name = 'Vue.js 실전 프로젝트'), 'B반', 22, 'DIRECT',
        NULL, '2026-08-24', '2026-09-18', 20, 22),
    ((SELECT id FROM courses WHERE name = '데이터베이스 설계'), 'A반', 20, 'ZOOM',
        (SELECT id FROM members WHERE email = 'minji.jung@skala.io'), '2026-08-31', '2026-09-10', 18, 20),
    ((SELECT id FROM courses WHERE name = '데이터베이스 설계'), 'B반', 24, 'DIRECT',
        NULL, '2026-08-31', '2026-09-11', 21, 24),
    ((SELECT id FROM courses WHERE name = '알고리즘 기초'), 'A반', 19, 'ZOOM',
        (SELECT id FROM members WHERE email = 'minji.jung@skala.io'), '2026-09-07', '2026-09-25', 17, 19),
    ((SELECT id FROM courses WHERE name = '알고리즘 기초'), 'B반', 6, 'DIRECT',
        NULL, '2026-09-07', '2026-09-26', 4, 6);
