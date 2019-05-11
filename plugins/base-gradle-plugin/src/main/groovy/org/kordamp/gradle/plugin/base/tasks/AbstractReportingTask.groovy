/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2019 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin.base.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.kordamp.gradle.AnsiConsole

import static org.kordamp.gradle.StringUtils.isNotBlank

/**
 * @author Andres Almiray
 * @since 0.11.0
 */
@CompileStatic
abstract class AbstractReportingTask extends DefaultTask {
    protected final AnsiConsole console = new AnsiConsole(project)

    protected void doPrint(value, int offset) {
        if (value instanceof Map) {
            doPrintMap((Map) value, offset)
        } else if (value instanceof Collection) {
            doPrintCollection(value, offset)
        } else {
            doPrintElement(value, offset)
        }
    }

    protected void doPrintMap(Map<String, ?> map, int offset) {
        map.each { key, value ->
            if (value instanceof Map) {
                if (!value.isEmpty()) {
                    println(('    ' * offset) + key + ':')
                    doPrintMap(value, offset + 1)
                }
            } else if (value instanceof Collection) {
                if (!value.isEmpty()) {
                    println(('    ' * offset) + key + ':')
                    doPrintCollection((Collection) value, offset + 1)
                }
            } else if (isNotNullNorBlank(value)) {
                doPrintMapEntry(key, value, offset)
            }

            if (offset == 0) {
                println(' ')
            }
        }
    }

    protected void doPrintMapEntry(String key, value, int offset) {
        String result = formatValue(value, offset)
        if (isNotBlank(result)) println(('    ' * offset) + key + ': ' + result)
    }

    protected void doPrintCollection(Collection<?> collection, int offset) {
        collection.each { value ->
            if (value instanceof Map) {
                if (!value.isEmpty()) {
                    doPrintMap(value, offset)
                }
            } else if (value instanceof Collection && !((Collection) value).empty) {
                if (!value.isEmpty()) {
                    doPrintCollection((Collection) value, offset + 1)
                }
            } else if (isNotNullNorBlank(value)) {
                doPrintElement(value, offset)
            }
        }
    }

    protected void doPrintMap(String key, Map<String, ?> map, int offset) {
        if (!map.isEmpty()) {
            println(('    ' * offset) + key + ':')
            doPrintMap(map, offset + 1)
        }
    }

    protected void doPrintCollection(String key, Collection<?> collection, int offset) {
        if (!collection.isEmpty()) {
            println(('    ' * offset) + key + ':')
            doPrintCollection(collection, offset + 1)
        }
    }

    protected void doPrintElement(value, int offset) {
        println(('    ' * offset) + formatValue(value, offset))
    }

    protected boolean isNotNullNorBlank(value) {
        value != null || (value instanceof CharSequence && isNotBlank(String.valueOf(value)))
    }

    protected String formatValue(value, int offset) {
        if (value instanceof Boolean) {
            Boolean b = (Boolean) value
            return b ? console.green(String.valueOf(b)) : console.red(String.valueOf(b))
        } else if (value instanceof Number) {
            return console.cyan(String.valueOf(value))
        } else if (value != null) {
            String s = String.valueOf(value)

            String r = parseAsBoolean(s)
            if (r != null) return r
            r = parseAsInteger(s)
            if (r != null) return r
            r = parseAsDouble(s)
            if (r != null) return r

            return console.yellow(s)
        }
        return value
    }

    protected String parseAsBoolean(String s) {
        if ('true'.equalsIgnoreCase(s) || 'false'.equalsIgnoreCase(s)) {
            boolean b = Boolean.valueOf(s)
            return b ? console.green(String.valueOf(b)) : console.red(String.valueOf(b))
        } else {
            return null
        }
    }

    protected String parseAsInteger(String s) {
        try {
            Integer.parseInt(s)
            return console.cyan(s)
        } catch (Exception e) {
            return null
        }
    }

    protected String parseAsDouble(String s) {
        try {
            Double.parseDouble(s)
            return console.cyan(s)
        } catch (Exception e) {
            return null
        }
    }
}
