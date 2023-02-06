/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
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
package com.axelor.db;

import static org.junit.Assert.*;

import com.axelor.JpaTestModule;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;
import com.axelor.test.db.FieldTypes;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(GuiceRunner.class)
@GuiceModules({JpaTestModule.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JpaFixtureTest extends JpaSupport {

  @Inject private JpaFixture fixture;

  private FieldTypes fieldTypes;

  @Before
  @Transactional
  public void setUp() {
    if (all(FieldTypes.class).count() == 0) {
      fixture.load("demo-field-types.yml");
    }
  }

  @Test
  public void testString() {
    whenLoadTypeFields();
    expectString();
  }

  private void expectString() {
    assertEquals("Hello, Axelor!", fieldTypes.getString());
  }

  @Test
  public void testLocalDate() {
    whenLoadTypeFields();
    expectLocalDate();
  }

  private void expectLocalDate() {
    assertEquals(LocalDate.parse("2021-04-29"), fieldTypes.getLocalDate());
  }

  @Test
  public void testLocalDateTime() {
    whenLoadTypeFields();
    expectLocalDateTime();
  }

  private void expectLocalDateTime() {
    assertEquals(LocalDateTime.of(2021, 4, 29, 7, 57, 0), fieldTypes.getLocalDateTime());
  }

  @Test
  public void testLocalTime() {
    whenLoadTypeFields();
    expectLocalTime();
  }

  private void expectLocalTime() {
    assertEquals(LocalTime.of(7, 57), fieldTypes.getLocalTime());
  }

  private void whenLoadTypeFields() {
    fieldTypes = all(FieldTypes.class).fetchOne();
  }
}
