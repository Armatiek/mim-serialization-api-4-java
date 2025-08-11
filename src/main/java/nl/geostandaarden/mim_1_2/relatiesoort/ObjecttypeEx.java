package nl.geostandaarden.mim_1_2.relatiesoort;

import java.util.Optional;

public class ObjecttypeEx extends Objecttype {
  
  public Attribuutsoort getAttribuutsoort(String name) {
    if (name == null) {
      return null;
    }
    Optional<Attribuutsoort> attr = getAttribuutsoorten().getAttribuutsoort().stream().filter(a -> name.equals(((Attribuutsoort) a).getNaam())).findFirst();
    if (attr.isPresent()) {
      return attr.get();
    }
    return null;
  }
  
}