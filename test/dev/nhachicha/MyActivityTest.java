package dev.nhachicha;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import dev.nhachicha.parisparking.MainActivity;
import dev.nhachicha.parisparking.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        String hello = new MainActivity().getResources().getString(R.string.hello_world);
        assertThat(hello, equalTo("Hello world!"));
    }
    
    @Test
    public void pingMongoLab () throws Exception {
        String url = "https://api.mongolab.com/api/1/databases/parking/collections/places?apiKey=500573b6e4b0772ac8325e7c&c=true";//should return 12738
        //String url = "https://api.mongolab.com/api/1/databases/parking/collections/places?apiKey=500573b6e4b0772ac8325e7c&l=1";//should return First elements
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream in = entity.getContent();
        String json = convertStreamToString(in);
        JSONObject obj = new JSONObject (json);
        System.out.println(obj);
    }
    
    private String convertStreamToString (InputStream in){
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
