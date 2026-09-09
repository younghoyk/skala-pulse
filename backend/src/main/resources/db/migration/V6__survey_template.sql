CREATE TABLE survey_template (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL UNIQUE REFERENCES courses(id) ON DELETE CASCADE,
    template_json TEXT NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
