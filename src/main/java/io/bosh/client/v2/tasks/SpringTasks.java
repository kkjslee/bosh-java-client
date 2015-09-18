/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bosh.client.v2.tasks;

import java.net.URI;
import java.util.Arrays;

import org.springframework.web.client.RestOperations;

import io.bosh.client.v2.internal.AbstractSpringOperations;
import rx.Observable;

/**
 * @author David Ehringer
 */
public class SpringTasks extends AbstractSpringOperations implements Tasks {

    private static final int DEFAULT_RECENT_TASK_COUNT = 30;
    
    public SpringTasks(RestOperations restOperations, URI root) {
        super(restOperations, root);
    }

    @Override
    public Observable<ListTasksResponse> listRunning() {
        return get(Task[].class,
                builder -> builder.pathSegment("tasks")
                                   .queryParam("state", "processing,cancelling,queued"))
               .map(response -> new ListTasksResponse().withErrands(Arrays.asList(response)));
    }

    @Override
    public Observable<ListTasksResponse> listRecent() {
        return listRecent(DEFAULT_RECENT_TASK_COUNT);
    }

    @Override
    public Observable<ListTasksResponse> listRecent(int count) {
        return get(Task[].class,
                builder -> builder.pathSegment("tasks")
                                   .queryParam("limit", count)
                                   .queryParam("verbose", 1))
               .map(response -> new ListTasksResponse().withErrands(Arrays.asList(response)));
    }

    @Override
    public Observable<Task> get(String id) {
        return get(Task.class, builder -> builder.pathSegment("tasks", id));
    }

}