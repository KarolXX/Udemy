ALTER TABLE users ADD COLUMN salt VARCHAR(100);
ALTER TABLE authors ADD COLUMN salt VARCHAR(100);