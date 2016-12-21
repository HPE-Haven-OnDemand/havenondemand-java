package hod.ajax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;

import hodclient.*;
import hodresponseparser.*;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet
@MultipartConfig
public class servletforajax extends HttpServlet implements IHODClientCallback {
	private static final long serialVersionUID = 1L;
	HODClient client = null;
	HODResponseParser parser= null;
	String hodApp = "";
	String jobID = "";
    private PrintWriter out = null; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public servletforajax() {
        super();
        // TODO Auto-generated constructor stub
        client = new HODClient("your-api-key", this);
		// if proxy required
        //client.SetProxy("proxy-address", port);
        parser = new HODResponseParser();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action"); 
		
		Map<String, Object> params = new HashMap<String, Object>();
        if (action != null) {
			if (action.equals(HODApps.ANALYZE_SENTIMENT)) {
				this.hodApp = action;
				params.put("text", request.getParameter("text"));	
				params.put("language", request.getParameter("language"));
				out = response.getWriter();
				client.GetRequest(params, this.hodApp, false);
			} else {
				response.getWriter().print("Please implement API call for this action " + action);
			}
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		out = response.getWriter();
		if(ServletFileUpload.isMultipartContent(request)){
            try {
            	DiskFileItemFactory factory = new DiskFileItemFactory();
                // files smaller than 5MB will be held in memory
                factory.setSizeThreshold(5000000); 

            	ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setFileSizeMax(100000000); // max size of attachment 100MB

            	FileItemIterator iterator = upload.getItemIterator(request);
                
            	Map<String, Object> params = new HashMap<String, Object>();
            	List<Object> files = new ArrayList<Object>();
                while (iterator.hasNext()) {          
                	FileItemStream item = iterator.next();
    	            String name = item.getFieldName();
    	            InputStream inputStream = item.openStream();
    	            if (item.isFormField()) {
    	            	if (name.equals("action")) {
    	            		this.hodApp = Streams.asString(inputStream);
    	            	} else {
    	            		params.put(name, Streams.asString(inputStream));
    	            	}
    		            inputStream.close();
    	            } else {
	    	            // Copy file content to InputStream. Supported for small/medium size file
	    	            InputStream input = copyToStream(inputStream);
	    	           	if (input != null) {
	    	           		Map<String, Object> file = new HashMap<String, Object>();
	    	           		file.put(name, input);
	    	           	} else {
	    	           		// handle stream copy error
	    	           	}
    	            	
    	            	// Write to temp file. Should be used for big file. User takes care of deleting temp file
						/*
	    	           	String fileName = item.getName();
	    	           	if( fileName.lastIndexOf("\\") >= 0 ){
	    	                fileName =  fileName.substring( fileName.lastIndexOf("\\"));
	    	            } else {
	    	                fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
	    	            }
	    	            String filePathAndName = "C:\\Users\\vuv\\workspace\\Servers" + fileName;
	    	            File tempFile = new File(filePathAndName);
	    	            try {
	    	               	writeToFile(inputStream, tempFile);
	    	               	Map<String,Object> f = new HashMap<String,Object>();
	    	               	f.put(name, tempFile);
	    	               	files.add(f);
	    	            } catch (FileNotFoundException ex) {
	    	              	throw ex;
	    	            }  catch (IOException ex) {
	                    	throw ex;
	   	                }*/
    	                inputStream.close();
    	            }
                }
    			if (files.size() > 0)
                	params.put("file", files);
    			client.PostRequest(params, this.hodApp, "v1", true);
    			// delete temp files
    			for (Object file : files) {
    				Map<String, Object> f = (HashMap<String, Object>) file;
        			for (Map.Entry<String, Object> item : f.entrySet()) {
        				File df = (File) item.getValue();
        				df.delete();
        			}
    			}
            } catch (Exception ex) {
               	String html = "<html><head/><body><h2>Error</h2>";
   				html += "<div> File Upload Failed due to " + ex + "</div>";
   				html += "</body></html>";
   				out.print(html);
            }           
        } else {
        	out.print("Sorry this Servlet only handles file upload request");
        }
	}
	// Private util methods
    private void writeToFile(InputStream inputStream, File file) throws FileNotFoundException, IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] bytes = new byte[1024];
        int bytesRead = -1;
        do {
        	bytesRead = inputStream.read(bytes);
        	if (bytesRead != -1) outputStream.write(bytes, 0, bytesRead);
        } while (bytesRead != -1);
        outputStream.flush();
        outputStream.close();    	
    }

    private InputStream copyToStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream _copy = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int bytesRead = -1;
        try {
	        do {
	        	bytesRead = inputStream.read(bytes);
	        	if (bytesRead != -1) _copy.write(bytes, 0, bytesRead);
	        } while (bytesRead != -1);
	        _copy.flush();
	        return (InputStream)new ByteArrayInputStream(_copy.toByteArray());
        }
        catch(IOException ex)
        {
            
        }
		return null;
    }
	//

	@Override
	public void requestCompletedWithContent(String response) {
		// TODO Auto-generated method stub
		if (hodApp.equals(HODApps.ANALYZE_SENTIMENT)) {
			SentimentAnalysisResponse resp = parser.ParseSentimentAnalysisResponse(response);
			if (resp != null) {
				String html = "<div>";
				html += "<div>Positive:</div>";
				for (SentimentAnalysisResponse.Entity pos : resp.positive) {
					if (pos.sentiment != null)
						html += "<span>Sentiment: " + pos.sentiment + "</span><br/>";
					if (pos.topic != null)
						html += "<span>Topic: " + pos.topic + "</span><br/>";
					html += "<span>Score: " + pos.score.toString() + "</span><br/>";
					html += "<span>Ori text: " + pos.original_text + "</span><br/>";
					html += "<span>Doc #: " + pos.documentIndex + "</span><br/>";
	            }
				html += "<div>Negative:</div>";
				for (SentimentAnalysisResponse.Entity neg : resp.negative) {
					if (neg.sentiment != null)
						html += "<span>Sentiment: " + neg.sentiment + "</span><br/>";
					if (neg.topic != null)
						html += "<span>Topic: " + neg.topic + "</span><br/>";
					html += "<span>Score: " + neg.score.toString() + "</span><br/>";
					html += "<span>Ori text: " + neg.original_text + "</span><br/>";
					html += "<span>Doc #: " + neg.documentIndex + "</span><br/>";
	            }
				html += "</div>";
				out.print(html);
			} else {
				List<HODErrorObject> errors = parser.GetLastError();
				for (HODErrorObject err : errors) {
					if (err.error == HODErrorCode.QUEUED) {
						jobID = err.jobID;
						try {
							Thread.sleep(3000);
							client.GetJobStatus(jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else if (err.error == HODErrorCode.IN_PROGRESS) {
						jobID = err.jobID;
						try {
							Thread.sleep(10000);
							client.GetJobStatus(jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else {
						String html = "<html><head/><body><h2>Error</h2>";
						html += "<div>Reason: " + err.reason + "</div>";
						html += "<div>Detail: " + err.detail + "</div>";
						html += "</body></html>";
						out.print(html);
					}
				}
			}
		}
	}

	@Override
	public void requestCompletedWithJobID(String response) {
		// TODO Auto-generated method stub
		String jobID = parser.ParseJobID(response);
		if (jobID.length() > 0)
			client.GetJobStatus(jobID);
		else
		{
			List<HODErrorObject> errors = parser.GetLastError();
			for (HODErrorObject err : errors) {
				String html = "<html><head/><body><h2>Error</h2>";
				html += "<div>Reason: " + err.reason + "</div>";
				html += "<div>Detail: " + err.detail + "</div>";
				html += "</body></html>";
				out.print(html);
			}
		}
	}

	@Override
	public void onErrorOccurred(String errorMessage) {
		// TODO Auto-generated method stub
		String html = "<html><head/><body><h2>Error</h2>";
		html += "<div>" + errorMessage + "</div>";
		html += "</body></html>";
		out.print(html);
	}

	public class EntityAdditionalInformation
    {
        public List<String> person_profession;
        public String person_date_of_birth;
        public Long wikidata_id;
        public String wikipedia_eng;
        public String image;
        public String person_date_of_death;
        public Double lon;
        public Double lat;
        public Long place_population;
        public String place_country_code;
        public String place_region1;
        public String place_region2;
        public String url_homepage;
        public Double place_elevation; 
        public String place_type;
        public String place_continent;
    }
	public class Components
	{   
		public Integer original_length;  
		public String original_text;  
		public String type;
	}
    public class Entity
    {
        public String normalized_text;
        public String original_text;
        public String type;
        public Long normalized_length;
        public Long original_length;
        public Double score;
        public String normalized_date;
        public EntityAdditionalInformation additional_information;
        public List<Components> components;
        public int documentIndex;
    }
    public class EntityExtractionResponse
    {
        public List<Entity> entities;
    }
}
