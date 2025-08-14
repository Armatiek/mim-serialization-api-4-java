package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;

/**
 * Interface that is implemented by all XhtmlTextEx classes
 */
public interface XhtmlDeserializer {

  public void setContentAsString(String xhtml) throws MimSerializationApiXhtmlException;
  
}