package dev.nhachicha.parisparking.util;
/*
Copyright 2012 Nabil HACHICHA

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */

public class RESTHelper {

    public static String getJson (String url) throws IllegalStateException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        String json = convertStreamToString(in);
        return json;
    }
    
    public static String postJson (String url, String payload) throws IllegalStateException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");
        
        StringEntity se = new StringEntity(payload);
        httpost.setEntity(se);
        
        HttpResponse response = httpClient.execute(httpost);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        String json = convertStreamToString(in);
        return json;
    }
    
    private static String convertStreamToString (InputStream in){
        StringBuffer buf = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
           
            while(null != (line=br.readLine())) {
                buf.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return buf.toString();
    }
}
