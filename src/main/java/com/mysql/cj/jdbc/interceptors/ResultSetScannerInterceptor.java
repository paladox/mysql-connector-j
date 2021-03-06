/*
  Copyright (c) 2007, 2017, Oracle and/or its affiliates. All rights reserved.

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

package com.mysql.cj.jdbc.interceptors;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.cj.api.MysqlConnection;
import com.mysql.cj.api.Query;
import com.mysql.cj.api.interceptors.QueryInterceptor;
import com.mysql.cj.api.io.ServerSession;
import com.mysql.cj.api.jdbc.result.ResultSetInternalMethods;
import com.mysql.cj.api.log.Log;
import com.mysql.cj.api.mysqla.result.Resultset;
import com.mysql.cj.core.Messages;
import com.mysql.cj.core.conf.PropertyDefinitions;
import com.mysql.cj.core.exceptions.ExceptionFactory;
import com.mysql.cj.core.exceptions.WrongArgumentException;

public class ResultSetScannerInterceptor implements QueryInterceptor {

    protected Pattern regexP;

    public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
        String regexFromUser = props.getProperty(PropertyDefinitions.PNAME_resultSetScannerRegex);

        if (regexFromUser == null || regexFromUser.length() == 0) {
            throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.0"));
        }

        try {
            this.regexP = Pattern.compile(regexFromUser);
        } catch (Throwable t) {
            throw ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ResultSetScannerInterceptor.1"), t);
        }
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Resultset> T postProcess(String sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {

        // requirement of anonymous class
        final T finalResultSet = originalResultSet;

        return (T) Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class<?>[] { Resultset.class, ResultSetInternalMethods.class },
                new InvocationHandler() {

                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if ("equals".equals(method.getName())) {
                            // Let args[0] "unwrap" to its InvocationHandler if it is a proxy.
                            return args[0].equals(this);
                        }

                        Object invocationResult = method.invoke(finalResultSet, args);

                        String methodName = method.getName();

                        if (invocationResult != null && invocationResult instanceof String || "getString".equals(methodName) || "getObject".equals(methodName)
                                || "getObjectStoredProc".equals(methodName)) {
                            Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());

                            if (matcher.matches()) {
                                throw new SQLException(Messages.getString("ResultSetScannerInterceptor.2"));
                            }
                        }

                        return invocationResult;
                    }
                });

    }

    public <T extends Resultset> T preProcess(String sql, Query interceptedQuery) {
        // we don't care about this event

        return null;
    }

    // we don't issue queries, so it should be safe to intercept at any point
    public boolean executeTopLevelOnly() {
        return false;
    }

    public void destroy() {

    }
}