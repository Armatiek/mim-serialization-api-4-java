package nl.geostandaarden.mim_1_1_0.relatierol;

public class ObjectExFactory {

  public static Attribuutsoort createAttribuutsoort() {
    return new AttribuutsoortEx();
  }
  
  public static XhtmlText createXhtmlText() {
    return new XhtmlTextEx();
  }
  
}