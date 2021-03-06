/*
A Java-based HTTP stub server

Copyright (C) 2012 Alexander Zagniotov, Isa Goksu and Eric Mrak

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package by.stub.yaml.stubs;


import by.stub.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Alexander Zagniotov
 * @since 6/14/12, 1:21 AM
 */
@SuppressWarnings("unchecked")
public final class StubHttpLifecycle {

   private String marshalledYaml;
   private StubRequest request;
   private Object response;
   private int responseSequenceCounter = 0;

   public StubHttpLifecycle() {
      response = new StubResponse();
   }

   public void setRequest(final StubRequest request) {
      this.request = request;
   }

   public void setResponse(final Object response) {
      this.response = response;
   }

   public StubRequest getRequest() {
      return request;
   }

   public StubResponse getResponse() {
      return getActualStubbedResponse();
   }


   public List<StubResponse> getAllResponses() {

      if (response instanceof StubResponse) {
         return new LinkedList<StubResponse>() {{
            add((StubResponse) response);
         }};
      }

      return (LinkedList<StubResponse>) response;
   }

   public boolean isRestricted() {
      return StringUtils.isSet(getAuthorizationHeader());
   }

   public boolean hasNotAuthorized(final StubHttpLifecycle assertingLifecycle) {
      final String stubbedAuthorization = getAuthorizationHeader();
      final String assertingAuthorization = assertingLifecycle.getAuthorizationHeader();

      return !stubbedAuthorization.equals(assertingAuthorization);
   }

   private String getAuthorizationHeader() {
      return request.getHeaders().get(StubRequest.AUTH_HEADER);
   }

   public StubResponse getActualStubbedResponse() {

      if (response instanceof StubResponse) {
         return (StubResponse) response;
      }

      final List<StubResponse> responses = (LinkedList<StubResponse>) response;
      if (responses.isEmpty()) {
         return new StubResponse();
      }

      final StubResponse sequenceStubResponse = responses.get(responseSequenceCounter);
      responseSequenceCounter = (responseSequenceCounter + 1 == responses.size() ? responseSequenceCounter = 0 : ++responseSequenceCounter);

      return sequenceStubResponse;
   }

   public String getMarshalledYaml() {
      return marshalledYaml;
   }

   public void setMarshalledYaml(final String marshalledYaml) {
      this.marshalledYaml = marshalledYaml;
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof StubHttpLifecycle)) {
         return false;
      }

      final StubHttpLifecycle that = (StubHttpLifecycle) o;
      if (!request.equals(that.request)) {
         return false;
      }

      return true;
   }
}