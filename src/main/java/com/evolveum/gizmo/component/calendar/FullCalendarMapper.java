/*
 * Copyright (C) 2023 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.gizmo.component.calendar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class FullCalendarMapper {

    private static class MyJsonFactory extends MappingJsonFactory {

        @Override
        public JsonGenerator createGenerator(Writer w) throws IOException {
            return super.createGenerator(w).useDefaultPrettyPrinter();
        }

        @Override
        public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException {
            return super.createGenerator(f, enc).useDefaultPrettyPrinter();
        }

        @Override
        public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
            return super.createGenerator(out, enc).useDefaultPrettyPrinter();
        }

    }

    public static String toJson(Object object) {

        ObjectMapper mapper = new ObjectMapper(new FullCalendarMapper.MyJsonFactory());
        SimpleModule module = new SimpleModule("fullCalendar");
        module.addSerializer(Plugins.class, new PluginSerializer());
        mapper.registerModule(module);
//        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error encoding object: " + object + " into JSON string", e);
        }
        return json;
    }

}
