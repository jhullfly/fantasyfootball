package fantasyfootball;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nu.validator.htmlparser.common.XmlViolationPolicy;
import nu.validator.htmlparser.sax.HtmlParser;
import nu.validator.htmlparser.sax.XmlSerializer;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UrlToXml {
	private DocumentBuilder builder; 
	private String cookies;
	
	public UrlToXml() throws ParserConfigurationException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(false);
	    builder = factory.newDocumentBuilder();
	}
	
	public Document toXml(String url)
			throws IOException, ClientProtocolException, SAXException,
			XPathExpressionException, ParserConfigurationException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Cookie", cookies);
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			convertToXml(is, out);
			String xml = new String(out.toByteArray(), Charset.forName("UTF-8"));
		    return builder.parse(new InputSource(new StringReader(xml)));
		} finally {
		    response.close();
		}
	}
	private static void convertToXml(InputStream is, OutputStream out) throws SAXException, IOException {
        ContentHandler serializer = new XmlSerializer(out);

        HtmlParser parser = new HtmlParser(XmlViolationPolicy.ALTER_INFOSET);
        //parser.setErrorHandler(new SystemErrErrorHandler());
        parser.setContentHandler(serializer);
        parser.setProperty("http://xml.org/sax/properties/lexical-handler",
                serializer);
        parser.parse(new InputSource(is));
        out.flush();
        out.close();
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

}
