package nl.geostandaarden.mim.samples;

import java.io.InputStream;
import java.util.List;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import nl.geostandaarden.mim.MimSerializationApi;
import nl.geostandaarden.mim.error.MimSerializationApiException;
import nl.geostandaarden.mim.error.MimSerializationApiLoadException;
import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;
import nl.geostandaarden.mim_1_2.relatierol.Attribuutsoort;
import nl.geostandaarden.mim_1_2.relatierol.AttribuutsoortEx;
import nl.geostandaarden.mim_1_2.relatierol.Codelijst;
import nl.geostandaarden.mim_1_2.relatierol.Objecttype;
import nl.geostandaarden.mim_1_2.relatierol.ObjecttypeEx;
import nl.geostandaarden.mim_1_2.relatierol.Referentielijst;
import nl.geostandaarden.mim_1_2.relatierol.XhtmlTextEx;
import nl.geostandaarden.mim_1_2.relatierol.ref.RefType;
import nl.geostandaarden.mim_1_2.relatierol.ref.RefTypeEx;

/**
 * Sample that shows how to load (unmarshal, deserialize) an existing MIM XML serialization
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
  
  /* Display the names of all Objecttypes in the first domain of the model: */
  public void displayNamesOfObjecttypesInFirstDomain() {
    List<nl.geostandaarden.mim_1_2.relatierol.Objecttype> objectTypesInFirstDomain = mimModel.getInformatiemodel().getPackages().getDomein().get(0).getObjecttypen().getObjecttype();
    objectTypesInFirstDomain.forEach(
        objectType -> System.out.println(objectType.getNaam())
    );
  }
  
  /* Displays a xhtml field as string: */
  public void displayXhtmlContent() throws MimSerializationApiXhtmlException {
    /* Get an Objecttype by its name using a helper method: */
    nl.geostandaarden.mim_1_2.relatierol.Objecttype objectType = mimModel.getObjecttypeByName("Bankrekening");
    
    /* Cast its definition field to the XhtmlTextEx interface: */
    XhtmlTextEx definitie = (XhtmlTextEx) objectType.getDefinitie();
    
    /* Display the xhtml text: */
    System.out.println(definitie.getContentAsString());
  }
  
  public void followReferences() {
    /* Get an Objecttype by its name using a helper method: */
    nl.geostandaarden.mim_1_2.relatierol.Objecttype objectType = mimModel.getObjecttypeByName("Leverancier");
    
    /* Iterate the references to the supertypes: */
    objectType.getSupertypen().getGeneralisatieObjecttypen().forEach(
      gen -> 
        {
          RefType objectypeRef = gen.getSupertype().getObjecttypeRef();
          
          /* Cast the RefType to a RefTypeEx: */
          RefTypeEx refType = (RefTypeEx) objectypeRef;
          
          /* Get the name of the supertype: */
          System.out.println(((Objecttype) refType.getTarget()).getNaam());
        }     
    );
  }
  
  public void getAttribuutsoortType() {
    /* Get an Objecttype by its name using a helper method: */
    nl.geostandaarden.mim_1_2.relatierol.Objecttype objectType = mimModel.getObjecttypeByName("Leverancier");
    
    /* Get its attribuut with name "kvk nummer": */
    Attribuutsoort attr = ((ObjecttypeEx) objectType).getAttribuutsoort("kvk nummer");
    
    AttribuutsoortEx attrEx = (AttribuutsoortEx) attr;
    
    if (attrEx.getAttribuutsoortType() instanceof Codelijst) {
      System.out.println("Codelijst: " + ((Codelijst) attrEx.getAttribuutsoortType()).getNaam());
    } else if (attrEx.getAttribuutsoortType() instanceof Referentielijst) {
      System.out.println("Referentielijst: " + ((Referentielijst) attrEx.getAttribuutsoortType()).getNaam());
    }
    
  }
  
  public static void main(String[] args) throws Exception {
    LoadMimModel lmm = new LoadMimModel();
    try {
      lmm.loadModel();
      lmm.displayNamesOfObjecttypesInFirstDomain();
      lmm.displayXhtmlContent();
      lmm.followReferences();
      lmm.getAttribuutsoortType();
    } catch (MimSerializationApiException e) {
      e.printStackTrace(System.err);
    }
  }

}