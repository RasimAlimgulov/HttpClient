package com.httpclient.practic;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MyHttpClient {


    public static void main(String[] args) throws IOException {
        MyHttpClient client = new MyHttpClient();
        // client.get();
        // client.post();
        //client.credentialProvider();
        client.proxy();
    }


                               /////////////// Отправка запроса GET
    private void get() throws IOException {
      //CloseableHttpClient clients = HttpClients.createDefault();   Создание обычного Http клиента

        CloseableHttpClient client = HttpClients.custom().addInterceptorFirst(new MyHttpRequestInterceptor()).build();
        HttpGet get = new HttpGet("https://eo5jq6bfktbf7ns.m.pipedream.net"); // Создал get запрос в качестве uri

        get.setHeader(new BasicHeader("sample-header", "My first header"));
        get.setHeader(new BasicHeader("demo-header", "My second header"));
        get.setHeader(new BasicHeader("test-header", "My third header"));
        System.out.println("Request type : " + get.getMethod());

        ResponseHandler<String> responseHandler = new MyResponseHandler();
        String response = client.execute(get, responseHandler);  // Создал объект ответа, выполняя запрос get, и сразу получил ответ
        System.out.println(response);
    }


                     //////////////// Отправка запроса POST
    private void post() throws IOException {
        //CloseableHttpClient client=HttpClients.createDefault();

        CloseableHttpClient client = HttpClients.custom().addInterceptorFirst(new MyHttpRequestInterceptor()).build();
        HttpPost post = new HttpPost("https://eo5jq6bfktbf7ns.m.pipedream.net");

        System.out.println("request type=" + post.getMethod());
        ResponseHandler<String> responseHandler = new MyResponseHandler();
        String response = client.execute(post, responseHandler);  // Создал обьек ответа, выполняя запрос get, и сразу получил ответ
        System.out.println(response);
    }
                    ////////////////  Отправка запроса с логином и паролем
    private void credentialProvider() throws IOException { ////////// Поставщик учётных данных
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope("mail.com", 80), new UsernamePasswordCredentials("user", "mypass"));
        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder = clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        CloseableHttpClient httpClient = clientBuilder.build();
        HttpGet get = new HttpGet("https://eo5jq6bfktbf7ns.m.pipedream.net");
        HttpResponse response = httpClient.execute(get);
        System.out.println(EntityUtils.toString(response.getEntity()));

    }



                  //////////////////  Использлвание прокси сервера
    private void proxy() throws IOException {
        HttpHost proxyHost = new HttpHost("localhost",8080); // имя прокси хоста
        HttpHost targetHost = new HttpHost("google.com"); // имя целевого хоста
        HttpRoutePlanner planner = new DefaultProxyRoutePlanner(proxyHost); //  Планировщик маршрута к хосту
        HttpClientBuilder clientBuilder=HttpClients.custom();
        clientBuilder.setRoutePlanner(planner);
        CloseableHttpClient httpClient=clientBuilder.build();  // Создали клиента с настройками

        HttpGet httpGet=new HttpGet("/"); // Создали запрос

        HttpResponse response=httpClient.execute(targetHost,httpGet);   // отправляем запрос на целевой хост


        System.out.println(response.getStatusLine());
        System.out.println(EntityUtils.toString(response.getEntity()));
    }




    /////Обработчик ответов
    public class MyResponseHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            int codeResponse = httpResponse.getStatusLine().getStatusCode();
            if (codeResponse >= 200 || codeResponse < 300) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity == null) {
                    return "no entity";
                } else {
                    return EntityUtils.toString(entity);
                }
            } else {
                return "code status: " + codeResponse;
            }
        }
    }

    ////////Перехватчик Запросов
    class MyHttpRequestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
            if (httpRequest.containsHeader("sample-header")) {
                System.out.println("Contains header sample-header, removing it..");
                httpRequest.removeHeaders("sample-header");
            }
            Header[] headers = httpRequest.getAllHeaders();
            for (Header ob : headers) {
                System.out.println(ob.getName());
            }
        }
    }
}