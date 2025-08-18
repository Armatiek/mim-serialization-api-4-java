package nl.geostandaarden.mim_1_1_0.relatiesoort;

import org.w3c.dom.Document;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.MimSerializationApi.MIM_RELATIEMODELLERINGSTYPE;
import nl.geostandaarden.mim.MimSerializationApi.MIM_VERSION;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;

public class MimModel extends nl.geostandaarden.mim.MimModel {
  
  public MimModel() {
    this.informationModel = new Informatiemodel();
  }
    
  public MimModel(Document mimDoc, ValidationEventHandler validationEventHandler) throws MimSerializationApiLoadException {
    super(mimDoc, validationEventHandler);
  }
  
  public MimModel(Document mimDoc) throws MimSerializationApiLoadException {
    super(mimDoc);
  }
        
  @Override
  public Informatiemodel getInformatiemodel() {
    return (Informatiemodel) this.informationModel;
  }
  
  @Override
  protected Class<?> getInformatiemodelClass() {
    return Informatiemodel.class;
  }
  
  @Override
  protected JAXBElement<?> getInformatiemodelWrapper() {
    return new ObjectFactory().createInformatiemodel(getInformatiemodel());
  }
  
  @Override
  public MIM_VERSION getMimVersie() {
    return MIM_VERSION.VERSION_1_1_0;
  }
  
  @Override
  public MIM_RELATIEMODELLERINGSTYPE getRelatiemodelleringsType() {
    return MIM_RELATIEMODELLERINGSTYPE.RELATIESOORT_LEIDEND;
  }
  
  public Object getModelElementByRefType(nl.geostandaarden.mim_1_1_0.relatiesoort.ext.RefType refType) {
    return super.getModelElementById(refType.getHref().substring(1));
  }
  
  public Object getModelElementByRefType(nl.geostandaarden.mim_1_1_0.relatiesoort.ref.RefType refType) {
    return super.getModelElementById(refType.getHref().substring(1));
  }
  
}