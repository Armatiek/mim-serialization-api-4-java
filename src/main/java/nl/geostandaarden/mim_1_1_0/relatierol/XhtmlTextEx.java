package nl.geostandaarden.mim_1_1_0.relatierol;

import nl.geostandaarden.mim.error.MimSerializationApiXhtmlException;
import nl.geostandaarden.mim.interfaces.XhtmlDeserializer;
import nl.geostandaarden.mim.interfaces.XhtmlSerializer;
import nl.geostandaarden.mim.util.XhtmlUtil;

public class XhtmlTextEx extends XhtmlText implements XhtmlSerializer, XhtmlDeserializer  {

  @Override
  public void setContentAsString(String xhtml) throws MimSerializationApiXhtmlException {
    XhtmlUtil.deserializeXhtml(xhtml, this.getContent());
  }

  @Override
  public String getContentAsString() throws MimSerializationApiXhtmlException {
   return XhtmlUtil.serializeXhtml(this.getContent());
  }
  
}