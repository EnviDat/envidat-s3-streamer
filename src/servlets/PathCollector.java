package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;


public class PathCollector {

	private static class ListObjectsResponse {
		
		@Override
		public String toString() {
			String shortenedData = "";
			if (!(data == null || data.trim().isEmpty())) {
				shortenedData = data.substring(0, Math.min(data.length(), 20)) + "...";
			}
			return "ListObjectsResponse [ token=" + token + ", data=" + shortenedData + "]";
		}
		
		private String data;
		private String token;
		
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		
	}
	
	public static String getPaths() {
		
		int MAX_ITER = 30;
		
		//https://envicloud.os.zhdk.cloud.switch.ch/?prefix=chelsa/chelsa_V1/&max-keys=1000
		String prefix = "chelsa/chelsa_V1/";
		int maxKeys = 5000;
		String pathBaseUrl = "https://envicloud.os.zhdk.cloud.switch.ch/";
		String baseUrl = "https://envicloud.os.zhdk.cloud.switch.ch/";
		
		String pathsText = "";

		boolean doContinue = true;
		int iterations = 0;
		
		String token = null;
		
		while (doContinue && iterations< MAX_ITER) {
			
			System.out.println(" -------- Iteration " + iterations + " --------");
			ListObjectsResponse response = requestData(pathBaseUrl, baseUrl, prefix, maxKeys, token);
			
			pathsText += response.getData();
			token = response.getToken();
			
			if (token == null || token.trim().isEmpty()) {
				doContinue = false;
				System.out.println(" ----------  DONE  ----------");
			}
			
			iterations++;
		}
		
		return(pathsText);
	}
	
	public static ListObjectsResponse requestData(String pathBaseUrl, String baseUrl, String prefix, int maxKeys, String startAfter) {
		
		String requestUrl = baseUrl + "?list-type=2&prefix=" + prefix + "&max-keys=" + maxKeys;
		
		if (!(startAfter == null || startAfter.trim().isEmpty())){
			requestUrl += "&start-after=" + startAfter;
		}
    	
		System.out.println("Request URL: " + requestUrl);
		
		InputStream inputStream;
		
		String pathsText = "";
		String token = "";
		boolean truncated = false;

		try {
			inputStream = new URL(requestUrl).openStream();
		
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader;

			reader = inputFactory.createXMLStreamReader(inputStream);
			
			while (reader.hasNext()) {
				  int eventType = reader.next();
				  
				  if (eventType == XMLStreamReader.START_ELEMENT && reader.hasName()) {
					  String name = reader.getLocalName();
					  
					  if (name.equals("Key")) {
						  String value = reader.getElementText();
						  //System.out.println(" - Name: " + name + " * Value: " + value);
						  pathsText += pathBaseUrl + value.strip() + "\n";
					  } else if (name.equals("NextContinuationToken")) {
						  String value = reader.getElementText();
						  //System.out.println(" - Name: " + name + " * Value: " + value);
						  token = value.strip();
					  } else if (name.equals("IsTruncated")) {
						  String value = reader.getElementText();
						  //System.out.println(" - Name: " + name + " * Value: \"" + value + "\"");
		    	      	  if (value.strip().toString().equals("true")) {
		    	      		  truncated = true;
		    	      	  }
					  }
				  }
			}
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ListObjectsResponse response = new ListObjectsResponse();
		response.setData(pathsText);
		
		if (truncated) {
			response.setToken(token);
		}
		
		System.out.println("- Response: " + response.toString());
		return(response);
	}
 
}


