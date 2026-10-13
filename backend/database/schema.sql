-- Scholarship Management System - MySQL schema
-- Spring Boot creates these tables automatically (spring.jpa.hibernate.ddl-auto=update).
-- This file is for reference, or to create the tables by hand in MySQL Workbench.

CREATE DATABASE IF NOT EXISTS scholarship_db;
USE scholarship_db;

-- Admin and student accounts
CREATE TABLE IF NOT EXISTS users (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,            -- BCrypt hash, never plain text
    role      VARCHAR(20)  NOT NULL,            -- ADMIN or STUDENT
    gender    VARCHAR(20),                      -- Female or Male
    category  VARCHAR(20),                      -- General, OBC, BC, SC, ST, EBC, Minority
    phone     VARCHAR(20),
    course    VARCHAR(255)
);

-- Scholarships managed by the admin
CREATE TABLE IF NOT EXISTS scholarship (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    provider    VARCHAR(255) NOT NULL,
    level       VARCHAR(20)  NOT NULL,          -- Diploma, UG, PG, PhD or Any
    amount      VARCHAR(255) NOT NULL,
    deadline    DATE         NOT NULL,
    gender      VARCHAR(20)  DEFAULT 'All',     -- All, Female or Male
    categories  VARCHAR(255) DEFAULT 'All',     -- All, or a list such as SC,ST,BC
    description VARCHAR(1000)
);

-- Applications: many applications belong to one student and one scholarship
CREATE TABLE IF NOT EXISTS scholarship_application (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id     BIGINT NOT NULL,
    scholarship_id BIGINT NOT NULL,
    course         VARCHAR(255),
    percentage     DOUBLE,
    annual_income  BIGINT,
    status         VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED or REJECTED
    applied_on     DATE,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (scholarship_id) REFERENCES scholarship(id)
);
