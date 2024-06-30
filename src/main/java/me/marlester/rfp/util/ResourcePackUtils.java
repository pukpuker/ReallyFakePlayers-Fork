package me.marlester.rfp.util;

import java.net.MalformedURLException;
import java.net.URI;
import lombok.experimental.UtilityClass;

/**
 * Utils related to resource packs.
 */
@UtilityClass
public class ResourcePackUtils {

  /**
   * Checks if the given resource pack url is valid.
   *
   * @param url resource pack url.
   * @return true if valid, false if not.
   */
  public boolean isValidResourcePackUrl(String url) {
    try {
      var protocol = URI.create(url).toURL().getProtocol();
      return "http".equals(protocol) || "https".equals(protocol);
    } catch (MalformedURLException var3) {
      return false;
    }
  }
}
