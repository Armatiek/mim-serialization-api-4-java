package nl.geostandaarden.mim.samples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import nl.geostandaarden.mim.MimSerializationApi;
import nl.geostandaarden.mim_1_2.relatierol.MimModel;
import nl.geostandaarden.mim_1_2.relatierol.Objecttype;

public class CombinedSample {

  private static final String SAMPLE_FILE_PATH = "/xsd/1.2/Samples/Fietsenwinkel.xml";
  
  public boolean preValidate() throws SAXException, IOException, ParserConfigurationException {
    InputStream mimSerialization = this.getClass().getResourceAsStream(SAMPLE_FILE_PATH);
    System.out.println("Validating MIM serialization ...");
    List<SAXParseException> parseExceptions = MimSerializationApi.validateMimSerialization(mimSerialization);
    if (parseExceptions.isEmpty()) {
      System.out.println("- No validation errors");
    } else {
      parseExceptions.forEach(
        e -> System.out.println(String.format("- Line number: %s, Column number: %s. %s", e.getLineNumber(), e.getColumnNumber(), e.getMessage()))
      );
    }
    return parseExceptions.isEmpty();
  }
  
  public MimModel loadModel() throws Exception {
    System.out.println("Loading MIM serialization ...");
    InputStream mimSerialization = this.getClass().getResourceAsStream(SAMPLE_FILE_PATH);
    MimModel mimModel = (MimModel) MimSerializationApi.loadModel(mimSerialization);    
    System.out.println("The names of all objecttypes of the first domain: ...");
    List<Objecttype> objectTypesInFirstDomain =  mimModel.getInformatiemodel().getPackages().getDomein().get(0).getObjecttypen().getObjecttype();
    objectTypesInFirstDomain.forEach(
      e -> System.out.println("- " + e.getNaam())
    );
    return mimModel;
  }
  
  public void changeModel(MimModel mimModel) {
    System.out.println("Changing the name of the first objecttype of the first domain ...");
    mimModel.getInformatiemodel().getPackages().getDomein().get(0).getObjecttypen().getObjecttype().get(0).setNaam("Nieuwe naam");
  }
  
  public byte[] saveModel(MimModel mimModel) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    mimModel.serialize(baos);
    return baos.toByteArray();
  }
  
  public void postValidate(byte[] mimSerialization) throws SAXException, IOException, ParserConfigurationException {
    System.out.println("Validating MIM serialization ...");
    List<SAXParseException> parseExceptions = MimSerializationApi.validateMimSerialization(new ByteArrayInputStream(mimSerialization));
    if (parseExceptions.isEmpty()) {
      System.out.println("- No validation errors");
    } else {
      parseExceptions.forEach(
        e -> System.out.println(String.format("- Line number: %s, Column number: %s. %s", e.getLineNumber(), e.getColumnNumber(), e.getMessage()))
      );
    }
  }
  
  public void run() throws Exception {
    if (!preValidate()) {
      return;
    }
    MimModel mimModel = loadModel();
    changeModel(mimModel);
    byte[] mimSerialization = saveModel(mimModel);
    postValidate(mimSerialization);
  }

  public static void main(String[] args) throws Exception {
    CombinedSample sample = new CombinedSample();
    sample.run();
  }
  
}