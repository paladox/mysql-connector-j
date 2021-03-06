/*
  Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.

  The MySQL Connector/J is licensed under the terms of the GPLv2
  <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most MySQL Connectors.
  There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
  this software, see the FOSS License Exception
  <http://www.mysql.com/about/legal/licensing/foss-exception.html>.

  This program is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation; version 2
  of the License.

  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along with this
  program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
  Floor, Boston, MA 02110-1301  USA

 */

package com.mysql.cj.xdevapi;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mysql.cj.api.result.RowList;
import com.mysql.cj.api.xdevapi.Column;
import com.mysql.cj.api.xdevapi.Row;
import com.mysql.cj.api.xdevapi.RowResult;
import com.mysql.cj.core.result.Field;
import com.mysql.cj.x.core.StatementExecuteOk;
import com.mysql.cj.x.io.DevapiRowFactory;

public class RowResultImpl extends AbstractDataResult<Row> implements RowResult {
    private ArrayList<Field> metadata;
    protected TimeZone defaultTimeZone;

    public RowResultImpl(ArrayList<Field> metadata, TimeZone defaultTimeZone, RowList rows, Supplier<StatementExecuteOk> completer) {
        super(rows, completer, new DevapiRowFactory(metadata, defaultTimeZone));
        this.metadata = metadata;
        this.defaultTimeZone = defaultTimeZone;
    }

    public int getColumnCount() {
        return this.metadata.size();
    }

    public List<Column> getColumns() {
        return this.metadata.stream().map(ColumnImpl::new).collect(Collectors.toList());
    }

    public List<String> getColumnNames() {
        return this.metadata.stream().map(Field::getColumnLabel).collect(Collectors.toList());
    }
}
