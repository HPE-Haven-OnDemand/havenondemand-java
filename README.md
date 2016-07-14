# Java client library for Haven OnDemand.

Official Java client library to help with calling [Haven OnDemand APIs](http://havenondemand.com).

## What is Haven OnDemand?
Haven OnDemand is a set of over 70 APIs for handling all sorts of unstructured data. Here are just some of our APIs' capabilities:
* Speech to text
* OCR
* Text extraction
* Indexing documents
* Smart search
* Language identification
* Concept extraction
* Sentiment analysis
* Web crawlers
* Machine learning

For a full list of all the APIs and to try them out, check out https://www.havenondemand.com/developer/apis

## Overview
The library contains 2 packages:

HODClient package for sending HTTP GET/POST requests to Haven OnDemand APIs.

HODResponseParser package for parsing JSON responses from Haven OnDemand APIs. To use the HODResponseParser, you will need the json-1.7.1.jar and java.json.jar dependencies.

----
## Integrate HODClient into your Java project (using Eclipse IDE)
1. Download the library to your local machine.
2. Right click the Project name and select "Build Path" - "Configure Build Path..." option
3. Select "Java Build Path" then click on the "Libraries" tab and then click on "Add External JARs..."
>![](/images/addjar.jpg)
4. Browse to the libs folder and select all jar files.

----
## Using HODClient package
```
import hodlient.*;
HODClient client = new HODClient("API_KEY", "v1");
```
where "API_KEY" is your API key and can be found [here](https://www.havenondemand.com/account/api-keys.html). `version` is an *optional* parameter which can be either `"v1"` or `"v2"`, but defaults to `"v1"` if not specified.

## Using HODClient with proxies
```   
HODClient client = new HODClient("API_KEY", "v1");
client.SetProxy("proxy.address", port);
```
## Implement callback functions
You will need to implement callback functions to receive responses from Haven OnDemand server
```
public class MyServlet extends HttpServlet implements IHODClientCallback {

@Override
public void requestCompletedWithContent(String response) { }

@Override
public void requestCompletedWithJobID(String response) { }

@Override
public void onErrorOccurred(String errorMessage) { }
``` 

When you call the GetRequest() or PostRequest() with the ASYNC mode, the response will be returned to this callback function. The response is a JSON string containing the jobID.
```
@Override
public void requestCompletedWithJobID(string response)
{
    // use the HODResponseParser to parse the jobID from the response
}
``` 

When you call the GetRequest() or PostRequest() with the SYNC mode, or call the GetJobResult() or GetJobStatus() functions, the response will be returned to this callback function. The response is a JSON string containing the actual result of the service.
```
@Override
public void requestCompletedWithContent(string response)
{
    // use the HODResponseParser to parse content the response
}
``` 

If there was an error occurred, the error message will be returned to this callback function.
```
@Override
public void onErrorOccurred(string errorMessage)
{
    // check and handle errors
}
```

## Sending requests to the API - GET and POST
You can send requests to the API with either a GET or POST request, where POST requests are required for uploading files and recommended for larger size queries and GET requests are recommended for smaller size queries.

### Function GetRequest
```
void GetRequest(Map<String, Object> params, String hodApp, REQ_MODE mode)
```

* `params` is a HashMap object containing key/value pair parameters to be sent to a Haven OnDemand API, where the key is the name of a parameter of that API.

>Note: For a value with its type is an array<>, the value must be defined in a List\<String\>. 
```
List<String> entities = new ArrayList<String>();
entities.add("people_eng");
entities.add("places_eng");
Map<String, Object> params = new HashMap<String, Object>();
params.put("entity_type", entities);
```

* `hodApp` a string to identify a Haven OnDemand API. E.g. "extractentities". Current supported apps are listed in the HODApps class.
* `mode` [REQ_MODE.ASYNC | REQ_MODE.SYNC]: specifies API call as Asynchronous or Synchronous.

*Example code:*
```
// Call the Entity Extraction API to find people and places from CNN and BBC website
String hodApp = HODApps.ENTITY_EXTRACTION;

var urls = new List<String>();
urls.Add("http://www.cnn.com");
urls.Add("http://www.bbc.com");

var entity_type = new List<String>();
entity_type.Add("people_eng");
entity_type.Add("places_eng");

Map<String, Object> params = new HashMap<String, Object>();
params.put("url", urls);
params.put("entity_type", entity_type);

client.GetRequest(params, hodApp, HODClient.REQ_MODE.SYNC);
```

### Function PostRequest
```
void PostRequest(Map<String, Object> params, String hodApp, REQ_MODE mode)
```
* `params` is a HashMap object containing key/value pair parameters to be sent to a Haven OnDemand API, where the key is the name of a parameter of that API. 


```
// Note 1: Post files syntax.
Map<String, Object> params = new HashMap<String, Object>();

// post a single file with a file InputStream
FileInputStream inputStream = new FileInputStream("fileName");
Map<String, Object> uploadFile = new HashMap<String, Object>();
uploadFile.put(item.getName(), inputStream);
params.put("file", uploadFile);
// post a single file with a file name
params.put("file", "fileName");

// post a single file with a File object
File uploadFile = new File("fileName"); 
params.put("file", upload);

// post multiple files with filename
List<String> uploadFiles = new ArrayList<String>();
uploadFiles.add("filename1");
uploadFiles.add("filename2");
uploadFiles.add("filename3");
params.put("file", uploadFiles);

// post multiple files with File object
List<File> uploadFiles = new ArrayList<File>();
File tempFile1 = new File(filename1);
File tempFile2 = new File(filename2);
File tempFile3 = new File(filename3);
uploadFiles.add(tempFile1);
uploadFiles.add(tempFile2);
uploadFiles.add(tempFile3);
params.put("file", uploadFiles);

// Note 2: For a value with its type is an array<>, the value must be defined in a List<String>. 
Map<String, Object> params = new HashMap<String, Object>();
List<String> entities = new ArrayList<String>();
entities.add("people_eng");
entities.add("places_eng");
params.put("entity_type", entities);
    
client.PostRequest(params, HODApps.ENTITY_EXTRACTION, HODClient.REQ_MODE.ASYNC);
```

* `hodApp` a string to identify a Haven OnDemand API. E.g. "ocrdocument". Current supported apps are listed in the HODApps class.

* `mode` [REQ_MODE.SYNC | REQ_MODE.ASYNC]: specifies API call as Asynchronous or Synchronous.


### Function GetJobResult
```
void GetJobResult(String jobID)
```

* `jobID` the jobID returned from a Haven OnDemand API upon an asynchronous call.

*Example code:*
```
// Parse a JSON string contained a jobID and call the function to get the actual content from Haven OnDemand server
String jobID = parser.ParseJobID(response);
```

### Function GetJobStatus
```
void GetJobStatus(String jobID)
```

* `jobID` the job ID returned from an Haven OnDemand API upon an asynchronous call.

*Example code:*

```
// Parse a JSON string contained a jobID and call the function to get the status of a call from Haven OnDemand API 
String jobID = parser.ParseJobID(response);
``` 

## Using HODResponseParser package
```
import hodresponseparser;
HODResponseParser parser = new HODResponseParser();
```

### Function ParseJobID
```
String ParseJobID(String response)
```
* `response` a JSON string returned from an asynchronous API call.

*Return value:*
* The jobID or an empty string if not found.

*Example code:*
```
@Override
public void requestCompletedWithJobID(String response)
{
    String jobID = parser.ParseJobID(response);
    if (jobID != "")
        client.GetJobResult(jobID);
}
```

## Parse Haven OnDemand APIs' response

*Example code:*

```
// 
@Override
public void requestCompletedWithContent(String response)
{
    OCRDocumentResponse resp = parser.ParseOCRDocumentResponse(response);
    if (resp != null)
    {
        String text = "";
        for (OCRDocumentResponse.TextBlock obj : resp.text_block) {
            text += String.format("Recognized text: %s\n", obj.text);
            text += String.format("Top/Left corner: %d/%d\n", obj.left, obj.top);
            text += String.format("Width/Height: %d/%d\n", obj.width, obj.height);
        }
	PrintWriter.print(text);
    }
    else
    {
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
```

###Function ParseCustomResponse
```
public Object ParseCustomResponse(Class<?> T, String jsonStr)
```

* `T`: a custom class object.
* `jsonStr` a json string returned from Haven OnDemand APIs.

*Example code:*
```
// define a custom class for Query Text Index API response
public class QueryIndexResponse
{
    public List<Documents> documents;
    public int totalhits;
    public class Documents
    {
        public String reference;
        public String index;
        public Double weight;
        public List<string> from;
        public List<string> to;
        public List<string> sent;
        public List<string> subject;
        public List<string> attachment;
        public List<string> hasattachments;
        public List<string> content_type;
        public string content;
    }
}
void client_requestCompletedWithContent(String response)
{
    QueryIndexResponse resp = (QueryIndexResponse) parser.ParseCustomResponse(QueryIndexResponse.class, response);
    if (resp != null)
    {
        for (QueryIndexResponse.Documents doc : resp.documents)
        {
	    // walk thru documents array
            String reference = doc.reference;
            String index = doc.index;
            Double weight = doc.weight;
            if (doc.from != null)
                var from = doc.from[0];
            if (doc.to != null)
                var to = doc.to[0];
            
            // parse any other values
        }
    }
    else
    {
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
```

---
## Examples\WebAppDemo-01: 
How to use the library in Java Servlet app.

## Examples\AjaxDemo:
How to use the library in with AJAX calls.

## Examples\JavaAppDemo:
How to use the library in a Java app.

## License
Licensed under the MIT License.