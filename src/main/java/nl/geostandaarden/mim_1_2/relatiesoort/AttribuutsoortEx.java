package nl.geostandaarden.mim_1_2.relatiesoort;

import nl.geostandaarden.mim.interfaces.AttribuutsoortType;

public class AttribuutsoortEx extends Attribuutsoort {
  
  public AttribuutsoortType getAttribuutsoortType() {
    Type type = this.getType();
    if (type.getDatatype() != null) {
      return (AttribuutsoortType) type.getDatatype();
    }
    if (type.getDatatypeRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_2.relatiesoort.ref.RefTypeEx) type.getDatatypeRef()).getTarget();
    }
    if (type.getConstructieRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_2.relatiesoort.ext.RefTypeEx) type.getConstructieRef()).getTarget();
    }
    if (type.getKeuzeRef() != null) {
      return (AttribuutsoortType) ((nl.geostandaarden.mim_1_2.relatiesoort.ref.RefTypeEx) type.getKeuzeRef()).getTarget();
    }
    return null;
  }
  
}