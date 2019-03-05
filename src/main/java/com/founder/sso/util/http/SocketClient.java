package com.founder.sso.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;


public class SocketClient {

	public static String doGet(String url) {
        URL URL = null;
        Proxy proxy = null;
        String result = null;

        BufferedReader br = null;
        try {
            SocketAddress sa = new InetSocketAddress("isasrvhk.hold.founder.com", 8080);
            proxy = new Proxy(Proxy.Type.HTTP, sa);
            URL = new URL(url);
            URL.openConnection(proxy);
            HttpURLConnection conn = (HttpURLConnection) URL.openConnection(proxy);
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                result = line;
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
}
