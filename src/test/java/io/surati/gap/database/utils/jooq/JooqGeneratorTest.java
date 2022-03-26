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
package io.surati.gap.database.utils.jooq;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case {@link JooqGenerator}.
 *
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class JooqGeneratorTest {

    @Test
    void generatesSimple(@TempDir final Path temp) throws Exception {
        final String pkg = "generated";
        new JooqGenerator(
            "liquibase/db.changelog-master.xml", pkg, "ad_(.*)",
            temp.toString()
        ).start();
        MatcherAssert.assertThat(
            JooqGeneratorTest.listFiles(
                temp.resolve(pkg).resolve("tables").toFile()
            ),
            new IsIterableContaining<>(
                new IsEqual<>("AdPerson.java")
            )
        );
    }

    @Test
    void generatesNestedChangeLogs(@TempDir final Path temp) throws Exception {
        final String pkg = "generated";
        new JooqGenerator(
            "liquibase/db2.split-changelog-master.xml", pkg, "ad_(.*)|log_(.*)",
            temp.toString()
        ).start();
        MatcherAssert.assertThat(
            JooqGeneratorTest.listFiles(
                temp.resolve(pkg).resolve("tables").toFile()
            ),
            Matchers.hasItems("LogEvent.java", "AdPerson.java")
        );
    }

    /**
     * Lists files of a directory.
     *
     * @param dir Directory
     * @return List of files
     */
    private static Set<String> listFiles(final File dir) {
        return Stream.of(dir.listFiles())
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .collect(Collectors.toSet());
    }
}
