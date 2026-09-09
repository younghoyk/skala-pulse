CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN'))
);

CREATE TABLE pending_signups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    requested_role VARCHAR(20) NOT NULL CHECK (requested_role IN ('STUDENT', 'INSTRUCTOR'))
);

CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    lead_instructor_id BIGINT NOT NULL REFERENCES members(id)
);

CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    format VARCHAR(10) NOT NULL CHECK (format IN ('ZOOM', 'DIRECT')),
    lab_instructor_id BIGINT REFERENCES members(id),
    start_date DATE,
    end_date DATE,
    submitted_count INTEGER NOT NULL DEFAULT 0,
    total_count INTEGER NOT NULL
);

CREATE INDEX idx_classes_course_id ON classes(course_id);
