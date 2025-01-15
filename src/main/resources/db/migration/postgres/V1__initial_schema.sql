-- V1__initial_schema_postgres.sql

-- Core tables
CREATE TABLE platform (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE,
                          description TEXT,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE platform_binary (
                                 id BIGSERIAL PRIMARY KEY,
                                 platform_id BIGINT NOT NULL REFERENCES platform(id),
                                 name VARCHAR(100) NOT NULL,
                                 variant VARCHAR(100) NOT NULL DEFAULT 'Default',
                                 description TEXT,
                                 is_default BOOLEAN DEFAULT FALSE,
                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE platform_binary_launch_argument (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 platform_binary_id BIGINT NOT NULL REFERENCES platform_binary(id),
                                                 argument_order INTEGER NOT NULL,
                                                 argument_template TEXT NOT NULL,
                                                 is_required BOOLEAN DEFAULT TRUE,
                                                 file_argument BOOLEAN DEFAULT FALSE,
                                                 description TEXT,
                                                 created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                 updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE author (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Program and related tables
CREATE TABLE program (
                         id BIGSERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         platform_id BIGINT NOT NULL REFERENCES platform(id),
                         type VARCHAR(50) NOT NULL,
                         release_year INTEGER,
                         description TEXT,
                         content_rating VARCHAR(50),
                         curation_status VARCHAR(50) NOT NULL,
                         curator_notes TEXT,
                         last_run_at TIMESTAMP WITH TIME ZONE,
                         run_count INTEGER DEFAULT 0,
                         platform_binary_id BIGINT REFERENCES platform_binary(id),
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT check_program_type CHECK (type IN ('DEMO', 'GAME', 'MUSIC')),
                         CONSTRAINT check_content_rating CHECK (content_rating IN ('UNRATED', 'SAFE', 'NSFW')),
                         CONSTRAINT check_curation_status CHECK (curation_status IN ('UNCURATED', 'WORKING', 'BROKEN'))
);


CREATE TABLE playback_timeline_event (
                                  id BIGSERIAL PRIMARY KEY,
                                  program_id BIGINT NOT NULL REFERENCES program(id),
                                  event_type VARCHAR(50) NOT NULL,
                                  sequence_number INTEGER NOT NULL,
                                  time_offset_seconds INTEGER NOT NULL,  -- Delay before executing this command
                                  event_data JSONB,  -- For command-specific data (e.g., keys to press)
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                  CONSTRAINT unique_program_order UNIQUE(program_id, sequence_number),
                                  CONSTRAINT check_command_type CHECK (event_type IN ('MOUNT_NEXT_DISK', 'PRESS_KEYS', 'END_PLAYBACK'))
);

CREATE TABLE program_disk_image (
                                    id BIGSERIAL PRIMARY KEY,
                                    program_id BIGINT NOT NULL REFERENCES program(id),
                                    disk_number INTEGER NOT NULL,
                                    image_name VARCHAR(1024) NOT NULL,
                                    file_hash VARCHAR(64) NOT NULL UNIQUE,
                                    file_size BIGINT NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE(program_id, disk_number)
);



-- Join table for program-author many-to-many relationship
CREATE TABLE program_author (
                                program_id BIGINT NOT NULL REFERENCES program(id),
                                author_id BIGINT NOT NULL REFERENCES author(id),
                                author_role VARCHAR(50), -- Optional: e.g., "Code", "Graphics", "Music"
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (program_id, author_id)
);

CREATE TABLE program_launch_argument (
                                         id BIGSERIAL PRIMARY KEY,
                                         program_id BIGINT NOT NULL REFERENCES program(id),
                                         argument_order INTEGER NOT NULL,
                                         argument_value TEXT NOT NULL,
                                         argument_group VARCHAR(50),
                                         description TEXT,
                                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Initial data
INSERT INTO platform (name, description) VALUES
    ('C64', 'Commodore 64 computer platform');

-- Insert default binary
INSERT INTO platform_binary (platform_id, name, description, is_default) VALUES
    ((SELECT id FROM platform WHERE name = 'C64'), 'x64sc', 'Accurate C64 emulation - better for demos', TRUE);

-- Add default argument templates for x64sc
INSERT INTO platform_binary_launch_argument (
    platform_binary_id,
    argument_order,
    argument_template,
    is_required,
    file_argument,
    description
) VALUES (
             (SELECT id FROM platform_binary WHERE name = 'x64sc'),
             1,
             '-silent',
             true,
             false,
             'Run without sound'
         ), (
             (SELECT id FROM platform_binary WHERE name = 'x64sc'),
             2,
             '-fullscreen',
             true,
             false,
             'Run in fullscreen mode'
         ), (
             (SELECT id FROM platform_binary WHERE name = 'x64sc'),
             999,
             '-autostart',
             true,
             true,
             'Autostart the program file'
         );

