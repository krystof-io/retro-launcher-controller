-- Add MGV columns to program table
ALTER TABLE program ADD COLUMN avg_music_score DECIMAL(3,2);
ALTER TABLE program ADD COLUMN avg_graphics_score DECIMAL(3,2);
ALTER TABLE program ADD COLUMN avg_vibes_score DECIMAL(3,2);
ALTER TABLE program ADD COLUMN total_votes INTEGER DEFAULT 0;
ALTER TABLE program ADD COLUMN mgv_index DECIMAL(5,2);
ALTER TABLE program ADD COLUMN tier VARCHAR(1);

-- Create table for individual votes
CREATE TABLE program_vote (
                              id BIGSERIAL PRIMARY KEY,
                              program_id BIGINT NOT NULL REFERENCES program(id),
                              user_id VARCHAR(255) NOT NULL,  -- References external User Service ID
                              platform_id VARCHAR(50) NOT NULL, -- e.g., 'twitch', 'discord'
                              music_score INTEGER NOT NULL CHECK (music_score BETWEEN 1 AND 5),
                              graphics_score INTEGER NOT NULL CHECK (graphics_score BETWEEN 1 AND 5),
                              vibes_score INTEGER NOT NULL CHECK (vibes_score BETWEEN 1 AND 5),
                              comment TEXT,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Each user can only vote once per program
                              UNIQUE(program_id, user_id)
);

-- Create indexes for efficient lookups
CREATE INDEX idx_program_vote_lookup ON program_vote(program_id, user_id);
CREATE INDEX idx_program_vote_scores ON program_vote(program_id, music_score, graphics_score, vibes_score);
CREATE INDEX idx_program_tier_mgv ON program(tier, mgv_index DESC);