package com.axelor.cache;

import com.axelor.app.AppSettings;
import com.axelor.test.GuiceModules;
import org.hibernate.cache.jcache.ConfigSettings;

@GuiceModules(EhcacheTest.EhcacheTestModule.class)
public class EhcacheTest extends AbstractBaseCache {

  public static class EhcacheTestModule extends CacheTestModule {

    @Override
    protected void configure() {
      resetSettings();

      AppSettings.get()
          .getProperties()
          .put(ConfigSettings.PROVIDER, "org.ehcache.jsr107.EhcacheCachingProvider");

      super.configure();
    }
  }
}