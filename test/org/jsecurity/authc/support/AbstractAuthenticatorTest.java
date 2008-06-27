/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jsecurity.authc.support;

import static org.easymock.EasyMock.*;
import org.jsecurity.authc.*;
import org.jsecurity.authc.event.AuthenticationEvent;
import org.jsecurity.authc.event.AuthenticationEventListener;
import org.jsecurity.authc.event.FailedAuthenticationEvent;
import org.jsecurity.authc.event.SuccessfulAuthenticationEvent;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Les Hazlewood
 * @since 0.1
 */
public class AbstractAuthenticatorTest {

    AbstractAuthenticator abstractAuthenticator;
    private final SimpleAccount account = new SimpleAccount("user1", "secret", "realmName");

    private AbstractAuthenticator createAuthcReturnNull() {
        return new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                return null;
            }
        };
    }

    private AbstractAuthenticator createAuthcReturnValidAuthcInfo() {
        return new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                return account;
            }
        };
    }

    private AuthenticationToken newToken() {
        return new UsernamePasswordToken("user1", "secret".toCharArray());
    }

    @Before
    public void setUp() {
        abstractAuthenticator = createAuthcReturnValidAuthcInfo();
    }

    @Test
    public void newAbstractAuthenticatorSecurityManagerConstructor() {
        abstractAuthenticator = new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                return account;
            }
        };
    }


    /**
     * Ensures that the authenticate() method proactively fails if a <tt>null</tt> AuthenticationToken is passed as an
     * argument.
     */
    @Test(expected = IllegalArgumentException.class)
    public void authenticateWithNullArgument() {
        abstractAuthenticator.authenticate(null);
    }

    /**
     * Ensures that the authenticate() method throws an AuthenticationException if the subclass returns <tt>null</tt>
     * as the return value to the doAuthenticate() method.
     */
    @Test(expected = AuthenticationException.class)
    public void throwAuthenticationExceptionIfDoAuthenticateReturnsNull() {
        abstractAuthenticator = createAuthcReturnNull();
        abstractAuthenticator.authenticate(newToken());
    }

    /**
     * Ensures a non-null <tt>Subject</tt> instance is returned from the authenticate() method after a valid
     * authentication attempt (i.e. the subclass's doAuthenticate implementation returns a valid, non-null
     * Account object).
     */
    @Test
    public void nonNullAccountAfterAuthenticate() {
        Account authcInfo = abstractAuthenticator.authenticate(newToken());
        assertNotNull(authcInfo);
    }

    @Test(expected = AuthenticationException.class)
    public void createFailureEventReturnsNull() {
        abstractAuthenticator = new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                throw new AuthenticationException();
            }
        };
        abstractAuthenticator.authenticate(newToken());
    }

    @Test
    public void createSuccessEventReturnsNull() {
        abstractAuthenticator = new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                return account;
            }
        };
        abstractAuthenticator.authenticate(newToken());
    }

    @Test
    public void sendSuccessEventAfterDoAuthenticate() {
        AuthenticationEventListener mockListener = createMock(AuthenticationEventListener.class);
        abstractAuthenticator.add(mockListener);
        AuthenticationToken token = newToken();
        AuthenticationEvent successEvent = new SuccessfulAuthenticationEvent(token, account);

        mockListener.onEvent(isA(SuccessfulAuthenticationEvent.class));

        replay(mockListener);
        abstractAuthenticator.authenticate(token);
        verify(mockListener);
    }

    @Test
    public void sendFailedEventAfterDoAuthenticateThrowsAuthenticationException() {
        AuthenticationEventListener mockListener = createMock(AuthenticationEventListener.class);
        AuthenticationToken token = newToken();

        final AuthenticationException ae = new AuthenticationException("dummy exception to test event sending");

        abstractAuthenticator = new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                throw ae;
            }
        };
        abstractAuthenticator.add(mockListener);

        mockListener.onEvent(isA(FailedAuthenticationEvent.class));
        replay(mockListener);

        boolean exceptionThrown = false;
        try {
            abstractAuthenticator.authenticate(token);
        } catch (AuthenticationException e) {
            exceptionThrown = true;
            assertEquals(e, ae);
        }
        verify(mockListener);

        if (!exceptionThrown) {
            fail("An AuthenticationException should have been thrown during the sendFailedEvent test case.");
        }
    }

    @Test(expected = AuthenticationException.class)
    public void sendFailedEventAfterDoAuthenticateThrowsNonAuthenticationException() {

        abstractAuthenticator = new AbstractAuthenticator() {
            protected Account doAuthenticate(AuthenticationToken token) throws AuthenticationException {
                throw new IllegalArgumentException("not an AuthenticationException subclass");
            }
        };

        AuthenticationToken token = newToken();

        abstractAuthenticator.authenticate(token);
    }

}
