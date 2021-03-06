/*
 * Copyright (c) 2013, Toru Takahashi <torutk@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.gr.java_conf.torutk.nbmdownloader;

import java.net.URL;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ダウンロードするモジュールと状況を表現するJavaFXビューモデル。
 * 
 * <li>1. 連番
 * <li>2. モジュールファイル名
 * <li>3. ダウンロード状況
 * <li>4. モジュールファイルのURL
 * 
 * @author Toru Takahashi <torutk@gmail.com>
 */
public class Module {
    private int no;
    private String name;
    private final SimpleStringProperty status;
    private final URL url;

    /**
     * 連番、ファイル名、ダウンロード状況、ダウンロードURLを指定して生成する。
     * 
     * @param no
     * @param name
     * @param status
     * @param url
     */
    public Module(int no, String name, String status, URL url) {
        this.no = no;
        this.name = name;
        this.status = new SimpleStringProperty(status);
        this.url = url;
    }
    
    public int getNo() {
        return no;
    }
    
    public void setNo(int no) {
        this.no = no;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status.set(status);
    }
    
    public URL getUrl() {
        return url;
    }
            
}

