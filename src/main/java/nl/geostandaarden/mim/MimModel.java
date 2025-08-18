package nl.geostandaarden.mim;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.jaxb.lang.Child;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.MimSerializationApi.MIM_RELATIEMODELLERINGSTYPE;
import nl.geostandaarden.mim.MimSerializationApi.MIM_VERSION;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;
import nl.geostandaarden.mim.error.MimSerializationApiSaveException;
import nl.geostandaarden.mim.interfaces.TargetProvider;
import nl.geostandaarden.mim.util.ReflectionUtil;

/**
 * Abstract MimModel base class for all MIM version and relatiemodelleringstype specific subclasses.
 */
public abstract class MimModel {
     
  protected HashMap<String, Object> idModelElementMap = new HashMap<String, Object>();
  protected HashMap<String, List<Object>> nameModelElementMap = new HashMap<String, List<Object>>();
  protected Object informationModel;
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
        unmarshaller.setListener(new jakarta.xml.bind.Unmarshaller.Listener() {

          @Override
          public void beforeUnmarshal(Object target, Object parent) {
            if (target instanceof TargetProvider) {
              ((TargetProvider) target).setMimModel(thisModel);
            }
            super.beforeUnmarshal(target, parent);
          }

          @Override
          public void afterUnmarshal(Object target, Object parent) {
            if (target instanceof JAXBElement) {
              return;
            }
            String id = ReflectionUtil.getId(target);
            if (id != null && id.length() > 0) {
              idModelElementMap.put(id, target);
            }
            String name = ReflectionUtil.getNaam(target);
            if (name != null && name.length() > 0) {
              List<Object> objs = nameModelElementMap.get(name);
              if (objs == null) {
                objs = new ArrayList<Object>();
                nameModelElementMap.put(name, objs);
              }
              objs.add(target);
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
      ((Child) this.informationModel).setParent(this); 
    } catch (Exception e) {
      throw new MimSerializationApiLoadException(e.getMessage(), e);
    }
  }
  
  public MimModel(Document mimDoc) throws MimSerializationApiLoadException {
    this(mimDoc, null);
  }
  
  /**
   * Save (marshall/serialize) the model to an OutputStream
   * 
   * @param mimSerialization the OutputStream to save to
   * @param validationEventHandler a JAXB ValidationEventHandler
   * @throws MimSerializationApiSaveException
   */
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
  
  /**
   * Save (marshall/serialize) the model to a Path
   * 
   * @param mimSerializationPath the Path to save to
   * @param validationEventHandler a JAXB ValidationEventHandler
   * @throws MimSerializationApiSaveException
   */
  public void save(Path mimSerializationPath, ValidationEventHandler validationEventHandler) throws MimSerializationApiSaveException {    
    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(mimSerializationPath))) {
      save(bos, validationEventHandler);
    } catch (IOException e) {
      throw new MimSerializationApiSaveException(e.getMessage(), e);
    }  
  }
  
  /**
   * Save (marshall/serialize) the model to an OutputStream
   * 
   * @param mimSerialization the OutputStream to save to
   * @throws MimSerializationApiSaveException
   */
  public void save(OutputStream mimSerialization) throws MimSerializationApiSaveException {
    save(mimSerialization, null);
  }
  
  /**
   * Save (marshall/serialize) the model to a Path
   * 
   * @param mimSerializationPath the Path to save to
   * @throws MimSerializationApiSaveException
   */
  public void save(Path mimSerializationPath) throws MimSerializationApiSaveException {
    save(mimSerializationPath, null);
  }
  
  /**
   * Indexes all unique identifiers so that the RefTypeEx.getTarget() functionality will work
   */
  public void indexReferences() throws MimSerializationApiSaveException {
    try {
      JAXBContext context = JAXBContext.newInstance(this.getInformatiemodelClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setListener(new jakarta.xml.bind.Marshaller.Listener() {

        @Override
        public void beforeMarshal(Object source) {
          String id = ReflectionUtil.getId(source);
          if (id != null && id.length() > 0) {
            idModelElementMap.put(id, source);
          }
        }
        
      });
      
      marshaller.marshal(getInformatiemodelWrapper(), OutputStream.nullOutputStream());
    } catch (Exception e) {
      throw new MimSerializationApiSaveException(e.getMessage(), e);
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
  
  /**
   * Gets the classpath of the associated MIM XML schema
   * 
   * @return
   */
  public String getXmlSchemaPath() {
    return "xsd/" + MimSerializationApi.versionLabelMap.get(getMimVersie()) + "/" + getXmlSchemaName();
  }
  
  /**
   * Gets the XML target namespace of the associated MIM XML schema 
   * 
   * @return
   */
  public String getXmlNamespace() {
    return MimSerializationApi.MIM_VERSION.VERSION_1_2.equals(getMimVersie()) ? MimSerializationApi.MIM_NAMESPACE_1_2 : MimSerializationApi.MIM_NAMESPACE_1_1;
  }

  /**
   * Gets the filename of the associated MIM XML schema
   * 
   * @return
   */
  public String getXmlSchemaName() {
    return MimSerializationApi.relTypeSchemaNameMap.get(getRelatiemodelleringsType());
  }
  
  /**
   * Gets the model element (like an Attribuutsoort or Objecttype) with specified unique identifier
   * 
   * @param id the unique identifier of the model element
   * @return
   */
  public Object getModelElementById(String id) {
    return idModelElementMap.get(id);
  }
  
  /**
   * Gets all model elements (like an View or Objecttype) with specified name
   * 
   * @param name the name of the model elements
   * @return the list of model elements with specified name or null if no model elements with specified name
   */
  public List<Object> getModelElementsByName(String name) {
    return nameModelElementMap.get(name);
  }
   
  /**
   * Gets the Informatiemodel object that can be used as a starting point to traverse the whole model
   * 
   * @return
   */
  public abstract Object getInformatiemodel(); 
   
  public abstract MIM_VERSION getMimVersie();
  
  public abstract MIM_RELATIEMODELLERINGSTYPE getRelatiemodelleringsType();
 
  protected abstract Class<?> getInformatiemodelClass();
  
  protected abstract JAXBElement<?> getInformatiemodelWrapper();
}