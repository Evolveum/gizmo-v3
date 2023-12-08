/*
 * Copyright 2015 Viliam Repan (lazyman)
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

package com.evolveum.gizmo.dto;

import java.io.Serializable;

/**
 * @author lazyman
 */
public class EmailDto implements Serializable {

    public static final String F_TO = "to";
    public static final String F_CC = "cc";
    public static final String F_BCC = "bcc";
    public static final String F_BODY = "body";


    private String to;
    private String cc;
    private String bcc;
    private String body;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailDto emailDto = (EmailDto) o;

        if (bcc != null ? !bcc.equals(emailDto.bcc) : emailDto.bcc != null) return false;
        if (body != null ? !body.equals(emailDto.body) : emailDto.body != null) return false;
        if (cc != null ? !cc.equals(emailDto.cc) : emailDto.cc != null) return false;
        if (to != null ? !to.equals(emailDto.to) : emailDto.to != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = to != null ? to.hashCode() : 0;
        result = 31 * result + (cc != null ? cc.hashCode() : 0);
        result = 31 * result + (bcc != null ? bcc.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmailDto{");
        sb.append("to='").append(to).append('\'');
        sb.append(", cc='").append(cc).append('\'');
        sb.append(", bcc='").append(bcc).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
