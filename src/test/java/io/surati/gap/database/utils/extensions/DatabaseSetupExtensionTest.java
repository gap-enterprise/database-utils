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

import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import java.sql.Connection;
import java.sql.SQLException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.llorllale.cactoos.matchers.Satisfies;

/**
 * Test case for {@link DatabaseSetupExtension}.
 * <p>
 *     We notice that the database is created once for a data source.
 *     We recommend to use it with it constructor if you want to share
 *     a variable with all tests.
 * </p>
 *
 * @since 0.1
 */
final class DatabaseSetupExtensionTest {

    /**
     * Database setup extension.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @RegisterExtension
    final DatabaseSetupExtension src = new DatabaseSetupExtension(
        "liquibase/db.changelog-master.xml"
    );

    /**
     * Database connection.
     */
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        this.connection = this.src.getConnection();
    }

    @Test
    void checksIfStillCurrent() throws SQLException {
        new JdbcSession(this.src)
            .sql("INSERT INTO ad_person (name) VALUES (?)")
            .set("Olivier B. OURA")
            .insert(Outcome.VOID);
        MatcherAssert.assertThat(
            this.src.getConnection(),
            new Satisfies<>(
                conn -> conn.toString().equals(this.connection.toString())
            )
        );
    }

    @Test
    void attemptsToClose() throws SQLException {
        this.connection.close();
        MatcherAssert.assertThat(
            this.connection.isClosed(),
            Matchers.is(false)
        );
    }

    @Test
    void attemptsToCommit() throws SQLException {
        this.connection.commit();
        MatcherAssert.assertThat(
            this.connection.isClosed(),
            Matchers.is(false)
        );
    }
}
