
-- SQLite Database Schema for Library Service Optimizer
PRAGMA foreign_keys = ON;

-- Clean existing schema if running script repeatedly
DROP TABLE IF EXISTS algorithm_parameters;
DROP TABLE IF EXISTS audit_events;
DROP TABLE IF EXISTS algorithm_runs;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS issue_logs;
DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS roads;
DROP TABLE IF EXISTS locations;

-- 1. locations
CREATE TABLE IF NOT EXISTS locations (
    location_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    type            TEXT NOT NULL CHECK (type IN ('SHELF','DESK','ROOM','ENTRANCE','STAFF_ROOM')),
    latitude        REAL,
    longitude       REAL
);

-- 2. roads
CREATE TABLE IF NOT EXISTS roads (
    road_id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    from_location_id        INTEGER NOT NULL,
    to_location_id          INTEGER NOT NULL,
    distance                REAL NOT NULL,
    travel_time             REAL NOT NULL,
    road_condition_weight   REAL NOT NULL DEFAULT 1.0,
    FOREIGN KEY (from_location_id) REFERENCES locations(location_id) ON DELETE CASCADE,
    FOREIGN KEY (to_location_id)   REFERENCES locations(location_id) ON DELETE CASCADE
);

-- 3. members
CREATE TABLE IF NOT EXISTS members (
    member_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    index_number      TEXT UNIQUE NOT NULL,
    name              TEXT NOT NULL,
    membership_type   TEXT NOT NULL CHECK (membership_type IN ('STUDENT','STAFF','FACULTY')),
    registered_date   TEXT NOT NULL
);

-- 4. books
CREATE TABLE IF NOT EXISTS books (
    book_id             INTEGER PRIMARY KEY AUTOINCREMENT,
    isbn                TEXT,
    title               TEXT NOT NULL,
    author              TEXT NOT NULL,
    category            TEXT NOT NULL,
    shelf_location_id   INTEGER,
    total_copies        INTEGER NOT NULL DEFAULT 1,
    available_copies    INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (shelf_location_id) REFERENCES locations(location_id) ON DELETE SET NULL
);

-- 5. service_requests
CREATE TABLE IF NOT EXISTS service_requests (
    request_id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id                  INTEGER NOT NULL,
    book_id                    INTEGER NOT NULL,
    source_location_id         INTEGER,
    destination_location_id    INTEGER,
    category                   TEXT NOT NULL CHECK (category IN ('BORROW','RETURN','RESERVE','RENEW')),
    urgency                    INTEGER NOT NULL,
    time_submitted              TEXT NOT NULL,
    deadline                   TEXT,
    status                      TEXT NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING','IN_PROGRESS','FULFILLED','CANCELLED')),
    FOREIGN KEY (member_id)                REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id)                  REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (source_location_id)       REFERENCES locations(location_id) ON DELETE SET NULL,
    FOREIGN KEY (destination_location_id)  REFERENCES locations(location_id) ON DELETE SET NULL
);

-- 6. issue_logs
CREATE TABLE IF NOT EXISTS issue_logs (
    issue_log_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    request_id      INTEGER NOT NULL,
    book_id         INTEGER NOT NULL,
    member_id       INTEGER NOT NULL,
    issue_date      TEXT NOT NULL,
    due_date        TEXT NOT NULL,
    return_date     TEXT,
    fine_amount     REAL DEFAULT 0.0,
    FOREIGN KEY (request_id) REFERENCES service_requests(request_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id)    REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id)  REFERENCES members(member_id) ON DELETE CASCADE
);

-- 7. resources
CREATE TABLE IF NOT EXISTS resources (
    resource_id            INTEGER PRIMARY KEY AUTOINCREMENT,
    type                   TEXT NOT NULL CHECK (type IN ('STAFF','CART','KIOSK')),
    home_location_id       INTEGER,
    capacity               INTEGER NOT NULL DEFAULT 1,
    availability_status    TEXT NOT NULL CHECK (availability_status IN ('AVAILABLE','BUSY','OFFLINE')),
    FOREIGN KEY (home_location_id) REFERENCES locations(location_id) ON DELETE SET NULL
);

-- 8. algorithm_runs
CREATE TABLE IF NOT EXISTS algorithm_runs (
    run_id           INTEGER PRIMARY KEY AUTOINCREMENT,
    algorithm_name   TEXT NOT NULL,
    input_size       INTEGER NOT NULL,
    time_ns          INTEGER NOT NULL,
    memory_kb        REAL,
    date_run         TEXT NOT NULL
);

-- 9. audit_events
CREATE TABLE IF NOT EXISTS audit_events (
    event_id           INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type         TEXT NOT NULL CHECK (event_type IN ('ISSUE','RETURN','REQUEST_CREATED','REQUEST_CANCELLED','UNDO')),
    entity_type        TEXT NOT NULL,
    entity_id          INTEGER NOT NULL,
    performed_by       TEXT,
    event_details       TEXT,
    event_timestamp    TEXT NOT NULL,
    is_undone          INTEGER NOT NULL DEFAULT 0 CHECK (is_undone IN (0,1))
);

-- 10. algorithm_parameters
CREATE TABLE IF NOT EXISTS algorithm_parameters (
    param_id               INTEGER PRIMARY KEY AUTOINCREMENT,
    member_index_number    TEXT NOT NULL,
    param_name             TEXT NOT NULL,
    derived_value          REAL NOT NULL,
    derivation_note        TEXT NOT NULL,
    FOREIGN KEY (member_index_number) REFERENCES members(index_number) ON DELETE CASCADE
);

-- Indexes on foreign keys and lookup columns
CREATE INDEX IF NOT EXISTS idx_roads_from            ON roads(from_location_id);
CREATE INDEX IF NOT EXISTS idx_roads_to              ON roads(to_location_id);
CREATE INDEX IF NOT EXISTS idx_books_shelf           ON books(shelf_location_id);
CREATE INDEX IF NOT EXISTS idx_books_category        ON books(category);
CREATE INDEX IF NOT EXISTS idx_requests_member       ON service_requests(member_id);
CREATE INDEX IF NOT EXISTS idx_requests_book         ON service_requests(book_id);
CREATE INDEX IF NOT EXISTS idx_requests_status       ON service_requests(status);
CREATE INDEX IF NOT EXISTS idx_requests_source       ON service_requests(source_location_id);
CREATE INDEX IF NOT EXISTS idx_requests_destination  ON service_requests(destination_location_id);
CREATE INDEX IF NOT EXISTS idx_issuelogs_request     ON issue_logs(request_id);
CREATE INDEX IF NOT EXISTS idx_issuelogs_book        ON issue_logs(book_id);
CREATE INDEX IF NOT EXISTS idx_issuelogs_member      ON issue_logs(member_id);
CREATE INDEX IF NOT EXISTS idx_resources_home        ON resources(home_location_id);
CREATE INDEX IF NOT EXISTS idx_auditevents_entity    ON audit_events(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_algoparams_member     ON algorithm_parameters(member_index_number);

