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
package io.bosh.client.v2.deployments;

import io.bosh.client.v2.DirectorException;
import io.bosh.client.v2.internal.AbstractSpringOperations;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import rx.Observable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author David Ehringer
 */
public class SpringDeployments extends AbstractSpringOperations implements Deployments {

    public SpringDeployments(RestTemplate restTemplate, URI root) {
        super(restTemplate, root);
    }

    @Override
    public Observable<ListDeploymentsResponse> list() {
        return get(Deployment[].class, 
                builder -> builder.pathSegment("deployments"))
               .map(results -> new ListDeploymentsResponse().withDeployments(Arrays.asList(results)));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Observable<GetDeploymentResponse> get(GetDeploymentRequest request) {
        return get(GetDeploymentResponse.class, 
                   builder -> builder.pathSegment("deployments", request.getName()))
               .map(response -> {
                   response.setName(request.getName());

                   ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                   Map manifestMap = null;
                   try {
                       manifestMap = mapper.readValue(response.getRawManifest(), Map.class);
                   } catch (IOException e) {
                       throw new DirectorException("Unable to parse deployment manifest", e);
                   }
                   response.setManifestMap(manifestMap);
                   return response;
               });
    }

}