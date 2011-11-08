package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class ListBlobsResponse {

	private ArrayList<CloudBlob> m_Blobs;

	private boolean m_IsParsed;

	private String m_Marker;

	private int m_MaxResults;

	private String m_NextMarker;

	private String m_Prefix;

	private InputStream m_StreamRef;

	private String m_Delimiter;

	public ListBlobsResponse(InputStream inputstream) {
		m_Blobs = new ArrayList<CloudBlob>();
		m_StreamRef = inputstream;
	}
	public ArrayList<CloudBlob> getBlobs(CloudBlobClient cloudblobclient,
			CloudBlobContainer cloudblobcontainer) throws StorageException,
			NotImplementedException, SAXException, IOException,
			ParserConfigurationException, URISyntaxException,
			StorageInnerException {
		if (!m_IsParsed)
			parseResponse(cloudblobclient, cloudblobcontainer);
		return m_Blobs;
	}
	public String getDelimiter() {
		return m_Delimiter;
	}
	public String getMarker() {
		return m_Marker;
	}
	public int getMaxResults() {
		return m_MaxResults;
	}
	public String getNextMarker() {
		return m_NextMarker;
	}
	public String getPrefix() {
		return m_Prefix;
	}
	public void parseResponse(CloudBlobClient cloudblobclient,
			CloudBlobContainer cloudblobcontainer) throws StorageException,
			NotImplementedException, SAXException, IOException,
			ParserConfigurationException, URISyntaxException,
			StorageInnerException {
		URI containerUri = cloudblobcontainer.getUri();
		URI endpoint = new URI(containerUri.getScheme() + "://"
				+ containerUri.getAuthority());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(m_StreamRef);
		Element root = dom.getDocumentElement();
		NodeList items = root.getElementsByTagName("Blob");
		for (int index = 0; index < items.getLength(); ++index) {
			Element blobElement = (Element) items.item(index);
			Element nameElement = (Element) blobElement.getElementsByTagName(
					"Name").item(0);
			String name = nameElement.getTextContent();
			Element urlElement = (Element) blobElement.getElementsByTagName(
					"Url").item(0);
			String urlString = urlElement.getTextContent();
			HashMap<String, String[]> arguments = PathUtility
					.parseQueryString(urlString);
			CloudBlobClient client;
			try {
				StorageCredentials credentials = SharedAccessSignatureHelper
						.parseQuery(arguments);
				client = new CloudBlobClient(endpoint, credentials);
			} catch (IllegalArgumentException exception) {
				client = cloudblobclient;
			}

			Element propertiesElement = (Element) blobElement
					.getElementsByTagName("Properties").item(0);
			Element blobTypeElement = (Element) blobElement
					.getElementsByTagName("BlobType").item(0);
			String blockTypeString = blobTypeElement.getFirstChild()
					.getNodeValue();

			CloudBlob blob;

			URI blobUri = PathUtility.stripURIQueryAndFragment(new URI(
					urlString));
			if (blockTypeString.equals("BlockBlob")) {
				blob = new CloudBlockBlob(blobUri, client, cloudblobcontainer);
			} else if (blockTypeString.equals("PageBlob")) {
				blob = cloudblobcontainer.getPageBlobReference(name);
			} else {
				throw new StorageInnerException("Unknown blob type");
			}
			m_Blobs.add(blob);
		}
	}
}