/**
 * Copyright 2012 Julien Eluard
 * This project includes software developed by Julien Eluard: https://github.com/jeluard/
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
package org.semver.jardiff;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import org.osjava.jardiff.AbstractDiffHandler;
import org.osjava.jardiff.ClassInfo;
import org.osjava.jardiff.DiffException;
import org.osjava.jardiff.FieldInfo;
import org.osjava.jardiff.MethodInfo;
import org.semver.Delta;
import org.semver.Delta.Add;
import org.semver.Delta.Change;
import org.semver.Delta.Difference;
import org.semver.Delta.Remove;

/**
 *
 * {@link org.osjava.jardiff.DiffHandler} implementation accumulating {@link Difference}.
 *
 */
public final class DifferenceAccumulatingHandler extends AbstractDiffHandler {
    
    private String currentClassName;
    private final Set<String> includes;
    private final Set<String> excludes;
    private final Set<Difference> differences = new HashSet<Difference>();

    public DifferenceAccumulatingHandler() {
        this(Collections.<String>emptySet(), Collections.<String>emptySet());
    }

    public DifferenceAccumulatingHandler(@Nonnull final Set<String> includes, @Nonnull final Set<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    public String getCurrentClassName() {
        return this.currentClassName;
    }
    
    @Override
    public void startDiff(final String previous, final String current) throws DiffException {
    }

    @Override
    public void endDiff() throws DiffException {
    }

    @Override
    public void startOldContents() throws DiffException {
    }

    @Override
    public void endOldContents() throws DiffException {
    }

    @Override
    public void startNewContents() throws DiffException {
    }

    @Override
    public void endNewContents() throws DiffException {
    }

    @Override
    public void contains(final ClassInfo classInfo) throws DiffException {
    }

    @Override
    public void startAdded() throws DiffException {
    }

    @Override
    public void classAdded(final ClassInfo classInfo) throws DiffException {
        if (!isClassConsidered(classInfo.getName())) {
            return;
        }

        this.differences.add(new Add(getClassName(classInfo.getName()), classInfo));
    }

    @Override
    public void fieldAdded(final FieldInfo fieldInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Add(getCurrentClassName(), fieldInfo));
    }

    @Override
    public void methodAdded(final MethodInfo methodInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Add(getCurrentClassName(), methodInfo));
    }

    @Override
    public void endAdded() throws DiffException {
    }

    @Override
    public void startChanged() throws DiffException {
    }

    @Override
    public void startClassChanged(final String className) throws DiffException {
        this.currentClassName = getClassName(className);
    }

    @Override
    public void classChanged(final ClassInfo oldClassInfo, final ClassInfo newClassInfo) throws DiffException {
        if (!isClassConsidered(oldClassInfo.getName())) {
            return;
        }

        this.differences.add(new Change(getClassName(oldClassInfo.getName()), oldClassInfo, newClassInfo));
    }

    @Override
    public void fieldChanged(final FieldInfo oldFieldInfo, final FieldInfo newFieldInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Change(getCurrentClassName(), oldFieldInfo, newFieldInfo));
    }

    @Override
    public void methodChanged(final MethodInfo oldMethodInfo, final MethodInfo newMethodInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Change(getCurrentClassName(), oldMethodInfo, newMethodInfo));
    }

    @Override
    public void endClassChanged() throws DiffException {
    }

    @Override
    public void endChanged() throws DiffException {
    }

    @Override
    public void startRemoved() throws DiffException {
    }

    @Override
    public void classRemoved(final ClassInfo classInfo) throws DiffException {
        if (!isClassConsidered(classInfo.getName())) {
            return;
        }

        this.differences.add(new Remove(getClassName(classInfo.getName()), classInfo));
    }

    @Override
    public void fieldRemoved(final FieldInfo fieldInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Remove(getCurrentClassName(), fieldInfo));
    }

    @Override
    public void methodRemoved(final MethodInfo methodInfo) throws DiffException {
        if (!isClassConsidered(getCurrentClassName())) {
            return;
        }

        this.differences.add(new Remove(getCurrentClassName(), methodInfo));
    }

    @Override
    public void endRemoved() throws DiffException {
    }

    /**
     *
     * Is considered a class whose package:
     * * is included
     * * is not excluded
     *
     * If includes are provided then package must be defined here.
     *
     * @return
     */
    private boolean isClassConsidered(final String className) {
        for (final String exclude : this.excludes) {
            if (className.startsWith(exclude)) {
                return false;
            }
        }

        if (!this.includes.isEmpty()) {
            for (final String include : this.includes) {
                if (className.startsWith(include)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public Delta getDelta() {
        return new Delta(this.differences);
    }

}
