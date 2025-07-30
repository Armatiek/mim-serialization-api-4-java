package nl.geostandaarden.mim.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;

public class XhtmlUtil {
  
  public static String serializeXhtml(List<Object> objects) throws MimSerializationApiXhtmlException {
    try {
      StringBuilder sb = new StringBuilder();
      for (Object obj: objects) {
        if (obj instanceof String) {
          sb.append((String) obj);
        } else if (obj instanceof Element) {
          sb.append(nodeToString((Element) obj));
        }
      }
      return sb.toString();
    } catch (Exception e) {
      throw new MimSerializationApiXhtmlException(e.getMessage(), e);
    }
  }
  
  public static void deserializeXhtml(String xhtml, List<Object> objects) throws MimSerializationApiXhtmlException {
    try {
      String xml = "<xhtml:wrapper xmlns:xhtml=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://www.w3.org/1999/xhtml\">" + xhtml + "</xhtml:wrapper>";
      Document doc = loadDocument(new ByteArrayInputStream(xml.getBytes()));
      Node node = doc.getDocumentElement().getFirstChild();
      while (node != null) {
        if (node.getNodeType() == Node.TEXT_NODE) {
          objects.add(node.getTextContent());
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
          objects.add(node);
        }
        node = node.getNextSibling();
      }
    } catch (Exception e) {
      throw new MimSerializationApiXhtmlException(e.getMessage(), e);
    }
  }
  
  private static String nodeToString(Node node) throws Exception {
    if (node == null) {
      return "";
    }
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "no");
    DOMSource source = new DOMSource(node);
    StringWriter sw = new StringWriter();
    StreamResult result = new StreamResult(sw);
    transformer.transform(source, result);
    return sw.toString();
  }
  
  private static Document loadDocument(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(inputStream);
  }

}
