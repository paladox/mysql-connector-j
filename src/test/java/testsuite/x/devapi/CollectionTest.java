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

package testsuite.x.devapi;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysql.cj.api.xdevapi.Collection;
import com.mysql.cj.api.xdevapi.DatabaseObject.DbObjectStatus;
import com.mysql.cj.core.exceptions.MysqlErrorNumbers;
import com.mysql.cj.core.exceptions.WrongArgumentException;
import com.mysql.cj.x.core.XDevAPIError;

public class CollectionTest extends DevApiBaseTestCase {
    /** Collection for testing. */
    protected Collection collection;
    protected String collectionName;

    @Before
    public void setupCollectionTest() {
        if (setupTestSession()) {
            this.collectionName = "CollectionTest-" + new Random().nextInt(1000);
            dropCollection(this.collectionName);
            this.collection = this.schema.createCollection(this.collectionName);
        }
    }

    @After
    public void teardownCollectionTest() {
        if (this.isSetForXTests && this.session.isOpen()) {
            try {
                dropCollection(this.collectionName);
            } catch (Exception ex) {
                System.err.println("Error during cleanup teardownCollectionTest()");
                ex.printStackTrace();
            }
            destroyTestSession();
        }
    }

    @Test
    public void testCount() {
        if (!this.isSetForXTests) {
            return;
        }
        this.collection.add("{'a':'a'}".replaceAll("'", "\"")).execute();
        this.collection.add("{'b':'b'}".replaceAll("'", "\"")).execute();
        this.collection.add("{'c':'c'}".replaceAll("'", "\"")).execute();
        assertEquals(3, this.collection.count());
    }

    @Test
    public void testExists() {
        if (!this.isSetForXTests) {
            return;
        }
        String collName = "testExists";
        dropCollection(collName);
        Collection coll = this.schema.getCollection(collName);
        assertEquals(DbObjectStatus.NOT_EXISTS, coll.existsInDatabase());
        coll = this.schema.createCollection(collName);
        assertEquals(DbObjectStatus.EXISTS, coll.existsInDatabase());
        this.schema.dropCollection(collName);
    }

    @Test(expected = WrongArgumentException.class)
    public void getNonExistentCollectionWithRequireExistsShouldThrow() {
        if (!this.isSetForXTests) {
            throw new WrongArgumentException("Throw WrongArgumentException as expected, but test was ignored because of missed configuration.");
        }
        String collName = "testRequireExists";
        dropCollection(collName);
        this.schema.getCollection(collName, true);
    }

    @Test
    public void getNonExistentCollectionWithoutRequireExistsShouldNotThrow() {
        if (!this.isSetForXTests) {
            return;
        }
        String collName = "testRequireExists";
        dropCollection(collName);
        this.schema.getCollection(collName, false);
    }

    @Test
    public void getExistentCollectionWithRequireExistsShouldNotThrow() {
        if (!this.isSetForXTests) {
            return;
        }
        String collName = "testRequireExists";
        dropCollection(collName);
        this.schema.createCollection(collName);
        this.schema.getCollection(collName, true);
    }

    @Test
    public void createIndex() {
        if (!this.isSetForXTests) {
            return;
        }
        this.collection.createIndex("x_idx", true).field(".x", "INT", true).execute();
        this.collection.add("{'x':'1'}".replaceAll("'", "\"")).execute();
        this.collection.add("{'x':'2'}".replaceAll("'", "\"")).execute();
        try {
            // fail due to duplicate value for unique index on "x"
            this.collection.add("{'x':'1'}".replaceAll("'", "\"")).execute();
        } catch (XDevAPIError err) {
            assertEquals(MysqlErrorNumbers.ER_X_DOC_ID_DUPLICATE, err.getErrorCode());
        }

        // dropping non-existing index should not fail
        this.collection.dropIndex("non_existing_idx");

        // drop the index and we can now insert what was a duplicate key entry
        this.collection.dropIndex("x_idx");
        this.collection.add("{'x':'1'}".replaceAll("'", "\"")).execute();
    }
}
