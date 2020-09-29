package servlets;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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

    /**
     * Default constructor. 
     */
    public StreamServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		
	    System.out.println("*** Requested Paths ***");  
	    
	    // Get parameter and return error if not present
	    String prefix = request.getParameter("prefix");
	    if (prefix == null || prefix.trim().isEmpty()){
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'prefix' missing or empty");
	    } else {
	    	
	    	System.out.println(" - prefix = " + prefix);  
	    
	        try {
	            String outputResult = PathCollector.getPaths(prefix);
	            
	    	    if (outputResult == null || outputResult.trim().isEmpty()){
	    	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'prefix' wrong, no content");
	    	    } 

		    	// Build the response
				String fileName = "envidatS3paths";
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

}
