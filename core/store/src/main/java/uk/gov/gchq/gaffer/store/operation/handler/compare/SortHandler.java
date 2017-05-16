/*
 * Copyright 2017 Crown Copyright
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
package uk.gov.gchq.gaffer.store.operation.handler.compare;

import uk.gov.gchq.gaffer.commonutil.stream.GafferCollectors;
import uk.gov.gchq.gaffer.commonutil.stream.Streams;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.compare.Sort;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import java.util.Comparator;
import java.util.stream.Stream;

public class SortHandler implements OutputOperationHandler<Sort, Iterable<? extends Element>> {
    @Override
    public Iterable<? extends Element> doOperation(final Sort operation, final Context context, final Store store) throws OperationException {
        // If the input or comparator is null, we return null
        if (null == operation.getInput()) {
            return null;
        }

        if (null == operation.getComparator()) {
            return operation.getInput();
        }

        Comparator<Element> comparator = operation.getComparator();
        if (operation.isReversed()) {
            comparator = comparator.reversed();
        }

        try (Stream<? extends Element> stream =
                     Streams.toStream(operation.getInput())
                             .filter(e -> null != e)) {
            return stream.collect(GafferCollectors.toLimitedSortedSet(comparator,
                    operation.getResultLimit()));
        }
    }
}
