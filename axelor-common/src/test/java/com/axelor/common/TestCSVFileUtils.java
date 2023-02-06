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
package com.axelor.common;

import com.axelor.common.csv.CSVFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

public class TestCSVFileUtils {

  @Test
  public void testReadCsv() throws IOException {
    CSVParser parser = parse("grades.csv");

    Assert.assertEquals(9, parser.getHeaderNames().size());
    Assert.assertEquals("Lastname", parser.getHeaderNames().get(0));
    Assert.assertEquals(16, parser.getRecords().size());
  }

  @Test
  public void testReadCsvWithBom() throws IOException {
    CSVParser bomParser = parse("grades_bom.csv");

    // Without handle files that start with a Byte Order Mark (BOM), we would have ["Lastname"] as
    // first header name instead of [Lastname]
    Assert.assertEquals("Lastname", bomParser.getHeaderNames().get(0));

    // Check if records are strictly identical to file without BOM
    CSVParser parser = parse("grades.csv");

    List<CSVRecord> records = parser.getRecords();
    List<CSVRecord> bomRecords = bomParser.getRecords();

    for (int i = 0; i < bomRecords.size(); i++) {
      for (int j = 0; j < bomRecords.get(i).size(); j++) {
        if (!Objects.equals(bomRecords.get(i).get(j), records.get(i).get(j))) {
          Assert.fail("Records are not same");
        }
      }
    }

    Map<String, Integer> headers = parser.getHeaderMap();
    Map<String, Integer> bomHeaders = bomParser.getHeaderMap();

    for (String key : bomHeaders.keySet()) {
      if (!Objects.equals(headers.get(key), bomHeaders.get(key))) {
        Assert.fail("Headers are not same");
      }
    }
  }

  private CSVParser parse(String fileName) throws IOException {
    File file = new File(ResourceUtils.getResource(fileName).getFile());
    return CSVFile.DEFAULT.withFirstRecordAsHeader().parse(file);
  }
}
