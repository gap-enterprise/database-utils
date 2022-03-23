/*
 * Copyright (c) 2022 Surati
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.surati.gap.database.utils.extensions;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link SampleDataSourceExtension} with before each support.
 * <p>
 *     We notice that the database is created for each test.
 *     We recommend to use only {@link SampleDataSourceExtension} with constructor
 *     if you want to share a variable with all tests.
 * </p>
 *
 * @since 0.1
 */
@ExtendWith(SampleDataSourceExtension.class)
@ExtendWith(DataSourceBeforeEachResolver.class)
final class SampleDataSourceBeforeEachExtensionTest {

    /**
     * Database driver.
     */
    private String driver;

    @BeforeEach
    void setup(final DataSource src) throws SQLException {
        try (Connection conn = src.getConnection()) {
            this.driver = conn.getMetaData().getDriverName();
        }
    }

    @TestTemplate
    void readsDriver(final DataSource src) throws SQLException {
        try (Connection conn = src.getConnection()) {
            MatcherAssert.assertThat(
                conn.getMetaData().getDriverName(),
                new IsEqual<>(this.driver)
            );
        }
    }

    @TestTemplate
    void readsUrl(final DataSource src) throws SQLException {
        try (Connection conn = src.getConnection()) {
            MatcherAssert.assertThat(
                conn.getMetaData().getURL(),
                Matchers.startsWith("jdbc:h2:~/test_")
            );
        }
    }
}
