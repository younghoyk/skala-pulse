CREATE TABLE survey (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL UNIQUE REFERENCES classes(id) ON DELETE CASCADE
);

CREATE TABLE survey_response (
    id BIGSERIAL PRIMARY KEY,
    survey_id BIGINT NOT NULL REFERENCES survey(id) ON DELETE CASCADE,
    submitted_at TIMESTAMP NOT NULL,
    answers TEXT NOT NULL
);

CREATE INDEX idx_survey_response_survey_id ON survey_response(survey_id);

-- 반 하나당 설문 하나 — 지금 있는 반 전부에 설문을 미리 만들어둔다.
INSERT INTO survey (class_id) SELECT id FROM classes;
