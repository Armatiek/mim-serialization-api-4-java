package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;

/**
 * Interface that is implemented by all XhtmlTextEx classes
 */
public interface XhtmlSerializer {
  
  public String getContentAsString() throws MimSerializationApiXhtmlException;

}