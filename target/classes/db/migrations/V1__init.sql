CREATE TABLE jobs (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    command             TEXT NOT NULL,
    status              TEXT NOT NULL DEFAULT 'PENDING',
    attempts            INTEGER NOT NULL DEFAULT 0,
    max_attempts        INTEGER NOT NULL DEFAULT 5,
    available_at        INTEGER NOT NULL,
    locked_at           INTEGER,
    locked_by           TEXT,
    lease_expires_at    INTEGER,
    last_error          TEXT,
    created_at          INTEGER NOT NULL,
    updated_at          INTEGER NOT NULL
);

CREATE INDEX idx_jobs_claimable ON jobs(status, available_at);

CREATE TABLE workers (
    id                  TEXT PRIMARY KEY,
    started_at          INTEGER NOT NULL,
    last_heartbeat_at   INTEGER NOT NULL,
    status              TEXT NOT NULL DEFAULT 'RUNNING'
);

CREATE TABLE config (
    key                 TEXT PRIMARY KEY,
    value               TEXT NOT NULL
);
