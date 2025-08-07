package nl.geostandaarden.mim_1_1_0.relatierol;

import nl.geostandaarden.mim.interfaces.AttribuutsoortType;

public class AttribuutsoortEx extends Attribuutsoort {
  
  public AttribuutsoortType getAttribuutsoortType() {
    Type type = this.getType();
    if (type.getDatatype() != null) {
      return (AttribuutsoortType) type.getDatatype();
    }
    if (type.getDatatypeRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_1_0.relatierol.ref.RefTypeEx) type.getDatatypeRef()).getTarget();
    }
    if (type.getConstructieRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_1_0.relatierol.ext.RefTypeEx) type.getConstructieRef()).getTarget();
    }
    if (type.getKeuzeRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_1_0.relatierol.ref.RefTypeEx) type.getKeuzeRef()).getTarget();
    }
    return null;
  }
  
}