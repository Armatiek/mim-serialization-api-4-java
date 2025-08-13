package nl.geostandaarden.mim_1_1_0.relatierol;

import java.util.List;
import java.util.Optional;

import nl.geostandaarden.mim_1_1_0.relatierol.ext.Kenmerken.Kenmerk;
import nl.geostandaarden.mim_1_1_0.relatierol.ref.RefTypeEx;

public class ObjecttypeEx extends Objecttype {
  
  public Optional<Attribuutsoort> getAttribuutsoort(String name) {
    if (name == null)
      return null;
    return getAttribuutsoorten().getAttribuutsoort().stream().filter(a -> name.equals(((Attribuutsoort) a).getNaam())).findFirst();
  }
  
  public Optional<Gegevensgroep> getGegevensgroep(String name) {
    if (name == null)
      return null;
    return getGegevensgroepen().getGegevensgroep().stream().filter(g -> name.equals(((Gegevensgroep) g).getNaam())).findFirst();
  }
  
  public Optional<Relatiesoort> getRelatiesoort(String name) {
    if (name == null)
      return null;
    return getRelatiesoorten().getRelatiesoort().stream().filter(r -> name.equals(((Relatiesoort) r).getNaam())).findFirst();
  }
  
  public Optional<Keuze> getKeuze(String name) {
    if (name == null)
      return null;
    return getKeuzen().getKeuzeRef().stream()
        .filter(r -> name.equals(((Keuze) ((RefTypeEx) r).getTarget()).getNaam()))
        .map(r -> (Keuze) ((RefTypeEx) r).getTarget()).findFirst();
  }
  
  public Optional<Constraint> getConstraint(String name) {
    if (name == null)
      return null;
    return getConstraints().getConstraint().stream().filter(c -> name.equals(((Constraint) c).getNaam())).findFirst();
  }
  
  public Optional<Kenmerk> getKenmerk(String name) {
    if (name == null)
      return null;
    return getKenmerken().getKenmerk().stream().filter(k -> name.equals(((Kenmerk) k).getNaam())).findFirst();
  }
  
  public List<Objecttype> getSupertypen(boolean excludeStaticTypes) {
    return this.getSupertypen().generalisatieObjecttypen.stream()
        .filter(r -> (excludeStaticTypes) ? !isStaticGeneralisation(r) : true)
        .map(g -> (Objecttype) ((RefTypeEx) g.getSupertype().getObjecttypeRef()).getTarget()).toList();
  }
  
  private boolean isStaticGeneralisation(GeneralisatieObjecttypen go) {
    Optional<Kenmerk> type = go.getKenmerken().getKenmerk().stream().filter(k -> "type".equals(((Kenmerk) k).getNaam())).findFirst();
    if (type.isEmpty()) {
      return false;
    }
    return "GENERALISATIE STATIC".equals(type.get().getValue());
  }
  
}