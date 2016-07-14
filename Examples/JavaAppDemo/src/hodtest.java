import java.io.File;
import java.util.*;

import hodclient.*;
import hodresponseparser.*;

public class hodtest {

	public hodtest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestApp test = new TestApp();
		test.testSentimentAnalysis();
		test.testEntityExtraction();
		test.testSpeechRecognition();
	}

}

class TestApp implements IHODClientCallback {

	HODClient client = new HODClient("your-api-key", this);
	// if proxy required
	//client.SetProxy("proxy addr", port);
	HODResponseParser parser = new HODResponseParser();
	String hodApp = "";
	public TestApp()
	{
		
	}
	public void testSentimentAnalysis() {
		hodApp = HODApps.ANALYZE_SENTIMENT;
        Map<String, Object> params = new HashMap<String, Object>();

        String filename1 = "JavaAppDemo\\TestData\\sentiment1.txt";
        String filename2 = "JavaAppDemo\\TestData\\sentiment2.txt";
        String filename3 = "JavaAppDemo\\TestData\\sentiment3.txt";
        /*
        // pass multiple files as File object 
        ListFile> files = new ArrayList<File>();
        File tempFile1 = new File(filename1);
        File tempFile2 = new File(filename2);
        File tempFile3 = new File(filename3);
        */
        
        // pass multiple files as filename
        List<String> files = new ArrayList<String>();
        files.add(filename1);
        files.add(filename2);
        files.add(filename3);
        //
        
        params.put("file", files);
        params.put("language", "eng");
		
		client.PostRequest(params, hodApp, HODClient.REQ_MODE.ASYNC);
	}
	public void testEntityExtraction() {
		hodApp = HODApps.ENTITY_EXTRACTION;
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("url", "http://www.bbc.com");
        params.put("unique_entities", "true");
        List<String> entities = new ArrayList<String>();
        entities.add("people_eng");
        entities.add("places_eng");
        entities.add("companies_eng");
        params.put("entity_type", entities);
		
		client.GetRequest(params, hodApp, HODClient.REQ_MODE.SYNC);
	}
	public void testSpeechRecognition() {
		hodApp = HODApps.RECOGNIZE_SPEECH;
        Map<String, Object> params = new HashMap<String, Object>();

        String fileName = "JavaAppDemo\\TestData\\voanews1.mp3";
        
        // pass a single file as File object
        File mediaFile = new File(fileName); 

		// pass a single file as filename
		// String mediaFile = fileName;

        params.put("file", mediaFile);
        params.put("language", "en-US");
		
		client.PostRequest(params, hodApp, HODClient.REQ_MODE.ASYNC);
	}
	@Override
	public void requestCompletedWithContent(String response) {
		// TODO Auto-generated method stub
		if (hodApp.equals(HODApps.ANALYZE_SENTIMENT)) {
			SentimentAnalysisResponse resp = parser.ParseSentimentAnalysisResponse(response);
			if (resp != null) {
				String result = "Response:\r\n";
				result += "Positive:\r\n";
				for (SentimentAnalysisResponse.Entity pos : resp.positive) {
					if (pos.sentiment != null)
						result += "Sentiment: " + pos.sentiment + "\r\n";
					if (pos.topic != null)
						result += "Topic: " + pos.topic + "\r\n";
					result += "Score: " + pos.score.toString() + "\r\n";
					result += "Ori text: " + pos.original_text + "\r\n";
					result += "Doc #: " + pos.documentIndex + "\r\n";
	            }
				result += "Negative:\r\n";
				for (SentimentAnalysisResponse.Entity neg : resp.negative) {
					if (neg.sentiment != null)
						result += "Sentiment: " + neg.sentiment + "\r\n";
					if (neg.topic != null)
						result += "Topic: " + neg.topic + "\r\n";
					result += "Score: " + neg.score.toString() + "\r\n";
					result += "Ori text: " + neg.original_text + "\r\n";
					result += "Doc #: " + neg.documentIndex + "\r\n";
	            }
				result += "--END--";
				System.out.println(result);
			} else {
				List<HODErrorObject> errors = parser.GetLastError();
				for (HODErrorObject err : errors) {
					if (err.error == HODErrorCode.QUEUED) {
						try {
							Thread.sleep(3000);
							client.GetJobStatus(err.jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else if (err.error == HODErrorCode.IN_PROGRESS) {
						try {
							Thread.sleep(10000);
							client.GetJobStatus(err.jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else {
						String result = "Error:\r\n";
						result += "Code: " + String.format("%d\r\n", err.error);
						result += "Reason: " + err.reason + "\r\n";
						result += "Detail: " + err.detail + "\r\n";
						System.out.println(result);
					}
				}
			}
		} else if (hodApp.equals(HODApps.ENTITY_EXTRACTION))
        {
			EntityExtractionResponse resp = (EntityExtractionResponse)parser.ParseCustomResponse(EntityExtractionResponse.class, response);
            if (resp != null)
            {
                String result = "ENTITIES\r\n";
                if (resp.entities.size() > 0)
                {
                    for (EntityExtractionResponse.Entity entity : resp.entities)
                    {
                        if (entity.type.equals("companies_eng"))
                        {
                            result += "Company name: " + entity.normalized_text + "\r\n";
                            if (entity.additional_information != null)
                            {
                                if (entity.additional_information.wikipedia_eng != null)
                                    result += "Wikipedia page: " + entity.additional_information.wikipedia_eng + "\r\n";
                                if (entity.additional_information.url_homepage != null)
                                    result += "Home page: " + entity.additional_information.url_homepage + "\r\n";
                            }
                        }
                        else if (entity.type.equals("places_eng"))
                        {
                            result += "Place name: " + entity.normalized_text + "\r\n";
                            if (entity.additional_information != null)
                            {
                                if (entity.additional_information.place_population != null)
                                    result += "Polulation: " + entity.additional_information.place_population + "\r\n";
                                if (entity.additional_information.place_continent != null)
                                    result += "Continent: " + entity.additional_information.place_continent + "\r\n";
                                if (entity.additional_information.lon != null)
                                    result += "Lon/Lat: " + entity.additional_information.lon + "/" + entity.additional_information.lat + "\r\n";
                                if (entity.additional_information.wikipedia_eng != null)
                                    result += "Wiki page: " + entity.additional_information.wikipedia_eng + "\r\n";
                                if (entity.additional_information.image != null)
                                    result += "Wiki page: " + entity.additional_information.image + "\r\n";
                            }
                        }
                        else if (entity.type.equals("people_eng"))
                        {
                            result += "Place name: " + entity.normalized_text + "\r\n";
                            if (entity.additional_information != null)
                            {
                                if (entity.additional_information.person_profession != null) {
                                	String professions = "Professions: ";
                                	for (String prof : entity.additional_information.person_profession)
                                		professions += prof + " | ";
                                	result += professions + "\r\n";
                                }
                                if (entity.additional_information.wikipedia_eng != null)
                                	result += "Wiki page: " + entity.additional_information.wikipedia_eng + "\r\n";
                                if (entity.additional_information.image != null)
                                	result += "Wiki page: " + entity.additional_information.image + "\r\n";
                            }
                        }

                        result += "\r\n";
                    }
                    System.out.println(result);
                }
                else
                {
                    result += "No entity was found.";
                    System.out.println(result);
                }
            }
            else
            {
            	List<HODErrorObject> errors = parser.GetLastError();
            	String result = "Error:\r\n";
				for (HODErrorObject err : errors) {
					result += "Code: " + String.format("%d\r\n", err.error);
					result += "Reason: " + err.reason + "\r\n";
					result += "Detail: " + err.detail + "\r\n";
				}
				System.out.println(result);
            }
        } else if (hodApp.equals(HODApps.RECOGNIZE_SPEECH)) {
			SpeechRecognitionResponse resp = parser.ParseSpeechRecognitionResponse(response);
			if (resp != null) {
				String result = "Recognized text:\r\n";
                for (SpeechRecognitionResponse.Document doc : resp.document)
                {
                    result += doc.content;
                }
                System.out.println(result);
			} else {
				List<HODErrorObject> errors = parser.GetLastError();
				for (HODErrorObject err : errors) {
					if (err.error == HODErrorCode.QUEUED) {
						try {
							Thread.sleep(3000);
							client.GetJobStatus(err.jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else if (err.error == HODErrorCode.IN_PROGRESS) {
						try {
							Thread.sleep(10000);
							client.GetJobStatus(err.jobID);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					} else {
						String result = "Error:\r\n";
						result += "Code: " + String.format("%d\r\n", err.error);
						result += "Reason: " + err.reason + "\r\n";
						result += "Detail: " + err.detail + "\r\n";
						System.out.println(result);
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
	}

	@Override
	public void onErrorOccurred(String errorMessage) {
		// TODO Auto-generated method stub
		System.out.println("Error: " + errorMessage);
	}
	
}