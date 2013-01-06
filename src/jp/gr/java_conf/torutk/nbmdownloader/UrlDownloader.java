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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 指定されたURLのファイルを指定されたパスにダウンロードする。
 * 
 * @author Toru Takahashi <torutk@gmail.com>
 */
public class UrlDownloader {
   
    /**
     * urlで指定されたURLのファイルをpathで指定された場所に保存する。
     * 
     * 事前条件:urlが正規の形式である。
     * 　　　　　　 pathで指定された場所が書き込み可能で存在する。
     * 事後条件:pathにurlのファイル名でダウンロードしたファイルが保存される。
     * 
     * @param url ダウンロード対象URL
     * @param path ダウンロードしたファイルの保存場所
     * @throws UrlDownloadException ダウンロードに問題発生
     */
    public static void download(URL url, Path path) throws UrlDownloadException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.setRequestMethod("GET");
        } catch (IOException ex) {
            throw new UrlDownloadException("HTTP接続が確立不能", ex);
        }
        
        try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
            Path fileName = Paths.get(url.getPath()).getFileName();
            Path saveFile = path.resolve(fileName);
            Files.copy(in, saveFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new UrlDownloadException("ダウンロード中に異常発生", ex);
        } finally {
            connection.disconnect();
        }
    }

}
