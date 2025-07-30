package nl.geostandaarden.mim.samples;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.MimSerializationApi;
import nl.geostandaarden.mim.error.MimSerializationApiException;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;
import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;
import nl.geostandaarden.mim.interfaces.TargetProvider;
import nl.geostandaarden.mim.interfaces.XhtmlSerializer;
import nl.geostandaarden.mim_1_2.relatierol.Attribuutsoort;
import nl.geostandaarden.mim_1_2.relatierol.Referentielijst;

/**
 * Sample that shows how to load (unmarshal, deserialize) a MIM XML serialization and display the names
 * of all Objecttypes in the first domain of the model.
 */
public class LoadMimModel {
  
  private static final String SAMPLE_PATH = "/xsd/1.2/Samples/Fietsenwinkel.xml";
  
  private nl.geostandaarden.mim_1_2.relatierol.MimModel mimModel;
  
  public void loadModel() throws MimSerializationApiLoadException {
    /* Get an inputstream of a sample serialization: */
    InputStream mimSerialization = LoadMimModel.class.getResourceAsStream(SAMPLE_PATH);
    
    /* Load the model (MIM version 1.2 - Relatierol leidend) while validating the serialization: */
    this.mimModel = (nl.geostandaarden.mim_1_2.relatierol.MimModel) MimSerializationApi.loadModel(mimSerialization, new ValidationEventHandler() {
      @Override
      public boolean handleEvent(ValidationEvent event) {
        System.out.println("Validation error: " + event.getMessage() + " (" + event.getSeverity() + ")");
        return event.getSeverity() == ValidationEvent.WARNING;
      }
    });
  }
  
  public void displayNamesOfObjecttypesInFirstDomain() {
    /* Display the names of all Objecttypes in the first domain of the model: */
    List<nl.geostandaarden.mim_1_2.relatierol.Objecttype> objectTypesInFirstDomain = mimModel.getInformatiemodel().getPackages().getDomein().get(0).getObjecttypen().getObjecttype();
    objectTypesInFirstDomain.forEach(
        objectType -> System.out.println(objectType.getNaam())
    );
  }
  
  public void displayXhtmlContent() throws MimSerializationApiXhtmlException {
    /* Displays a xhtml field as string: */
    
    /* Get an Objecttype by its name using a helper method: */
    nl.geostandaarden.mim_1_2.relatierol.Objecttype objectType = mimModel.getObjecttypeByName("Bankrekening");
    
    /* Cast its definition field to the XhtmlSerializer interface: */
    XhtmlSerializer ser = (XhtmlSerializer) objectType.getDefinitie();
    
    /* Display the xhtml text: */
    System.out.println(ser.getContentAsString());
  }
  
  public void followReference() {
    /* Get an Objecttype by its name using a helper method: */
    nl.geostandaarden.mim_1_2.relatierol.Objecttype objectType = mimModel.getObjecttypeByName("Leverancier");
    
    /* Get its attribuut with name "kvk nummer": */
    Optional<Attribuutsoort> attr = objectType.getAttribuutsoorten().getAttribuutsoort().stream().filter(a -> "kvk nummer".equals(((Attribuutsoort) a).getNaam())).findFirst();
    
    /* Cast its DatatypeRef to the TargetProvider interface: */
    TargetProvider targetProvider = (TargetProvider) attr.get().getType().getDatatypeRef();
    
    /* Get the name of the Referentielijst target: */
    System.out.println(((Referentielijst) targetProvider.getTarget()).getNaam());
  }
  
  public static void main(String[] args) throws Exception {
    LoadMimModel lmm = new LoadMimModel();
    try {
      lmm.loadModel();
      lmm.displayNamesOfObjecttypesInFirstDomain();
      lmm.displayXhtmlContent();
      lmm.followReference();
    } catch (MimSerializationApiException e) {
      e.printStackTrace(System.err);
    }
  }

}