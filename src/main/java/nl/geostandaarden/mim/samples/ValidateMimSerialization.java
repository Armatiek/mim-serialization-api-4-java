package nl.geostandaarden.mim.samples;

import java.io.InputStream;
import java.util.List;

import org.xml.sax.SAXParseException;

import nl.geostandaarden.mim.MimSerializationApi;

public class ValidateMimSerialization {
  
  public void run() throws Exception {
    InputStream mimSerialization = this.getClass().getResourceAsStream("/xsd/1.2/Samples/Fietsenwinkel.xml");
    List<SAXParseException> parseExceptions = MimSerializationApi.validateMimSerialization(mimSerialization);
    if (parseExceptions.isEmpty()) {
      System.out.println("No validation errors");
    } else {
      parseExceptions.forEach(
        e -> System.out.println(String.format("Line number: %s, Column number: %s. %s", e.getLineNumber(), e.getColumnNumber(), e.getMessage()))
      );
    }
  }

  public static void main(String[] args) throws Exception {
    ValidateMimSerialization val = new ValidateMimSerialization();
    val.run();
  }

}