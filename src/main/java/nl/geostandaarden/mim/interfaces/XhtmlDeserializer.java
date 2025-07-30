package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;

public interface XhtmlDeserializer {

  public void setContentAsString(String xhtml) throws MimSerializationApiXhtmlException;
  
}