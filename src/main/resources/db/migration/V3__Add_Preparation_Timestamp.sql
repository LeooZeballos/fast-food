-- Add preparation_start_timestamp to food_order table
ALTER TABLE food_order ADD COLUMN IF NOT EXISTS preparation_start_timestamp TIMESTAMP(6);
