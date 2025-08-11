package nl.geostandaarden.mim;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Unmarshaller.Listener;
import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.MimSerializationApi.MIM_RELATIEMODELLERINGSTYPE;
import nl.geostandaarden.mim.MimSerializationApi.MIM_VERSION;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;
import nl.geostandaarden.mim.error.MimSerializationApiSaveException;
import nl.geostandaarden.mim.interfaces.TargetProvider;

public abstract class MimModel {
     
  protected HashMap<String, Object> idModelElementMap = new HashMap<String, Object>();
  protected Object informationModel;
  protected JXPathContext jxpathContext;
  protected Schema schema;
  
  public MimModel() { }
  
  public MimModel(Document mimDoc, ValidationEventHandler validationEventHandler) throws MimSerializationApiLoadException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(this.getInformatiemodelClass());
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      if (validationEventHandler != null) {
        unmarshaller.setSchema(getXmlSchema());
        unmarshaller.setEventHandler(validationEventHandler);
        MimModel thisModel = this;
        unmarshaller.setListener(new Listener() {

          @Override
          public void beforeUnmarshal(Object target, Object parent) {
            if (target instanceof TargetProvider) {
              ((TargetProvider) target).setMimModel(thisModel);
            }
            super.beforeUnmarshal(target, parent);
          }

          @Override
          public void afterUnmarshal(Object target, Object parent) {
            String id = null;
            try {
              Method method = target.getClass().getMethod("getId");
              id = (String) method.invoke(target);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) { 
              return;
            }
            if (id != null && id.length() > 0) {
              idModelElementMap.put(id, target);
            }
          }
          
        });
      }
      Object obj = unmarshaller.unmarshal(new DOMSource(mimDoc));
      if (obj instanceof JAXBElement) {
        this.informationModel = ((JAXBElement<?>) obj).getValue();
      } else {
        this.informationModel = obj;
      }
      this.jxpathContext = JXPathContext.newContext(this.informationModel);
    } catch (Exception e) {
      throw new MimSerializationApiLoadException(e.getMessage(), e);
    }
  }
  
  public MimModel(Document mimDoc) throws MimSerializationApiLoadException {
    this(mimDoc, null);
  }
  
  public void save(OutputStream mimSerialization, ValidationEventHandler validationEventHandler) throws MimSerializationApiSaveException {
    try {
      JAXBContext context = JAXBContext.newInstance(this.getInformatiemodelClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      if (validationEventHandler != null) {
        marshaller.setSchema(getXmlSchema());
        marshaller.setEventHandler(validationEventHandler);
      }
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, this.getXmlNamespace() + " " + "../" + this.getXmlSchemaName());
      marshaller.marshal(getInformatiemodelWrapper(), mimSerialization);
    } catch (Exception e) {
      throw new MimSerializationApiSaveException(e.getMessage(), e);
    }
  }
  
  public void save(Path mimSerializationPath, ValidationEventHandler validationEventHandler) throws MimSerializationApiSaveException {    
    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(mimSerializationPath))) {
      save(bos, validationEventHandler);
    } catch (IOException e) {
      throw new MimSerializationApiSaveException(e.getMessage(), e);
    }  
  }
  
  public void save(OutputStream mimSerialization) throws MimSerializationApiSaveException {
    save(mimSerialization, null);
  }
  
  public void save(Path mimSerializationPath) throws MimSerializationApiSaveException {
    save(mimSerializationPath, null);
  }
  
  public void indexReferences() throws NoSuchMethodException, SecurityException, 
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Iterator<?> iter = jxpathContext.iterate("//*[@id]");
    while (iter.hasNext()) {
      Object obj = iter.next();
      Method method = obj.getClass().getMethod("getId");
      String id = (String) method.invoke(obj);
      idModelElementMap.put(id, obj);
    }
  }
  
  protected Schema getXmlSchema() throws SAXException {
    if (this.schema == null) {
      File xsdFile = new File(this.getClass().getClassLoader().getResource(getXmlSchemaPath()).getFile());
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      this.schema = factory.newSchema(new StreamSource(xsdFile));
    }
    return this.schema;
  }
  
  public String getXmlSchemaPath() {
    return "xsd/" + MimSerializationApi.versionLabelMap.get(getMimVersie()) + "/" + getXmlSchemaName();
  }
  
  public String getXmlNamespace() {
    return MimSerializationApi.MIM_VERSION.VERSION_1_2.equals(getMimVersie()) ? MimSerializationApi.MIM_NAMESPACE_1_2 : MimSerializationApi.MIM_NAMESPACE_1_1;
  }
  
  public String getXmlSchemaName() {
    return MimSerializationApi.relTypeSchemaNameMap.get(getRelatiemodelleringsType());
  }
  
  public Object getModelElementById(String id) {
    return idModelElementMap.get(id);
  }
  
  protected Object getObjecttypeByName(String name, String packageName) {    
    return jxpathContext.selectSingleNode("(/packages/*[@naam = '" + packageName + "']/objecttypen/objecttype[@naam = '" + name + "'])[1]");
  }
  
  protected Object getObjecttypeByName(String name) {
    return jxpathContext.selectSingleNode("(/packages/*/objecttypen/objecttype[@naam = '" + name + "'])[1]");
  }
  
  protected List<Object> getByXPath(String xpath) {
    List<Object> objects = new ArrayList<Object>();
    Iterator<Object> iter = jxpathContext.iterate(xpath);
    while (iter.hasNext()) {
      objects.add(iter.next());
    }
    return objects;
  }
   
  public abstract Object getInformatiemodel(); 
   
  public abstract MIM_VERSION getMimVersie();
  
  public abstract MIM_RELATIEMODELLERINGSTYPE getRelatiemodelleringsType();
 
  protected abstract Class<?> getInformatiemodelClass();
  
  protected abstract JAXBElement<?> getInformatiemodelWrapper();
}