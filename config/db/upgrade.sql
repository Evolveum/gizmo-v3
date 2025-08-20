/*
 *  Copyright (C) 2025 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

ALTER TABLE g_part
ADD COLUMN color VARCHAR(255);

ALTER TABLE g_project
ADD COLUMN off BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE g_user
ADD COLUMN allocation FLOAT4 NOT NULL DEFAULT 1;

ALTER TABLE g_abstract_task
    ADD COLUMN from_time TIME,
    ADD COLUMN to_time TIME;

UPDATE g_project SET off = true WHERE id = 30203;

CREATE TABLE g_label (
    id   BIGSERIAL PRIMARY KEY,
    code TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL
);

CREATE TABLE g_part_label (
    part_id  BIGINT NOT NULL REFERENCES g_part(id)   ON DELETE CASCADE,
    label_id BIGINT NOT NULL REFERENCES g_label(id)  ON DELETE CASCADE,
    PRIMARY KEY (part_id, label_id)
);