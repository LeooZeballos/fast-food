-- Add branch_id to users table if it doesn't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS branch_id BIGINT;

-- Assign first branch to existing users (optional but avoids nulls for initial setup)
-- UPDATE users SET branch_id = (SELECT id FROM branch LIMIT 1);

-- Add foreign key constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_branch') THEN
        ALTER TABLE users ADD CONSTRAINT fk_user_branch FOREIGN KEY (branch_id) REFERENCES branch(id);
    END IF;
END $$;
