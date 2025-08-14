package nl.geostandaarden.mim.interfaces;

import nl.geostandaarden.mim.MimModel;

/**
 * Interface that is implemented by all RefTypeEx classes
 */
public interface TargetProvider {
  
  public void setMimModel(MimModel mimModel);
  
  public Object getTarget();
  
  public void setTarget(Object target);
  
}