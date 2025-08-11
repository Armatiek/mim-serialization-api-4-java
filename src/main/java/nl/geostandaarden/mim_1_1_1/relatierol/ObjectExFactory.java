package nl.geostandaarden.mim_1_1_1.relatierol;

public class ObjectExFactory {

  public static Attribuutsoort createAttribuutsoort() {
    return new AttribuutsoortEx();
  }
  
  public static XhtmlText createXhtmlText() {
    return new XhtmlTextEx();
  }
  
  public static Objecttype createObjecttype() {
    return new ObjecttypeEx();
  }
  
}