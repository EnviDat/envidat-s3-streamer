package servlets;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StreamServlet
 */
@WebServlet("/StreamServlet")
public class StreamServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Map<String,String> config;
	PathCollector collector;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
	    System.out.println("*** Requested Paths ***");  
	    
	    // Read config
	    this.config = readConfig();
		
	    // Create collector
	    int maxIterations = Integer.parseInt(config.get("MAX_ITER"));
	    int maxKeys = Integer.parseInt(config.get("MAX_KEYS"));
	    this.collector = new PathCollector(maxIterations, maxKeys, config.get("PATH_BASE_URL"), config.get("REQUEST_BASE_URL"));

	    
	    // Get parameter and return error if not present
	    String prefix = request.getParameter("prefix");
	    if (prefix == null || prefix.trim().isEmpty()){
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'prefix' missing or empty");
	    } else {
	    	
	    	System.out.println(" - prefix = " + prefix);  
	    
	        try {
	    	    // collect the paths
	            String outputResult = this.collector.getPaths(prefix);
	            
	    	    if (outputResult == null || outputResult.trim().isEmpty()){
	    	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'prefix' wrong, no content");
	    	    } 

		    	// Build the response
				String fileName = config.getOrDefault("OUTPUT_FILE_NAME", "envidatS3paths");
		        response.setContentType("text/plain");
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".txt\"");

		        // Write contents to the response
	            ServletOutputStream outputStream = response.getOutputStream();
	            outputStream.write(outputResult.getBytes());
	            outputStream.flush();
	            outputStream.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception got during request, please contac the administrator: " + e.toString());
	        }
	    }
        System.out.println("\n*** FINISHED ***");   
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected Map<String,String> readConfig(){
		Map<String,String> config = new HashMap<String, String>();
		
		try {
			Properties properties = new Properties();
			properties.load(getServletContext().getResourceAsStream("/WEB-INF/servlet.properties"));
			
			//properties.load(input);
			properties.forEach((key, value) -> config.put(key.toString(), value.toString()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    config.forEach((key, value) -> System.out.println("Key : " + key + ", Value : " + value));
		
		return config;
		
	}

}
