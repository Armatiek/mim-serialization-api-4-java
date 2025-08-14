package nl.geostandaarden.mim_1_1_0.relatierol.ext;

import nl.geostandaarden.mim.MimModel;
import nl.geostandaarden.mim.interfaces.TargetProvider;
import nl.geostandaarden.mim.util.ReflectionUtil;

public class RefTypeEx extends RefType implements TargetProvider {

  private MimModel mimModel;
  
  public void setMimModel(MimModel mimModel) {
    this.mimModel = mimModel;
  }
  
  public Object getTarget() {
    return this.mimModel.getModelElementById(getHref().substring(1));
  }
  
  public void setTarget(Object target) {
    String id = ReflectionUtil.getId(target);
    if (id != null && id.length() > 0) {
      this.setHref("#" + id);
    }
  }
  
}