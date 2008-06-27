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
package org.jsecurity.authc;

import java.net.InetAddress;

/**
 * <p>A simple username/password authentication token to support the most widely-used authentication mechanism.  This
 * class also implements the {@link RememberMeAuthenticationToken RememberMeAuthenticationToken} to support
 * &quot;Remember Me&quot; services across user sessions as well as the
 * {@link InetAuthenticationToken InetAuthenticationToken} to retain the IP address location from where the
 * authentication attempt is occuring.</p>
 *
 * <p>&quot;Remember Me&quot; authentications are disabled by default, but if the application developer wishes to allow
 * it for a login attempt, all it has to do is call {@link #setRememberMe setRememberMe(true)}.  If the underlying
 * <tt>SecurityManager</tt> implementation also supports <tt>RememberMe</tt> services, the user's identity will be
 * remembered across sessions.
 *
 * <p>Note that this class stores a password as a char[] instead of a String
 * (which may seem more logical).  This is because Strings are immutable and their
 * internal value cannot be overwritten - meaning even a nulled String instance might be accessible in memory at a later
 * time (e.g. memory dump).  This is not good for sensitive information such as passwords. For more information, see the
 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/security/jce/JCERefGuide.html#PBEEx">
 * Java Cryptography Extension Reference Guide</a>.</p>
 *
 * <p>To avoid this possibility of later memory access, the application developer should always call
 * {@link #clear() clear()} after using the token to perform a login attempt.</p>
 *
 * @author Jeremy Haile
 * @author Les Hazlewood
 * @since 0.1
 */
public class UsernamePasswordToken implements InetAuthenticationToken, RememberMeAuthenticationToken {

    /*--------------------------------------------
    |             C O N S T A N T S             |
    ============================================*/

    /*--------------------------------------------
    |    I N S T A N C E   V A R I A B L E S    |
    ============================================*/
    private String username;

    private char[] password;

    private boolean rememberMe = false;

    private InetAddress inetAddress;

    /*--------------------------------------------
    |         C O N S T R U C T O R S           |
    ============================================*/

    /**
     * JavaBeans compatible no-arg constructor.
     */
    public UsernamePasswordToken() {
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getInetAddress() inetAddress} and a
     * <tt>rememberMe</tt> default of <tt>false</tt>.
     *
     * @param username the username submitted for authentication
     * @param password the password character array submitted for authentication
     */
    public UsernamePasswordToken(final String username, final char[] password) {
        this(username, password, false, null);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getInetAddress() inetAddress} and
     * a <tt>rememberMe</tt> default of <tt>false</tt>
     *
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     */
    public UsernamePasswordToken(final String username, final String password) {
        this(username, password.toCharArray(), false, null);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, the
     * inetAddress from where the attempt is occurring, and a default <tt>rememberMe</tt> value of <tt>false</tt>
     *
     * @param username    the username submitted for authentication
     * @param password    the password string submitted for authentication
     * @param inetAddress the inetAddress from where the attempt is occuring
     * @since 0.2
     */
    public UsernamePasswordToken(final String username, final char[] password, final InetAddress inetAddress) {
        this(username, password, false, inetAddress);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, the
     * inetAddress from where the attempt is occurring, and a default <tt>rememberMe</tt> value of <tt>false</tt>
     *
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username    the username submitted for authentication
     * @param password    the password string submitted for authentication
     * @param inetAddress the inetAddress from where the attempt is occuring
     * @since 0.2
     */
    public UsernamePasswordToken(final String username, final String password, final InetAddress inetAddress) {
        this(username, password.toCharArray(), false, inetAddress);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, as well as if the user
     * wishes their identity to be remembered across sessions.
     *
     * @param username   the username submitted for authentication
     * @param password   the password string submitted for authentication
     * @param rememberMe if the user wishes their identity to be remembered across sessions
     * @since 0.9
     */
    public UsernamePasswordToken(final String username, final char[] password, final boolean rememberMe) {
        this(username, password, rememberMe, null);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, as well as if the user
     * wishes their identity to be remembered across sessions.
     *
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username   the username submitted for authentication
     * @param password   the password string submitted for authentication
     * @param rememberMe if the user wishes their identity to be remembered across sessions
     * @since 0.9
     */
    public UsernamePasswordToken(final String username, final String password, final boolean rememberMe) {
        this(username, password.toCharArray(), rememberMe, null);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, if the user
     * wishes their identity to be remembered across sessions, and the inetAddress from where the attempt is ocurring.
     *
     * @param username    the username submitted for authentication
     * @param password    the password character array submitted for authentication
     * @param rememberMe  if the user wishes their identity to be remembered across sessions
     * @param inetAddress the inetAddress from where the attempt is occuring
     * @since 0.9
     */
    public UsernamePasswordToken(final String username, final char[] password,
                                 final boolean rememberMe, final InetAddress inetAddress) {

        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
        this.inetAddress = inetAddress;
    }


    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted, if the user
     * wishes their identity to be remembered across sessions, and the inetAddress from where the attempt is ocurring.
     *
     * <p>This is a convience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username    the username submitted for authentication
     * @param password    the password string submitted for authentication
     * @param rememberMe  if the user wishes their identity to be remembered across sessions
     * @param inetAddress the inetAddress from where the attempt is occuring
     * @since 0.9
     */
    public UsernamePasswordToken(final String username, final String password,
                                 final boolean rememberMe, final InetAddress inetAddress) {
        this(username, password.toCharArray(), rememberMe, inetAddress);
    }

    /*--------------------------------------------
    |  A C C E S S O R S / M O D I F I E R S    |
    ============================================*/

    /**
     * Returns the username submitted during an authentication attempt.
     *
     * @return the username submitted during an authentication attempt.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for submission during an authentication attempt.
     *
     * @param username the username to be used for submission during an authentication attempt.
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Returns the password submitted during an authentication attempt as a character array.
     *
     * @return the password submitted during an authentication attempt as a character array.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Sets the password for submission during an authentication attempt.
     *
     * @param password the password to be used for submission during an authentication attemp.
     */
    public void setPassword(char[] password) {
        this.password = password;
    }

    /**
     * Simply returns {@link #getUsername() getUsername()}.
     *
     * @return the {@link #getUsername() username}.
     * @see org.jsecurity.authc.AuthenticationToken#getPrincipal()
     */
    public Object getPrincipal() {
        return getUsername();
    }

    /**
     * Returns the {@link #getPassword() password} char array.
     *
     * @return the {@link #getPassword() password} char array.
     * @see org.jsecurity.authc.AuthenticationToken#getCredentials()
     */
    public Object getCredentials() {
        return getPassword();
    }

    /**
     * Returns the inetAddress from where the authentication attempt occurs.  May be <tt>null</tt> if the inetAddress
     * is unknown or explicitly omitted.  It is up to the Authenticator implementation processing this token if
     * an authentication attempt without an inetAddress is valid or not.
     *
     * <p>(JSecurity's default Authenticator
     * allows <tt>null</tt> IPs to support localhost and proxy server environments).</p>
     *
     * @return the inetAddress from where the authentication attempt occurs, or <tt>null</tt> if it is unknown or
     *         explicitly omitted.
     * @since 0.2
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

    /**
     * Sets the inetAddress from where the authentication attempt occurs.  It is up to the Authenticator
     * implementation processing this token if an authentication attempt without an inetAddress is valid or not.
     *
     * <p>(JSecurity's default Authenticator
     * allows <tt>null</tt> IPs to allow localhost and proxy server environments).</p>
     *
     * @param inetAddress the inetAddress from where the authentication attempt occurs.
     * @since 0.2
     */
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    /**
     * Returns <tt>true</tt> if the submitting user wishes their identity (principal(s)) to be remembered
     * across sessions, <tt>false</tt> otherwise.  Unless overridden, this value is <tt>false</tt> by default.
     *
     * @return <tt>true</tt> if the submitting user wishes their identity (principal(s)) to be remembered
     *         across sessions, <tt>false</tt> otherwise (<tt>false</tt> by default).
     * @since 0.9
     */
    public boolean isRememberMe() {
        return rememberMe;
    }

    /**
     * Sets if the submitting user wishes their identity (pricipal(s)) to be remembered across sessions.  Unless
     * overridden, the default value is <tt>false</tt>, indicating <em>not</em> to be remembered across sessions.
     *
     * @param rememberMe value inidicating if the user wishes their identity (principal(s)) to be remembered across
     *                   sessions.
     * @since 0.9
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /*--------------------------------------------
    |               M E T H O D S               |
    ============================================*/

    /**
     * Clears out (nulls) the username, password, rememberMe, and inetAddress.  The password bytes are explicitly set to
     * <tt>0x00</tt> before nulling to eliminate the possibility of memory access at a later time.
     */
    public void clear() {
        this.username = null;
        this.inetAddress = null;
        this.rememberMe = false;

        if (this.password != null) {
            for (int i = 0; i < password.length; i++) {
                this.password[i] = 0x00;
            }
            this.password = null;
        }

    }

    /**
     * Returns the String representation.  It does not include the password in the resulting
     * string for security reasons to prevent accidentially printing out a password
     * that might be widely viewable).
     *
     * @return the String representation of the <tt>UsernamePasswordToken</tt>, omitting
     *         the password.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" - ");
        sb.append(username);
        sb.append(", rememberMe=").append(rememberMe);
        if (inetAddress != null) {
            sb.append(" (").append(inetAddress).append(")");
        }
        return sb.toString();
    }

}