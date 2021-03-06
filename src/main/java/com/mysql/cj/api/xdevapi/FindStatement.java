/*
  Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.

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

package com.mysql.cj.api.xdevapi;

import com.mysql.cj.xdevapi.DbDoc;

/**
 * A statement to <i>find</i> the set of documents according to the given specification.
 */
public interface FindStatement extends DataStatement<FindStatement, DocResult, DbDoc> {
    /**
     * Add/replace the field projections defining the result.
     * 
     * @param projections
     *            projection expression
     * @return {@link FindStatement}
     */
    FindStatement fields(String... projections);

    /**
     * Add/replace the field projection defining the result.
     * 
     * @param docProjection
     *            projection expression
     * @return {@link FindStatement}
     */
    FindStatement fields(Expression docProjection);

    /**
     * Add/replace the aggregation fields for this query.
     * 
     * @param groupBy
     *            groupBy expression
     * @return {@link FindStatement}
     */
    FindStatement groupBy(String... groupBy);

    /**
     * Add/replace the aggregate criteria for this query.
     * 
     * @param having
     *            having expression
     * @return {@link FindStatement}
     */
    FindStatement having(String having);

    /**
     * Add/replace the order specification for this query.
     * 
     * @param sortFields
     *            sort expression
     * @return {@link FindStatement}
     */
    FindStatement orderBy(String... sortFields);

    /**
     * Add/replace the order specification for this query.
     * <p>
     * Synonym for {@link #orderBy(String...)}
     * 
     * @param sortFields
     *            sort expression
     * @return {@link FindStatement}
     */
    FindStatement sort(String... sortFields);

    /**
     * Add/replace the document offset for this query.
     * 
     * @param limitOffset
     *            number of documents to skip
     * @return {@link FindStatement}
     */
    FindStatement skip(long limitOffset);

    /**
     * Add/replace the document limit for this query.
     * 
     * @param numberOfRows
     *            limit
     * @return {@link FindStatement}
     */
    FindStatement limit(long numberOfRows);

    /**
     * Locks matching rows against updates.
     * 
     * @return {@link FindStatement}
     */
    FindStatement lockShared();

    /**
     * Locks matching rows exclusively so no other transactions can read or write to them.
     * 
     * @return {@link FindStatement}
     */
    FindStatement lockExclusive();
}
