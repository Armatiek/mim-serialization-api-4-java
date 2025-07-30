package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.MimModel;

public interface TargetProvider {
  
  public void setMimModel(MimModel mimModel);
  
  public Object getTarget();
  
}