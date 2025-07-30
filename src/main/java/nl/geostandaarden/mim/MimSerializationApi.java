package nl.geostandaarden.mim;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;
import nl.geostandaarden.mim.error.MimSerializationApiMimVersionException;

public class MimSerializationApi {
  
  protected static final String MIM_NAMESPACE_1_1                = "http://www.geostandaarden.nl/mim/mim-core/1.1";
  protected static final String MIM_NAMESPACE_1_2                = "http://www.geostandaarden.nl/mim/mim-core/1.2";
  protected static final String MIM_RELTYPE_RELATIESOORT_LEIDEND = "Relatiesoort leidend";
  protected static final String MIM_RELTYPE_RELATIEROL_LEIDEND   = "Relatierol leidend";
  
  public enum MIM_VERSION {
    VERSION_1_2,
    VERSION_1_1_1,
    VERSION_1_1_0
  }
  
  public enum MIM_RELATIEMODELLERINGSTYPE {
    RELATIESOORT_LEIDEND,
    RELATIEROL_LEIDEND
  }
  
  protected static Map<MIM_VERSION, String> versionLabelMap = new HashMap<MIM_VERSION, String>();
  protected static Map<MIM_RELATIEMODELLERINGSTYPE, String> relTypeSchemaNameMap = new HashMap<MIM_RELATIEMODELLERINGSTYPE, String>();
  static {
    versionLabelMap.put(MIM_VERSION.VERSION_1_2, "1.2");
    versionLabelMap.put(MIM_VERSION.VERSION_1_1_1, "1.1.1");
    versionLabelMap.put(MIM_VERSION.VERSION_1_1_0, "1.1.0");
    relTypeSchemaNameMap.put(MIM_RELATIEMODELLERINGSTYPE.RELATIESOORT_LEIDEND, "MIMFORMAT_Mim_relatiesoort.xsd");
    relTypeSchemaNameMap.put(MIM_RELATIEMODELLERINGSTYPE.RELATIEROL_LEIDEND, "MIMFORMAT_Mim_relatierol.xsd");
  }
  
  public static MimModel loadModel(InputStream mimSerialization, ValidationEventHandler validationEventHandler) throws MimSerializationApiLoadException {
    try {
      Document mimDoc = loadDocument(mimSerialization);
      MimInfo mimInfo = getMimInfo(mimDoc);
      switch (mimInfo.getVersion()) {
      case VERSION_1_2:
        switch (mimInfo.getRelType()) {
        case RELATIESOORT_LEIDEND:
          return new nl.geostandaarden.mim_1_2.relatiesoort.MimModel(mimDoc, validationEventHandler);
        case RELATIEROL_LEIDEND:
          return new nl.geostandaarden.mim_1_2.relatierol.MimModel(mimDoc, validationEventHandler);
        }
      case VERSION_1_1_1:
        switch (mimInfo.getRelType()) {
        case RELATIESOORT_LEIDEND:
          return new nl.geostandaarden.mim_1_1_1.relatiesoort.MimModel(mimDoc, validationEventHandler);
        case RELATIEROL_LEIDEND:
          return new nl.geostandaarden.mim_1_1_1.relatierol.MimModel(mimDoc, validationEventHandler);
        }
      case VERSION_1_1_0:
        switch (mimInfo.getRelType()) {
        case RELATIESOORT_LEIDEND:
          return new nl.geostandaarden.mim_1_1_0.relatiesoort.MimModel(mimDoc, validationEventHandler);
        case RELATIEROL_LEIDEND:
          return new nl.geostandaarden.mim_1_1_0.relatierol.MimModel(mimDoc, validationEventHandler);
        }
      }
      return null; // Will not occur
    } catch (MimSerializationApiLoadException e) {
      throw e;
    } catch (Exception e) {
      throw new MimSerializationApiLoadException(e.getMessage(), e);
    }
  }
  
  public static MimModel loadModel(InputStream mimSerialization) throws MimSerializationApiLoadException {
    return loadModel(mimSerialization, null);
  }
  
  public static MimModel newModel(MIM_VERSION version, MIM_RELATIEMODELLERINGSTYPE relType) {
    switch (version) {
    case VERSION_1_2:
      switch (relType) {
      case RELATIESOORT_LEIDEND:
        return new nl.geostandaarden.mim_1_2.relatiesoort.MimModel();
      case RELATIEROL_LEIDEND:
        return new nl.geostandaarden.mim_1_2.relatierol.MimModel();
      }
    case VERSION_1_1_1:
      switch (relType) {
      case RELATIESOORT_LEIDEND:
        return new nl.geostandaarden.mim_1_1_1.relatiesoort.MimModel();
      case RELATIEROL_LEIDEND:
        return new nl.geostandaarden.mim_1_1_1.relatierol.MimModel();
      }
    case VERSION_1_1_0:
      switch (relType) {
      case RELATIESOORT_LEIDEND:
        return new nl.geostandaarden.mim_1_1_0.relatiesoort.MimModel();
      case RELATIEROL_LEIDEND:
        return new nl.geostandaarden.mim_1_1_0.relatierol.MimModel();
      }
    }
    return null; // Will not occur
  }
 
  private static MimInfo getMimInfo(Document mimDoc) throws MimSerializationApiMimVersionException {
    JXPathContext jxpathContext = JXPathContext.newContext(mimDoc);    
    Variables variables = jxpathContext.getVariables();
    variables.declareVariable("reltype-relatiesoort", MIM_RELTYPE_RELATIESOORT_LEIDEND);
    variables.declareVariable("reltype-relatierol", MIM_RELTYPE_RELATIEROL_LEIDEND);
    variables.declareVariable("ns-mim-1-1", MIM_NAMESPACE_1_1);
    variables.declareVariable("ns-mim-1-2", MIM_NAMESPACE_1_2);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-2 and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatiesoort"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_2, MIM_RELATIEMODELLERINGSTYPE.RELATIESOORT_LEIDEND);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-2 and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatierol"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_2, MIM_RELATIEMODELLERINGSTYPE.RELATIEROL_LEIDEND);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-1 and /*/*[local-name() = 'MIMVersie'] = '1.1.1' and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatiesoort"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_1_1, MIM_RELATIEMODELLERINGSTYPE.RELATIESOORT_LEIDEND);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-1 and /*/*[local-name() = 'MIMVersie'] = '1.1.1' and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatierol"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_1_1, MIM_RELATIEMODELLERINGSTYPE.RELATIEROL_LEIDEND);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-1 and /*/*[local-name() = 'MIMVersie'] = '1.1.0' and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatiesoort"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_1_0, MIM_RELATIEMODELLERINGSTYPE.RELATIESOORT_LEIDEND);
    if (jxpathContext.getValue("namespace-uri(/*) = $ns-mim-1-1 and /*/*[local-name() = 'MIMVersie'] = '1.1.0' and /*/*[starts-with(local-name(), 'relatiemodellering')] = $reltype-relatierol"). equals(Boolean.TRUE))
      return new MimInfo(MIM_VERSION.VERSION_1_1_0, MIM_RELATIEMODELLERINGSTYPE.RELATIEROL_LEIDEND);
    throw new MimSerializationApiMimVersionException("Serialization is not MIM or MIM version could not be established");
  }
  
  public static List<SAXParseException> validateMimSerialization(InputStream mimSerialization) throws SAXException, IOException, ParserConfigurationException {
    /*
    Document mimDoc = loadDocument(mimSerialization);
    MimInfo mimInfo = getMimInfo(mimDoc);
    String xsdLocation = "xsd/" + versionLabelMap.get(mimInfo.getVersion()) + "/" + relTypeSchemaNameMap.get(mimInfo.getRelType()); 
    File xsdFile = new File(MimSerializationApi.class.getClassLoader().getResource(xsdLocation).getFile());
    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Source schemaFile = new StreamSource(xsdFile);
    Schema schema = factory.newSchema(schemaFile);
    Validator validator = schema.newValidator();
    XmlErrorHandler xsdErrorHandler = new XmlErrorHandler();
    validator.setErrorHandler(xsdErrorHandler);
    try {
      validator.validate(new DOMSource(mimDoc));  
    } catch (SAXParseException e)  { }
    return xsdErrorHandler.getExceptions();
    */
    return null; // TODO
  }
  
  /*
  public static String serializeXhtml(List<Object> objects) throws Exception {
    StringBuilder sb = new StringBuilder();
    for (Object obj: objects) {
      if (obj instanceof String) {
        sb.append((String) obj);
      } else if (obj instanceof Element) {
        sb.append(nodeToString((Element) obj));
      }
    }
    return sb.toString();
  }
  
  public static void deserializeXhtml(String xhtml, List<Object> objects) throws Exception {
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
  */
  
  private static Document loadDocument(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(inputStream);
  }
  
  private static class MimInfo {
    
    private MIM_VERSION version;
    private MIM_RELATIEMODELLERINGSTYPE relType;
    
    public MimInfo(MIM_VERSION version, MIM_RELATIEMODELLERINGSTYPE relType) {
      this.version = version;
      this.relType = relType;
    }
    
    public MIM_VERSION getVersion() {
      return version;
    }
    
    public MIM_RELATIEMODELLERINGSTYPE getRelType() {
      return relType;
    }
    
  }
  
}