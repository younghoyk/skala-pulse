CREATE TABLE improvement (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_improvement_course_id ON improvement(course_id);
