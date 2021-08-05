/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2021 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.data.xml;

import com.axelor.common.StringUtils;
import com.axelor.db.mapper.JsonProperty;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XMLBindConverter implements Converter {
  private final Converter defaultConverter;
  private final ReflectionProvider reflectionProvider;

  public XMLBindConverter(Converter defaultConverter, ReflectionProvider reflectionProvider) {
    this.defaultConverter = defaultConverter;
    this.reflectionProvider = reflectionProvider;
  }

  @Override
  public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
    return XMLBind.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    final String field = reader.getAttribute("to");

    if (StringUtils.isBlank(field)) {
      final String jsonModel = reader.getAttribute("json-model");
      return StringUtils.isBlank(jsonModel)
          ? newInstance(context)
          : newInstanceJson(context, jsonModel);
    }

    return field.startsWith(JsonProperty.KEY_JSON_PREFIX)
        ? newInstanceJson(context, reader.getAttribute("json-model"))
        : newInstance(context);
  }

  private Object newInstance(UnmarshallingContext context) {
    return newInstance(XMLBind.class, context);
  }

  private Object newInstanceJson(UnmarshallingContext context, String jsonModel) {
    final Object result = newInstance(XMLBindJson.class, context);

    if (StringUtils.notBlank(jsonModel)) {
      reflectionProvider.writeField(result, "jsonModel", jsonModel, XMLBindJson.class);
    }

    return result;
  }

  private Object newInstance(Class<?> resultType, UnmarshallingContext context) {
    final Object result = reflectionProvider.newInstance(resultType);
    return context.convertAnother(result, resultType, defaultConverter);
  }
}