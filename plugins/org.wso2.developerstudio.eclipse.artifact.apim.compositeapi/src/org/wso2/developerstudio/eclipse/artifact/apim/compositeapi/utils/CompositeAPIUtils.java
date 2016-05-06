package org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.developerstudio.eclipse.artifact.apim.compositeapi.model.API;
import org.yaml.snakeyaml.Yaml;

public class CompositeAPIUtils {
	//Implemented for km url == gw url
	private static String consumeKey;
	private static String consumerSecret;
	private static String storeUrl;
	private static String username;
	private static String password;
	
	public static List<API> getAPIsFromStore(String url, String username1,
			String password1) {
		storeUrl = url;
		username = username1;
		password = password1;
		Map<String, String> dataMap = registerOAuthApplication(url, username, password);
		String accessToken = generateOAuthAccessToken("apim:api_view", dataMap, url, username, password);
		
		List<API> APIs = getAPIs(url, accessToken);
		
		return APIs;
		
	}
	
	private static List<API> getAPIs(String url, String accessToken) {
		List<API> apis = new ArrayList<API>();
		try{
		URL tokenEndpointURL = new URL(url + "/api/am/store/v0.9/apis");
		HashMap<String, String> accessKeyMap = new HashMap<String, String>();
		accessKeyMap.put("Authorization", "Bearer " + accessToken);
		HTTPResponse response = doGet(tokenEndpointURL, accessKeyMap);
		
		JSONObject apiJsonObject = new JSONObject(response);
		JSONObject responseContent = new JSONObject(apiJsonObject.get("data").toString());
		JSONArray apiArray = responseContent.getJSONArray("list");
		API importedApi;
		
		if(apiArray != null){
			for (int i = 0, size = apiArray.length(); i < size; i++)
		    {
		      JSONObject objectInArray = apiArray.getJSONObject(i);
		      importedApi = new API();
		      importedApi.setId(objectInArray.getString("id"));
		      importedApi.setName(objectInArray.getString("name"));
		      importedApi.setProvider(objectInArray.getString("provider"));
		      importedApi.setVersion(objectInArray.getString("version"));
		      importedApi.setContext(objectInArray.getString("context"));
		      apis.add(importedApi);
		      
		    }
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return apis;
	}

	private static String generateOAuthAccessToken(String scope,
			Map<String, String> dataMap, String url, String username, String password) {
		try {
            consumeKey = dataMap.get("consumerKey");
            consumerSecret = dataMap.get("consumerSecret");
            String messageBody = "grant_type=password&username=" + username + "&password=" + password + "&scope=" + scope;
            URL tokenEndpointURL = new URL("https://localhost:8243/token");
            HashMap<String, String> accessKeyMap = new HashMap<String, String>();

            //concat consumeKey and consumerSecret and make the authenticationHeader to get access token
            String authenticationHeader = consumeKey + ":" + consumerSecret;
            byte[] encodedBytes = Base64.encodeBase64(authenticationHeader.getBytes("UTF-8"));
            accessKeyMap.put("Authorization", "Basic " + new String(encodedBytes, "UTF-8"));
            HTTPResponse tokenGenerateResponse = doPost(tokenEndpointURL, messageBody, accessKeyMap);
            JSONObject tokenGenJsonObject = new JSONObject(tokenGenerateResponse);
            String accessToken = new JSONObject(tokenGenJsonObject.get("data").toString())
                    .get("access_token").toString();

            if (accessToken != null) {
                return accessToken;
            }

        } catch (Exception e) {
        	e.printStackTrace();
            
        }
        return null;
	}

	private  static Map<String, String> registerOAuthApplication(String keyMangerUrl, String username, String password) {

        String dcrEndpointURL = keyMangerUrl + "/client-registration/v0.9/register";

        //use a random name for client to avoid conflicts in application(s)
        String randomClientName = RandomStringUtils.randomAlphabetic(5);
        String applicationRequestBody = "{\n" +
                "\"callbackUrl\": \"www.google.lk\",\n" +
                "\"clientName\": \"" + randomClientName + "\",\n" +
                "\"tokenScope\": \"Production\",\n" +
                "\"owner\": \"admin\",\n" +
                "\"grantType\": \"password refresh_token\",\n" +
                "\"saasApp\": true\n" +
                "}";

        Map<String, String> dcrRequestHeaders = new HashMap<String, String>();
        Map<String, String> dataMap = new HashMap<String, String>();

        try {
        	String usernamePw = username + ":" + password;
            //Basic Auth header is used for only to get token
            byte[] encodedBytes = Base64.encodeBase64(usernamePw.getBytes("UTF-8"));
            dcrRequestHeaders.put("Authorization", "Basic " + new String(encodedBytes, "UTF-8"));

            //Set content type as its mandatory
            dcrRequestHeaders.put("Content-Type", "application/json");
            HTTPResponse clientregRes = doPost(new URL(dcrEndpointURL), applicationRequestBody, dcrRequestHeaders);
            JSONObject clientRegistrationResponse = new JSONObject(clientregRes);
            String consumerKey = new JSONObject(clientRegistrationResponse.getString("data"))
                    .get("clientId").toString();
            String consumerSecret = new JSONObject
                    (clientRegistrationResponse.getString("data")).
                    get("clientSecret").toString();

            //give 2 second duration to create consumer key and consumer secret
            Thread.sleep(2000);
            dataMap.put("consumerKey", consumerKey);
            dataMap.put("consumerSecret", consumerSecret);

        } catch (Exception e){
        	e.printStackTrace();
        }
        return dataMap;
    }
	
	public static String getApiSwaggerDefinition(String apiId){
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("consumerKey", consumeKey);
        dataMap.put("consumerSecret", consumerSecret);
        
        String accessToken = generateOAuthAccessToken("apim:api_view", dataMap, storeUrl, username, password);
        
        //List<API> apis = new ArrayList<API>();
		try{
		URL apiEndpointURL = new URL(storeUrl + "/api/am/store/v0.9/apis/" + apiId + "/swagger" );
		HashMap<String, String> accessKeyMap = new HashMap<String, String>();
		accessKeyMap.put("Authorization", "Bearer " + accessToken);
		HTTPResponse response = doGet(apiEndpointURL, accessKeyMap);
		
		JSONObject apiJsonObject = new JSONObject(response);
		JSONObject responseContent = new JSONObject(apiJsonObject.get("data").toString());
		Yaml yaml = new Yaml();
		  // get json string
		  String prettyJSONString = responseContent.toString();
		  // mapping
		  Map<String,Object> map = (Map<String, Object>) yaml.load(prettyJSONString);
		  // convert to yaml string (yaml formatted string)
		  String output = yaml.dump(map);
		  return output;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
        
	}
	
	public static HTTPResponse doPost(URL endpoint, String postBody, Map<String, String> headers) throws Exception {
        HttpURLConnection urlConnection = null;

        HTTPResponse key1;
        try {
            urlConnection = (HttpURLConnection)endpoint.openConnection();

            try {
                urlConnection.setRequestMethod("POST");
            } catch (ProtocolException var33) {
                throw new Exception("Shouldn\'t happen: HttpURLConnection doesn\'t support POST?? " + var33.getMessage(), var33);
            }

            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            Iterator e = headers.entrySet().iterator();

            while(e.hasNext()) {
                Entry sb = (Entry)e.next();
                urlConnection.setRequestProperty((String)sb.getKey(), (String)sb.getValue());
            }

            OutputStream e1 = urlConnection.getOutputStream();

            try {
                OutputStreamWriter sb1 = new OutputStreamWriter(e1, "UTF-8");
                sb1.write(postBody);
                sb1.close();
            } catch (IOException var32) {
                throw new Exception("IOException while posting data " + var32.getMessage(), var32);
            } finally {
                if(e1 != null) {
                    e1.close();
                }

            }

            StringBuilder sb2 = new StringBuilder();
            BufferedReader rd = null;

            try {
                rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.defaultCharset()));

                String itr;
                while((itr = rd.readLine()) != null) {
                    sb2.append(itr);
                }
            } catch (FileNotFoundException var35) {
                ;
            } finally {
                if(rd != null) {
                    rd.close();
                }

            }

            Iterator itr1 = urlConnection.getHeaderFields().keySet().iterator();
            HashMap responseHeaders = new HashMap();

            while(itr1.hasNext()) {
                String key = (String)itr1.next();
                if(key != null) {
                    responseHeaders.put(key, urlConnection.getHeaderField(key));
                }
            }

            key1 = new HTTPResponse(sb2.toString(), urlConnection.getResponseCode(), responseHeaders);
        } catch (IOException var37) {
            throw new Exception("Connection error (is server running at " + endpoint + " ?): " + var37.getMessage(), var37);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return key1;
    }
	
	public static HTTPResponse doGet(URL endpoint, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = null;

        HTTPResponse ignored1;
        try {
            URL url = endpoint;
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(30000);
            Iterator sb = headers.entrySet().iterator();

            while(sb.hasNext()) {
                Entry rd = (Entry)sb.next();
                conn.setRequestProperty((String)rd.getKey(), (String)rd.getValue());
            }

            conn.connect();
            StringBuilder sb1 = new StringBuilder();
            BufferedReader rd1 = null;

            HTTPResponse httpResponse;
            try {
                rd1 = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.defaultCharset()));

                String ignored;
                while((ignored = rd1.readLine()) != null) {
                    sb1.append(ignored);
                }

                httpResponse = new HTTPResponse(sb1.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } catch (IOException var17) {
                rd1 = new BufferedReader(new InputStreamReader(conn.getErrorStream(), Charset.defaultCharset()));

                String line;
                while((line = rd1.readLine()) != null) {
                    sb1.append(line);
                }

                httpResponse = new HTTPResponse(sb1.toString(), conn.getResponseCode());
                httpResponse.setResponseMessage(conn.getResponseMessage());
            } finally {
                if(rd1 != null) {
                    rd1.close();
                }

            }

            ignored1 = httpResponse;
        } finally {
            if(conn != null) {
                conn.disconnect();
            }

        }

        return ignored1;
    }
}
