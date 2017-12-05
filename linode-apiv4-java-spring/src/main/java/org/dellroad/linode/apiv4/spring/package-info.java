
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

/**
 * Linode v4 API Spring integration.
 *
 * <p><b>Overview</b></p>
 *
 * <p>
 * Here are the primary classes provided:
 * <ul>
 *  <li>{@link org.dellroad.linode.apiv4.spring.LinodeApiRequestSender} - Java version of the API</li>
 *  <li>{@link org.dellroad.linode.apiv4.spring.LinodeApiHttpRequestFactory} - configures how HTTP requests
 *      are performed, including token authorization</li>
 *  <li>{@link org.dellroad.linode.apiv4.spring.LinodeApiException} - thrown if there is an error, including
 *      a decode of the JSON error payload (if any)</li>
 * </ul>
 *
 * <p><b>XML Setup</b></p>
 *
 * <p>
 * This package comes with pre-configured Spring XML beans that you can include in your Spring application:
 * <pre>
 *     &lt;import resource="classpath:org/dellroad/linode/apiv4/spring/linodeApi.xml"/&gt;
 * </pre>
 *
 * <p>
 * The XML sets up a {@link org.dellroad.linode.apiv4.spring.LinodeApiRequestSender} which can be used to perform API queries:
 * <pre>
 *  import org.dellroad.linode.apiv4.model.{@link org.dellroad.linode.apiv4.model.Linode};
 *  import org.dellroad.linode.apiv4.request.{@link org.dellroad.linode.apiv4.request.CreateLinodeRequest};
 *  import org.dellroad.linode.apiv4.spring.{@link org.dellroad.linode.apiv4.spring.LinodeApiHttpRequestFactory};
 *  import org.dellroad.linode.apiv4.spring.{@link org.dellroad.linode.apiv4.spring.LinodeApiRequestSender};
 *  import org.springframework.beans.factory.annotation.Autowired;
 *
 *  public class MyBean {
 *
 *      &#64;Autowired
 *      private {@link org.dellroad.linode.apiv4.spring.LinodeApiRequestSender} sender;
 *
 *      &#64;Autowired
 *      private {@link org.dellroad.linode.apiv4.spring.LinodeApiHttpRequestFactory} requestFactory;
 *
 *      // Throws {@link org.dellroad.linode.apiv4.spring.LinodeApiException} if something goes wrong
 *      public Linode createLinode() {
 *
 *          // Set authorization token (you only need to do this once)
 *          this.requestFactory.setAuthorizationToken("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
 *
 *          // Setup request
 *          final CreateLinodeRequest request = new CreateLinodeRequest}();
 *          request.setTypeId("g5-nanode-1");
 *          request.setRegionId("us-west-1a")
 *          request.setLabel("MyLinode");
 *          request.setGroup("MyLinodeGroup");
 *
 *          // Send request and return the newly created Linode
 *          return this.sender.createLinode(request);
 *      }
 *  }
 * </pre>
 *
 * @see <a href="http://projects.spring.io/spring-framework/">Spring Framework</a>
 */
package org.dellroad.linode.apiv4.spring;
