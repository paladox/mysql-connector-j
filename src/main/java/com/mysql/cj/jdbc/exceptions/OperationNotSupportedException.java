/*
  Copyright (c) 2004, 2017, Oracle and/or its affiliates. All rights reserved.

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

package com.mysql.cj.jdbc.exceptions;

import java.sql.SQLException;

import com.mysql.cj.core.Messages;
import com.mysql.cj.core.exceptions.MysqlErrorNumbers;

public class OperationNotSupportedException extends SQLException {

    static final long serialVersionUID = 474918612056813430L;

    public OperationNotSupportedException() {
        super(Messages.getString("RowDataDynamic.10"), MysqlErrorNumbers.SQL_STATE_ILLEGAL_ARGUMENT);
    }

    public OperationNotSupportedException(String message) {
        super(message, MysqlErrorNumbers.SQL_STATE_ILLEGAL_ARGUMENT);
    }
}