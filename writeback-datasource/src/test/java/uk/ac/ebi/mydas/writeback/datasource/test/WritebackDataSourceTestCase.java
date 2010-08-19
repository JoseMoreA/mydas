package uk.ac.ebi.mydas.writeback.datasource.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;

import junit.framework.TestCase;


public class WritebackDataSourceTestCase extends TestCase {
	private String baseUrl;
	private Server server;

	protected void setUp() throws Exception {
		if (server == null) {
			server = new Server(0);
			server.addHandler(new WebAppContext("./src/main/webapp", "/"));
			server.start();

			int actualPort = server.getConnectors()[0].getLocalPort();
			baseUrl = "http://localhost:" + actualPort + "/das";
		}

	}

	public void testCreateUser(){
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("user","tester"));
		parameters.add(new NameValuePair("password","tester"));
		WebRequestSettings webRequestSettings=null;
		try {
			webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback/createuser"));
		} catch (MalformedURLException e) {
			fail("Problem creating the URL");
		}
		webRequestSettings.setHttpMethod(HttpMethod.GET);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page=null;
		try {
			page = webClient.getPage(webRequestSettings);
		} catch (FailingHttpStatusCodeException e) {
			fail("It got a status code exception");
		} catch (IOException e) {
			fail("Input/output problems while getting the response page ");
		}
		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
	}
	public void testAuthenticateUser(){
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("user","tester"));
		parameters.add(new NameValuePair("password","tester"));
		WebRequestSettings webRequestSettings=null;
		try {
			webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback/authenticate"));
		} catch (MalformedURLException e) {
			fail("Problem creating the URL");
		}
		webRequestSettings.setHttpMethod(HttpMethod.GET);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page=null;
		try {
			page = webClient.getPage(webRequestSettings);
		} catch (FailingHttpStatusCodeException e) {
			fail("It got a status code exception");
		} catch (IOException e) {
			fail("Input/output problems while getting the response page ");
		}
		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
	}
	public void testWritebackCreateNewSegment() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		String xmlfeature="";
		xmlfeature+="<?xml version=\"1.0\" standalone=\"no\"?>";
		xmlfeature+="<DASGFF>";
		xmlfeature+="<GFF>";
		xmlfeature+="<SEGMENT id=\"one\" start=\"1\" stop=\"34\" version=\"Up-to-date\" label=\"one_label\">";
		xmlfeature+="<FEATURE id=\"oneFeatureIdOne\" label=\"one Feature Label One\">";
		xmlfeature+="<TYPE id=\"oneFeatureTypeIdOne\" cvId=\"CV:00001\" category=\"oneFeatureCategoryOne\">one Feature DasType Label One</TYPE>";
		xmlfeature+="<METHOD id=\"oneFeatureMethodIdOne\" cvId=\"ECO:12345\">one Feature Method Label One</METHOD>";
		xmlfeature+="<START>5</START>";
		xmlfeature+="<END>10</END>";
		xmlfeature+="<SCORE>123.45</SCORE>";
		xmlfeature+="<ORIENTATION>0</ORIENTATION>";
		xmlfeature+="<PHASE>-</PHASE>";
		xmlfeature+="<NOTE>This is a note relating to feature one of segment one.</NOTE>";
		xmlfeature+="<NOTE>USER=tester</NOTE>";
		xmlfeature+="<NOTE>PASSWORD=tester</NOTE>";
		xmlfeature+="<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>";
		xmlfeature+="<TARGET id=\"oneTargetId\" start=\"20\" stop=\"30\">oneTargetName</TARGET>";
		xmlfeature+="</FEATURE>";
		xmlfeature+="</SEGMENT>";
		xmlfeature+="</GFF>";
		xmlfeature+="</DASGFF>";
		parameters.add(new NameValuePair("_content",xmlfeature));

		WebRequestSettings webRequestSettings=null;
		try {
			webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		} catch (MalformedURLException e) {
			fail("Problem creating the URL");
		}
		webRequestSettings.setHttpMethod(HttpMethod.POST);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page=null;
		try {
			page = webClient.getPage(webRequestSettings);
		} catch (FailingHttpStatusCodeException e) {
			fail("It got a status code exception");
		} catch (IOException e) {
			fail("Input/output problems while getting the response page ");
		}

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"one\" start=\"1\" stop=\"34\" version=\"Up-to-date\" label=\"one_label\">");
		assertLocalContains(resp,"<FEATURE id=\"http://writeback/");
		assertLocalContains(resp,"\" label=\"one Feature Label One\">");
		assertLocalContains(resp,"<TYPE id=\"oneFeatureTypeIdOne\" cvId=\"CV:00001\" category=\"oneFeatureCategoryOne\">one Feature DasType Label One</TYPE>");
		assertLocalContains(resp,"<METHOD id=\"oneFeatureMethodIdOne\" cvId=\"ECO:12345\">one Feature Method Label One</METHOD>");
		assertLocalContains(resp,"<START>5</START>");
		assertLocalContains(resp,"<END>10</END>");
		assertLocalContains(resp,"<SCORE>123.45</SCORE>");
		assertLocalContains(resp,"<NOTE>This is a note relating to feature one of segment one.</NOTE>");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>");
		assertLocalContains(resp,"<TARGET id=\"oneTargetId\" start=\"20\" stop=\"30\">oneTargetName</TARGET>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackCreateSameSegment() throws Exception {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		String xmlfeature="";
		xmlfeature+="<?xml version=\"1.0\" standalone=\"no\"?>";
		xmlfeature+="<DASGFF>";
		xmlfeature+="<GFF>";
		xmlfeature+="<SEGMENT id=\"one\" start=\"1\" stop=\"34\" version=\"Up-to-date\" label=\"one_label\">";
		xmlfeature+="<FEATURE id=\"oneFeatureIdTwo\" label=\"one Feature Label Two\">";
		xmlfeature+="<TYPE id=\"oneFeatureTypeIdOne\" cvId=\"CV:00001\" category=\"oneFeatureCategoryOne\">one Feature DasType Label One</TYPE>";
		xmlfeature+="<METHOD id=\"oneFeatureMethodIdOne\" cvId=\"ECO:12345\">one Feature Method Label One</METHOD>";
		xmlfeature+="<START>15</START>";
		xmlfeature+="<END>20</END>";
		xmlfeature+="<SCORE>12.345</SCORE>";
		xmlfeature+="<ORIENTATION>0</ORIENTATION>";
		xmlfeature+="<PHASE>-</PHASE>";
		xmlfeature+="<NOTE>This is a note relating to feature two of segment one.</NOTE>";
		xmlfeature+="<NOTE>USER=tester</NOTE>";
		xmlfeature+="<NOTE>PASSWORD=tester</NOTE>";
		xmlfeature+="<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>";
		xmlfeature+="<TARGET id=\"oneTargetId\" start=\"20\" stop=\"30\">oneTargetName</TARGET>";
		xmlfeature+="</FEATURE>";
		xmlfeature+="</SEGMENT>";
		xmlfeature+="</GFF>";
		xmlfeature+="</DASGFF>";
		parameters.add(new NameValuePair("_content",xmlfeature));

		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.POST);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"one\" start=\"1\" stop=\"34\" version=\"Up-to-date\" label=\"one_label\">");
		assertLocalContains(resp,"<FEATURE id=\"http://writeback/");
		assertLocalContains(resp,"\" label=\"one Feature Label Two\">");
		assertLocalContains(resp,"<TYPE id=\"oneFeatureTypeIdOne\" cvId=\"CV:00001\" category=\"oneFeatureCategoryOne\">one Feature DasType Label One</TYPE>");
		assertLocalContains(resp,"<METHOD id=\"oneFeatureMethodIdOne\" cvId=\"ECO:12345\">one Feature Method Label One</METHOD>");
		assertLocalContains(resp,"<START>5</START>");
		assertLocalContains(resp,"<END>10</END>");
		assertLocalContains(resp,"<SCORE>123.45</SCORE>");
		assertLocalContains(resp,"<NOTE>This is a note relating to feature one of segment one.</NOTE>");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>");
		assertLocalContains(resp,"<TARGET id=\"oneTargetId\" start=\"20\" stop=\"30\">oneTargetName</TARGET>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackUpdateNewFeatureNewSegment() throws Exception {
		String xmlfeature="";
		xmlfeature+="<?xml version=\"1.0\" standalone=\"no\"?>";
		xmlfeature+="<DASGFF>";
		xmlfeature+="<GFF href=\"http://otherserver.com\">";
		xmlfeature+="<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">";
		xmlfeature+="<FEATURE id=\"twoFeatureIdOne\" label=\"two Feature Label One\">";
		xmlfeature+="<TYPE id=\"twoFeatureTypeIdOne\" cvId=\"CV:00002\" category=\"twoFeatureCategoryOne\">two Feature DasType Label One</TYPE>";
		xmlfeature+="<METHOD id=\"twoFeatureMethodIdOne\" cvId=\"ECO:23456\">two Feature Method Label One</METHOD>";
		xmlfeature+="<START>50</START>";
		xmlfeature+="<END>100</END>";
		xmlfeature+="<SCORE>1.2345</SCORE>";
		xmlfeature+="<ORIENTATION>0</ORIENTATION>";
		xmlfeature+="<PHASE>-</PHASE>";
		xmlfeature+="<NOTE>This is a note relating to feature one of segment two.</NOTE>";
		xmlfeature+="<NOTE>USER=tester</NOTE>";
		xmlfeature+="<NOTE>PASSWORD=tester</NOTE>";
		xmlfeature+="<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>";
		xmlfeature+="</FEATURE>";
		xmlfeature+="</SEGMENT>";
		xmlfeature+="</GFF>";
		xmlfeature+="</DASGFF>";

		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.PUT);
		webRequestSettings.setRequestBody(xmlfeature);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">");
		assertLocalContains(resp,"<FEATURE id=\"http://otherserver.com/twoFeatureIdOne");
		assertLocalContains(resp,"\" label=\"two Feature Label One\">");
		assertLocalContains(resp,"<TYPE id=\"twoFeatureTypeIdOne\" cvId=\"CV:00002\" category=\"twoFeatureCategoryOne\">two Feature DasType Label One</TYPE>");
		assertLocalContains(resp,"<METHOD id=\"twoFeatureMethodIdOne\" cvId=\"ECO:23456\">two Feature Method Label One</METHOD>");
		assertLocalContains(resp,"<START>50</START>");
		assertLocalContains(resp,"<END>100</END>");
		assertLocalContains(resp,"<SCORE>1.2345</SCORE>");
		assertLocalContains(resp,"<NOTE>This is a note relating to feature one of segment two.</NOTE>");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackUpdateNewFeatureSameSegment() throws Exception {
		String xmlfeature="";
		xmlfeature+="<?xml version=\"1.0\" standalone=\"no\"?>";
		xmlfeature+="<DASGFF>";
		xmlfeature+="<GFF href=\"http://otherserver.com\">";
		xmlfeature+="<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">";
		xmlfeature+="<FEATURE id=\"twoFeatureIdTwo\" label=\"two Feature Label Two\">";
		xmlfeature+="<TYPE id=\"twoFeatureTypeIdTwo\" cvId=\"CV:000021\" category=\"twoFeatureCategoryTwo\">two Feature DasType Label Two</TYPE>";
		xmlfeature+="<METHOD id=\"twoFeatureMethodIdTwo\" cvId=\"ECO:23456\">two Feature Method Label Two</METHOD>";
		xmlfeature+="<START>150</START>";
		xmlfeature+="<END>200</END>";
		xmlfeature+="<SCORE>2.345</SCORE>";
		xmlfeature+="<ORIENTATION>0</ORIENTATION>";
		xmlfeature+="<PHASE>-</PHASE>";
		xmlfeature+="<NOTE>This is a note relating to feature two of segment two.</NOTE>";
		xmlfeature+="<NOTE>USER=tester</NOTE>";
		xmlfeature+="<NOTE>PASSWORD=tester</NOTE>";
		xmlfeature+="<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>";
		xmlfeature+="</FEATURE>";
		xmlfeature+="</SEGMENT>";
		xmlfeature+="</GFF>";
		xmlfeature+="</DASGFF>";

		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.PUT);
		webRequestSettings.setRequestBody(xmlfeature);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">");
		assertLocalContains(resp,"<FEATURE id=\"http://otherserver.com/twoFeatureIdTwo");
		assertLocalContains(resp,"\" label=\"two Feature Label Two\">");
		assertLocalContains(resp,"<TYPE id=\"twoFeatureTypeIdTwo\" cvId=\"CV:000021\" category=\"twoFeatureCategoryTwo\">two Feature DasType Label Two</TYPE>");
		assertLocalContains(resp,"<METHOD id=\"twoFeatureMethodIdTwo\" cvId=\"ECO:23456\">two Feature Method Label Two</METHOD>");
		assertLocalContains(resp,"<START>150</START>");
		assertLocalContains(resp,"<END>200</END>");
		assertLocalContains(resp,"<SCORE>2.345</SCORE>");
		assertLocalContains(resp,"<NOTE>This is a note relating to feature two of segment two.</NOTE>");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackUpdateSameFeature() throws Exception {
		String xmlfeature="";
		xmlfeature+="<?xml version=\"1.0\" standalone=\"no\"?>";
		xmlfeature+="<DASGFF>";
		xmlfeature+="<GFF href=\"http://otherserver.com\">";
		xmlfeature+="<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">";
		xmlfeature+="<FEATURE id=\"twoFeatureIdOne\" label=\"two Feature Label One\">";
		xmlfeature+="<TYPE id=\"twoFeatureTypeIdOne\" cvId=\"CV:00002\" category=\"twoFeatureCategoryOne\">two Feature DasType Label One</TYPE>";
		xmlfeature+="<METHOD id=\"twoFeatureMethodIdOne\" cvId=\"ECO:23456\">two Feature Method Label One</METHOD>";
		xmlfeature+="<START>55</START>";
		xmlfeature+="<END>105</END>";
		xmlfeature+="<SCORE>1.5345</SCORE>";
		xmlfeature+="<ORIENTATION>0</ORIENTATION>";
		xmlfeature+="<PHASE>-</PHASE>";
		xmlfeature+="<NOTE>Editing the note!.</NOTE>";
		xmlfeature+="<NOTE>USER=tester</NOTE>";
		xmlfeature+="<NOTE>PASSWORD=tester</NOTE>";
		xmlfeature+="<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>";
		xmlfeature+="</FEATURE>";
		xmlfeature+="</SEGMENT>";
		xmlfeature+="</GFF>";
		xmlfeature+="</DASGFF>";

		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.PUT);
		webRequestSettings.setRequestBody(xmlfeature);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"two\" start=\"1\" stop=\"340\" version=\"Up-to-date\" label=\"two_label\">");
		assertLocalContains(resp,"<FEATURE id=\"http://otherserver.com/twoFeatureIdOne");
		assertLocalContains(resp,"\" label=\"two Feature Label One\">");
		assertLocalContains(resp,"<TYPE id=\"twoFeatureTypeIdOne\" cvId=\"CV:00002\" category=\"twoFeatureCategoryOne\">two Feature DasType Label One</TYPE>");
		assertLocalContains(resp,"<METHOD id=\"twoFeatureMethodIdOne\" cvId=\"ECO:23456\">two Feature Method Label One</METHOD>");
		assertLocalContains(resp,"<START>50</START>");
		assertLocalContains(resp,"<END>100</END>");
		assertLocalContains(resp,"<SCORE>1.2345</SCORE>");
		assertLocalContains(resp,"<NOTE>This is a note relating to feature one of segment two.</NOTE>");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<LINK href=\"http://code.google.com/p/mydas/\">mydas project home page.</LINK>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}

	public void testWritebackDeleteSameSegmentNewFeature() throws Exception {
		String segmentId="two";
		String featureId="TheFeature";
		String user="tester";
		String password="tester";
		String href="http://otherserver.com";

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("segmentid",segmentId));
		parameters.add(new NameValuePair("featureid",featureId));
		parameters.add(new NameValuePair("user",user));
		parameters.add(new NameValuePair("password",password));
		parameters.add(new NameValuePair("href",href));
		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.DELETE);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\""+segmentId+"\"");
		assertLocalContains(resp,"<FEATURE id=\""+href+"/"+featureId+"\"");
		assertLocalContains(resp,"<NOTE>USER="+user+"</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=1</NOTE>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackDeleteSameSegmentExistingFeature() throws Exception {
		String segmentId="two";
		String featureId="twoFeatureIdOne";
		String user="tester";
		String password="tester";
		String href="http://otherserver.com";

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("segmentid",segmentId));
		parameters.add(new NameValuePair("featureid",featureId));
		parameters.add(new NameValuePair("user",user));
		parameters.add(new NameValuePair("password",password));
		parameters.add(new NameValuePair("href",href));
		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.DELETE);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\""+segmentId+"\"");
		assertLocalContains(resp,"<FEATURE id=\""+href+"/"+featureId+"\"");
		assertLocalContains(resp,"<NOTE>USER="+user+"</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=3</NOTE>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	public void testWritebackDeleteNewSegmentNewFeature() throws Exception {
		String segmentId="three";
		String featureId="threeFeatureIdOne";
		String user="tester";
		String password="tester";
		String href="http://otherserver.com";

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("segmentid",segmentId));
		parameters.add(new NameValuePair("featureid",featureId));
		parameters.add(new NameValuePair("user",user));
		parameters.add(new NameValuePair("password",password));
		parameters.add(new NameValuePair("href",href));
		WebRequestSettings webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback"));
		webRequestSettings.setHttpMethod(HttpMethod.DELETE);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page = webClient.getPage(webRequestSettings);

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\""+segmentId+"\"");
		assertLocalContains(resp,"<FEATURE id=\""+href+"/"+featureId+"\"");
		assertLocalContains(resp,"<NOTE>USER="+user+"</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=1</NOTE>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}

	public void testWritebackHistory(){
		String featureId="http://otherserver.com/twoFeatureIdOne";

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new NameValuePair("feature",featureId));
		WebRequestSettings webRequestSettings=null;
		try{
			webRequestSettings = new WebRequestSettings(new URL(this.baseUrl+"/writeback/historical"));
		} catch (MalformedURLException e) {
			fail("Problem creating the URL");
		}
		webRequestSettings.setHttpMethod(HttpMethod.GET);
		webRequestSettings.setRequestParameters(parameters);
		WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnFailingStatusCode(false);
		Page page=null;
		try {
			page = webClient.getPage(webRequestSettings);
		} catch (FailingHttpStatusCodeException e) {
			fail("It got a status code exception");
		} catch (IOException e) {
			fail("Input/output problems while getting the response page ");
		}

		assertEquals(HttpServletResponse.SC_OK, page.getWebResponse().getStatusCode());
		String resp=page.getWebResponse().getContentAsString();
		assertLocalContains(resp,"<DASGFF>");
		assertLocalContains(resp,"<GFF");
		assertLocalContains(resp,"<SEGMENT id=\"two\"");
		assertLocalContains(resp,"<FEATURE id=\""+featureId+"\"");
		assertLocalContains(resp,"<NOTE>USER=tester</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=1</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=2</NOTE>");
		assertLocalContains(resp,"<NOTE>VERSION=3</NOTE>");
		assertLocalContains(resp,"</FEATURE>");
		assertLocalContains(resp,"</SEGMENT>");
		assertLocalContains(resp,"</GFF>");
		assertLocalContains(resp,"</DASGFF>");

	}
	private void assertLocalContains(String text,String subtext){
		assertTrue("The text ["+subtext+"] was not found in ["+text+"]",text.contains(subtext));
	}

}
