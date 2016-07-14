<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
function sendGetRequest(){  
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = function() {
      if (xhr.readyState == 4) {
          var data = xhr.responseText;
          document.getElementById("result").innerHTML = data;
      }
  }
  var text = document.getElementById('freetext').value;
  var action = document.getElementById("action").value;
  var sel = document.getElementById("language");
  var lang= sel.options[sel.selectedIndex].value;
  document.getElementById("result").innerHTML = "";
  xhr.open('GET', "servletforajax?action=" + action + "&text=" + text + "&language=" + lang, false);
  xhr.send(null);
}        
function sendPostRequest(){  
	  var xhr = new XMLHttpRequest();
	  xhr.onreadystatechange = function() {
	      if (xhr.readyState == 4) {
	          var data = xhr.responseText;
	          document.getElementById("result").innerHTML = data;
	      }
	  }
	  var fd = new FormData(document.getElementById("postform"));
	  document.getElementById("result").innerHTML = "";
	  xhr.open("POST", "servletforajax?", false);
	  xhr.setRequestHeader("Content-Type","multipart/form-data; charset=utf-8; boundary='---bound----'");
	  xhr.send(fd);
	}

</script>
<title>HOD client lib demo</title>
</head>
<body>
<h1>Call Sentiment Analysis API with GET</h1>
<div>
	<span>Enter free text:</span> <input type="text" id="freetext" value="I love this song and the singer" size=60/><br/>
	<span>Enter language:</span>
	<select id='language' name="language">
 		<option value="eng">English</option>
 		<option value="spa">Spanish</option>
 		<option value="fre">French</option>
 		<option value="ger">German</option> 
		<option value="ita">Italian</option>
		<option value="chi">Chinese</option>
		<option value="por">Portuguese</option>
		<option value="rus">Russian</option>
		<option value="cze">Czech</option>
		<option value="tur">Turkish</option> 		
 	</select><br/>
 	<input hidden type="text" id="action" value="analyzesentiment"/>
	<input type="button" value="GET" onclick="sendGetRequest()"/><br/>
</div>

<div>
	<h1>Sentiment Analysis from text file</h1><hr/>
	<form id="postform" enctype="multipart/form-data">
		Select a text file: <input type="file" multiple name="file"/><br/>
		Select a language:
		<select id='language' name="language">
	 		<option value="eng">English</option>
	 		<option value="spa">Spanish</option>
	 		<option value="fre">French</option>
	 		<option value="ger">German</option> 
			<option value="ita">Italian</option>
			<option value="chi">Chinese</option>
			<option value="por">Portuguese</option>
			<option value="rus">Russian</option>
			<option value="cze">Czech</option>
			<option value="tur">Turkish</option> 		
	 	</select><br/>
		<input hidden type="text" name="action" value="analyzesentiment"/>
		<input type="button" value="POST" onclick="sendPostRequest()"/>
	</form>  
</div>
<h2>Result</h2>
<div id="result"></div>
</body>
</html>