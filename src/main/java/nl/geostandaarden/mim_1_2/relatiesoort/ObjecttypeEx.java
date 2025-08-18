package nl.geostandaarden.mim_1_2.relatiesoort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.geostandaarden.mim_1_2.relatiesoort.ext.Kenmerken.Kenmerk;
import nl.geostandaarden.mim_1_2.relatiesoort.ref.RefTypeEx;

public class ObjecttypeEx extends Objecttype {
  
  public Attribuutsoort getAttribuutsoort(String name) {
    if (name == null)
      return null;
    return getAttribuutsoorten().getAttribuutsoort().stream().filter(a -> name.equals(((Attribuutsoort) a).getNaam())).findFirst().orElse(null);
  }
  
  public Gegevensgroep getGegevensgroep(String name) {
    if (name == null)
      return null;
    return getGegevensgroepen().getGegevensgroep().stream().filter(g -> name.equals(((Gegevensgroep) g).getNaam())).findFirst().orElse(null);
  }
  
  public Relatiesoort getRelatiesoort(String name) {
    if (name == null)
      return null;
    return getRelatiesoorten().getRelatiesoort().stream().filter(r -> name.equals(((Relatiesoort) r).getNaam())).findFirst().orElse(null);
  }
  
  public Keuze getKeuze(String name) {
    if (name == null)
      return null;
    return getKeuzen().getKeuzeRef().stream()
        .filter(r -> name.equals(((Keuze) ((RefTypeEx) r).getTarget()).getNaam()))
        .map(r -> (Keuze) ((RefTypeEx) r).getTarget()).findFirst().orElse(null);
  }
  
  public Constraint getConstraint(String name) {
    if (name == null)
      return null;
    return getConstraints().getConstraint().stream().filter(c -> name.equals(((Constraint) c).getNaam())).findFirst().orElse(null);
  }
  
  public Kenmerk getKenmerk(String name) {
    if (name == null)
      return null;
    return getKenmerken().getKenmerk().stream().filter(k -> name.equals(((Kenmerk) k).getNaam())).findFirst().orElse(null);
  }
  
  public List<Objecttype> getSupertypen(boolean excludeStaticOrMixinTypes) {
    return this.getSupertypen().generalisatieObjecttypen.stream()
        .filter(r -> (excludeStaticOrMixinTypes) ? !isStaticOrMixinGeneralisation(r) : true)
        .map(g -> (Objecttype) ((RefTypeEx) g.getSupertype().getObjecttypeRef()).getTarget()).collect(Collectors.toList());
  }
  
  private boolean isStaticOrMixinGeneralisation(GeneralisatieObjecttypen go) {
    if (go.isMixin()) {
      return true;
    }
    Optional<Kenmerk> type = go.getKenmerken().getKenmerk().stream().filter(k -> "type".equals(((Kenmerk) k).getNaam())).findFirst();
    if (type.isEmpty()) {
      return false;
    }
    return "GENERALISATIE STATIC".equals(type.get().getValue());
  }
  
}