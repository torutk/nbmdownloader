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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * NetBeansプラグインのUpdateCenter情報を保持するクラス。
 * 
 * 
 * @author Toru Takahashi <torutk@gmail.com>
 */
public class UpdateCenter {
    private URL url;
    private List<URL> moduleUrls = new ArrayList<>();
    
    /**
     * UpdateCenterのURLを指定してインスタンスを生成する。
     * 
     * @param url 
     */
    public UpdateCenter(URL url) {
        this.url = url;
        XMLStreamReader reader = null;
        try {
            url.openConnection();
            InputStream in = new BufferedInputStream(url.openStream());
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLStreamReader(in);
            while (reader.hasNext()) {
                int eventType = reader.next();
                if (eventType == XMLStreamReader.START_ELEMENT) {
                    if (!reader.getLocalName().equalsIgnoreCase("module")) {
                        continue;
                    }
                    String distribution = reader.getAttributeValue(null, "distribution");
                    URL moduleUrl = null;
                    if (distribution.contains("/")) {
                        moduleUrl = new URL(distribution);
                    } else {
                        moduleUrl = replaceFileName(url, distribution);
                    }
                    moduleUrls.add(moduleUrl);
                }
            }
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(UpdateCenter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException ex) {
                    // ignore
                }
            }
        }
    }

    
    public List<URL> getModuleUrls() {
        return Collections.unmodifiableList(moduleUrls);
    }
    
    /**
     * 引数で指定したURLから末尾のファイル名を除いた部分を文字列で返却する。
     * 
     * @param url ファイル名までを指定したURL
     * @return ファイル名を除いたURLの文字列
     */
    public static String getUrlBase(URL url) {
        String urlText = url.toExternalForm();
        int index = urlText.lastIndexOf("/");
        assert -1 < index;
        return urlText.substring(0, index);
    }

    /**
     * 引数で指定したURLから末尾のファイル名を文字列で返却する。
     * 
     * @param url ファイル名までを指定したURL
     * @return ファイル名の文字列
     */
    public static String getUrlFileName(URL url) {
        String urlText = url.toExternalForm();
        int index = urlText.lastIndexOf("/");
        return urlText.substring(index + 1, urlText.length());
    }
    
    /**
     * 引数urlで指定したURLのファイル名を引数moduleNameで置き換えたURLを返却する。
     * 
     * @param url ファイル名までを指定したURL
     * @param moduleName 置き換えるファイル名
     * @return 置き換えたURL
     */
    public static URL replaceFileName(URL url, String moduleName) {
        try {
            return new URL(getUrlBase(url) + "/" + moduleName);
        } catch (MalformedURLException ex) {
            throw new AssertionError(ex);
        }
    }
}
