package nl.geostandaarden.mim_1_1_1.relatierol.ext;

import nl.geostandaarden.mim.MimModel;
import nl.geostandaarden.mim.interfaces.TargetProvider;

public class RefTypeEx extends RefType implements TargetProvider {

  private MimModel mimModel;
  
  public void setMimModel(MimModel mimModel) {
    this.mimModel = mimModel;
  }
  
  public Object getTarget() {
    return this.mimModel.getModelElementById(getHref().substring(1));
  }
  
}