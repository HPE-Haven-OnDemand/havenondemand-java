<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HOD WebApp</title>
</head>
<body>
<h1>Sentiment Analysis from free input text</h1><hr/>
<form action="MyServlet" method="get">
	Enter free text: <input type="text" name="text" size=60/><br/>
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
	<input type="submit" value="Send">
</form>

<h1>Sentiment Analysis from text file(s)</h1><hr/>
<form action="MyServlet" method="post" enctype="multipart/form-data">
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
	<input type="submit" value="send"/>
</form>  

<br/>
<h1>Speech Recognition and Entity Extraction demo</h1><hr/>
<form action="MyServlet" method="post" enctype="multipart/form-data">
	Select a media file: <input type="file" name="file"/><br/>
	Select a language:
	<select id='language' name="language">
 		<option value="ar-MSA">Broadband Modern Standard Arabic</option> 
		<option value="de-DE">Broadband German</option>
		<option value="en-AU">Broadband Australian English</option>
		<option value="en-CA">Broadband Canadian English</option>
		<option selected value="en-US">Broadband US English</option>
		<option value="en-US-tel">Telephony US English</option>
		<option value="en-GB">Broadband British English</option>
		<option value="en-GB-tel">Telephony British English</option>
		<option value="es-ES">Broadband European Spanish</option>
		<option value="es-ES-tel">Telephony European Spanish</option>
		<option value="es-LA">Broadband Latin American Spanish</option>
		<option value="es-LA-tel">Telephony Latin American Spanish</option>
		<option value="fa-IR">Broadband Farsi (Persian)</option>
		<option value="fr-FR">Broadband French</option>
		<option value="fr-FR-tel">Telephony French</option>
		<option value="it-IT">Broadband Italian</option>
		<option value="ja-JP">Broadband Japanese</option>
		<option value="nl-NL">Broadband Dutch</option>
		<option value="pt-BR">Broadband Brazilian Portuguese</option>
		<option value="ru-RU">Broadband Russian</option>
		<option value="zh-CN">Broadband Mandarin</option>
 	</select><br/>
 	Check to call Entity Extraction from recognized text:
 	<input type="checkbox" id="chainapi" name="chainapi" value="chainapi"/><br/>
	<input hidden type="text" name="action" value="recognizespeech"/>
	<input type="submit" value="send"/>
</form>  
</body>
</html>