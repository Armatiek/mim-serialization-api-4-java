package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;

public interface XhtmlSerializer {
  
  public String getContentAsString() throws MimSerializationApiXhtmlException;

}