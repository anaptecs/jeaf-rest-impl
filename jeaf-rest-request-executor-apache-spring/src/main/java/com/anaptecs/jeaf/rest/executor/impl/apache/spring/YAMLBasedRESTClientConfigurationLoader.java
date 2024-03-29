/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache.spring;

import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;

import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfigurationImpl;

/**
 * Class provided a {@link RESTClientConfiguration} that reads all required configuration parameters from a YAML file.
 * 
 * @author JEAF Development Team
 */
public class YAMLBasedRESTClientConfigurationLoader {
  /**
   * Instances of this class do not make sense.
   */
  private YAMLBasedRESTClientConfigurationLoader( ) {
    // Nothing to do.
  }

  public static RESTClientConfiguration loadConfiguration( String pResource ) {
    Constructor lConstructor = new Constructor(RESTClientConfigurationImpl.class, getSecureSnakeYAMLOptions());
    Yaml lYAML = new Yaml(lConstructor);
    lYAML.setBeanAccess(BeanAccess.FIELD);
    ClassLoader lClassLoader = YAMLBasedRESTClientConfigurationLoader.class.getClassLoader();
    try (InputStream lInputStream = lClassLoader.getResourceAsStream(pResource)) {
      if (lInputStream != null) {
        RESTClientConfigurationImpl lConfiguration = (RESTClientConfigurationImpl) lYAML.load(lInputStream);
        lConfiguration.validate();
        return lConfiguration;
      }
      else {
        throw new IllegalArgumentException("Unable to load YAML configuration. '" + pResource
            + "' cloud not be found in the applications classpath.");
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("Unable to load YAML configuration '" + pResource
          + "'. " + e.getMessage());
    }
  }

  private static LoaderOptions getSecureSnakeYAMLOptions( ) {
    LoaderOptions lLoaderOptions = new LoaderOptions();
    lLoaderOptions.setTagInspector(new TagInspector() {
      @Override
      public boolean isGlobalTagAllowed( Tag pTag ) {
        return false;
      }
    });
    return lLoaderOptions;
  }
}
