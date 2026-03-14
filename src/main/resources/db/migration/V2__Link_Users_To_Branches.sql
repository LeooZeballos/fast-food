-- Add branch_id to users table if it doesn't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS branch_id BIGINT;

-- Add foreign key constraint if it doesn't exist
-- Using a pattern that works across Postgres and H2 (DROP then ADD)
ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_user_branch;
ALTER TABLE users ADD CONSTRAINT fk_user_branch FOREIGN KEY (branch_id) REFERENCES branch(id);
