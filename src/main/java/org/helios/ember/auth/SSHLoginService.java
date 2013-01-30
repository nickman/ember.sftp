/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.ember.auth;

import java.io.File;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.apache.log4j.Logger;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.MappedLoginService.RolePrincipal;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.UserIdentity.UnauthenticatedUserIdentity;
import org.springframework.beans.factory.InitializingBean;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * <p>Title: SSHLoginService</p>
 * <p>Description: A jetty {@link LoginService} implementation that uses SSH for authentication.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.auth.SSHLoginService</code></p>
 * <p><b>CAUTION!  :</b> This app passes around SSH passwords and private key passphrases which you should NEVER do. It's just a demo until I figure out a better way to do this.<p>
 */

public class SSHLoginService implements LoginService, InitializingBean {
	/** The default identity servuce */
	private final IdentityService defaultIdService = new DefaultIdentityService();
	/** The assigned identity servuce */
	private IdentityService assignedIdService = null;
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());	
	/** The pk repo */
	protected SFTPPrivateKeyRepository pkRepo;

	/**
	 * Sets the pk repository
	 * @param pkRepo the pk repository
	 */
	public void setPkRepo(SFTPPrivateKeyRepository pkRepo) {
		this.pkRepo = pkRepo;
	}

	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#getName()
	 */
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	/** Regex pattern to split username fragments <b><code>&lt;username&gt;[@&lt;host&gt;][:&lt;port&gt;]</code></b>. */
	public static final Pattern USER_NAME_SPLITTER = Pattern.compile("@|:");
	/** Regex pattern to validate the username construct */
	public static final Pattern USER_NAME_VALIDATOR = Pattern.compile("(\\w+)$|(\\w+?)@(.*?)$|(\\w+?)@(.*?):\\d+$");
	/** Regex pattern to extract the prepended port from the credentials */
	public static final Pattern PORT_EXTRACTOR = Pattern.compile("(\\d+)(.*)$");

	/**
	 * 
	 * <p><b><code>username</code></b> can be:<ol>
	 * 	<li><code>username</code></li>
	 * 	<li><code>username@hostname</code></li>
	 * 	<li><code>username@hostname:port</code></li>
	 * </ol></p>
	 * <p>However, a <code>":&lt;port&gt;"</code> in the username will be parsed out (in the browser ?) and prepended to the credentials as <code>"&lt;port&gt;:"</code> 
	 * so technically #3 will never been seen, so:<ul>
	 * 	<li>If we see #1, strip and ignore (or error out) on a leading <code>":&lt;port&gt;"</code> in the credentials</li>
	 *  <li>If we see #2, check the credentials a leading <code>":&lt;port&gt;"</code> and strip it out.</li>
	 * </ul></p>
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#login(java.lang.String, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UserIdentity login(String username, Object credentials) {
		SessionLogin sessionLogin = SessionLogin.newSessionLogin(pkRepo.getJSch(), username, credentials);
		if(sessionLogin==null) return null;
		if(!sessionLogin.login(5000)) {  // should be a param
			return null;
		}
		// ===== user authenticated, set up subject and principal
		Subject subject = new Subject();
		subject.getPrincipals().add(new RolePrincipal("foo"));
		
		if(subject.getPrivateCredentials().isEmpty()) {
			subject.getPrivateCredentials().add(new HashMap<String, String>(Collections.singletonMap(sessionLogin.getSessionKey(), sessionLogin.getPassword())));
			subject.getPublicCredentials().add(new HashMap<String, SessionLogin>(Collections.singletonMap(sessionLogin.getSessionKey(), sessionLogin)));
		} else {
			((HashMap<String, String>)subject.getPrivateCredentials().iterator().next()).put(sessionLogin.getSessionKey(), sessionLogin.getPassword());
			((HashMap<String, SessionLogin>)subject.getPublicCredentials().iterator().next()).put(sessionLogin.getSessionKey(), sessionLogin);
		}
		subject.getPrivateCredentials().add(sessionLogin.getPassword());
		subject.getPublicCredentials().add(sessionLogin.getSession());
		return new DefaultUserIdentity(subject, sessionLogin, new String[]{"foo"});
	}
	
//	if(session.getAttribute("auth") == null)   {
//        response.setStatus(401);
//        response.setHeader("WWW-Authenticate", "basic realm=\"Auth (" + session.getCreationTime() + ")\"" );
//        session.setAttribute("auth", Boolean.TRUE);
//        writer.println("Login Required");
//        return;
//    }	

    /**
     * Return an {@link UnauthenticatedUserIdentity} for the passed username
     * @param username The invalid username
     * @return an {@link UnauthenticatedUserIdentity} for the passed username
     */
    protected UserIdentity invalidUser(final String username) {
    	return  new UnauthenticatedUserIdentity()     {
    		@Override
            public Subject getSubject(){
                return null;
            }
    		@Override
            public Principal getUserPrincipal() {
                return null;
            }
    		@Override
            public boolean isUserInRole(String role, Scope scope) {
                return false;
            }            
            @Override
            public String toString() {
                return "Invalid Username [" + username + "]";
            }
        };	
 
    }
    
    /**
     * <p>Title: SessionLogin</p>
     * <p>Description: Represents a parsed out SFTP endpoint with a username and credential</p> 
     * <p>Company: Helios Development Group LLC</p>
     * @author Whitehead (nwhitehead AT heliosdev DOT org)
     * <p><code>org.helios.ember.auth.SSHLoginService.SessionLogin</code></p>
     */
    public static class SessionLogin implements Principal, UserInfo {
    	/** The user name */
    	private final String username;
    	/** The user credentials */
    	private final String password;    	
    	/** The SSH host to connect to  */
    	private final String host;
    	/** The SSH port to connect to  */
    	private final int port;
    	/** A JSch session created for this session login */
    	private Session session = null;
    	/** The most recent connect exception */
    	private Exception connectException = null;
    	/** The pk equipped JSch */
    	private JSch jsch = null;
    	
    	/**
    	 * Returns a key that uniquely identifies this session
    	 * @return a key that uniquely identifies this session
    	 */
    	public String getSessionKey() {
    		return new StringBuilder(username).append("@").append(host).append(":").append(port).toString(); 
    	}
    	
    	/**
    	 * Returns the JSch session or null if not connected
    	 * @return the JSch session or null if not connected
    	 */
    	public Session getSession() {
			return session;
		}

		/**
		 * Returns the most recent connect exception or null
		 * @return the most recent connect exception or null
		 */
		public Exception getConnectException() {
			return connectException;
		}


		/** Static class logger */
    	private static final Logger LOG = Logger.getLogger(SessionLogin.class);
    	
    	/**
    	 * Creates a new SessionLogin
    	 * @param username The session username provided by the browser
    	 * @param credentials The session credentials provided by the browser
    	 * @return a new SessionLogin
    	 */
    	public static SessionLogin newSessionLogin(JSch jsch, String username, Object credentials) {
    		try {
    			return new SessionLogin(jsch, username, credentials);
    		} catch (Exception ex) {
    			return null;
    		}
    	}
    	
    	/**
    	 * Connects this session login
    	 * @param timeout The timeout of the connection attempt in ms.
    	 * @return true if the connect succeeded, false otherwise
    	 */
    	public boolean login(int timeout) {
    		if(session!=null && session.isConnected()) return true;
    		try {
    			session = jsch.getSession(getName(), getHost(), getPort());
    			session.setUserInfo(this);
    			session.setDaemonThread(true);
    			session.connect(timeout);    			
    			connectException = null;
    			LOG.info("Session logged in [" + this + "]");
    			return true;
    		} catch (Exception ex) {
    			connectException = ex;
    			session = null;
    			return false;
    		}    		
    	}
    	
       	/**
    	 * Connects this session login with no timeout
    	 * @return true if the connect succeeded, false otherwise
    	 */
    	public boolean login() {
    		return login(0);
    	}
    	
    	/**
    	 * Logs out this session
    	 */
    	public void logout() {
    		if(session!=null) {
    			try { session.disconnect(); } catch (Exception ex) {}
    			LOG.info("Session logged out [" + this + "]");
    			session = null;
    		}
    	}
    	
    	
    	private SessionLogin(JSch jsch, String username, Object credentials) {
    		if(username==null || username.trim().isEmpty()) throw new IllegalArgumentException("The passed user name was null or empty", new Throwable());
    		username = username.trim();
    		this.jsch = jsch;
    		if(!USER_NAME_VALIDATOR.matcher(username).matches()) {
    			throw new RuntimeException("Invalid user name [" + username + "]", new Throwable());
    		}
    		String[] frags = USER_NAME_SPLITTER.split(username);
    		this.username = frags[0];
    		switch(frags.length) {
    			case 1:    				
    				host = "127.0.0.1";    				
    				break;
    			case 2:
    				host = frags[1];    				
    				break;
    			case 3:  // unlikely but putting in anyway
    				host = frags[1];
    				port = Integer.parseInt(frags[2].trim());
    			default:
    				throw new RuntimeException("Invalid user name [" + username + "]", new Throwable());
    				
    		}
    		if(credentials==null) {
    			port = 22;
    			password = "";
    		} else {
    			Matcher matcher = PORT_EXTRACTOR.matcher(credentials.toString()); 
	    		if(!matcher.matches()) {
	    			port = 22;
	    			password = credentials.toString();
	    		} else {
	    			port = Integer.parseInt(matcher.group(1));
	    			password = matcher.group(2);
	    		}
    		}
    	}
    	
		/**
		 * Returns the user name
		 * @return the user name
		 */
    	@Override
		public String getName() {
			return username;
		}

		/**
		 * Returns the user password or passphrase
		 * @return the user password or passphrase
		 */
		@Override
		public String getPassword() {
			return password;
		}

		/**
		 * Returns the host or IP address to connect to
		 * @return the host or IP address to connect to
		 */
		public String getHost() {
			return host;
		}

		/**
		 * Returns the port to connect to
		 * @return the port to connect to
		 */
		public int getPort() {
			return port;
		}
    	
    	

		/**
		 * {@inheritDoc}
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SessionLogin [username=");
			builder.append(username);
			builder.append(", host=");
			builder.append(host);
			builder.append(", port=");
			builder.append(port);
			if(session!=null) {
				builder.append(", serverVersion=").append(session.getServerVersion());
				builder.append(", clientVersion=").append(session.getClientVersion());
			}
			builder.append("]");
			return builder.toString();
		}

		/**
		 * {@inheritDoc}
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result + port;
			result = prime * result
					+ ((username == null) ? 0 : username.hashCode());
			return result;
		}

		/**
		 * {@inheritDoc}
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SessionLogin other = (SessionLogin) obj;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (port != other.port)
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}


		/**
		 * {@inheritDoc}
		 * @see com.jcraft.jsch.UserInfo#getPassphrase()
		 */
		@Override
		public String getPassphrase() {
			return password;
		}


		/**
		 * {@inheritDoc}
		 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
		 */
		@Override
		public boolean promptPassword(String message) {
			//if(LOG.isDebugEnabled()) 
			LOG.info("JSch prompted for password on [" + this + "]");
			return true;
		}


		/**
		 * {@inheritDoc}
		 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
		 */
		@Override
		public boolean promptPassphrase(String message) {
			//if(LOG.isDebugEnabled()) 
			LOG.debug("JSch prompted for passphrase on [" + this + "]");
			return true;
		}


		/**
		 * {@inheritDoc}
		 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
		 */
		@Override
		public boolean promptYesNo(String message) {
			//if(LOG.isDebugEnabled()) 
			LOG.debug("JSch prompted for yes/no [" + message + "] on [" + this + "]");
			return true;
		}


		/**
		 * {@inheritDoc}
		 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
		 */
		@Override
		public void showMessage(String message) {
			//if(LOG.isDebugEnabled()) 
			LOG.debug("JSch showed message [" + message + "] on [" + this + "]");			
		}
    }
    
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#validate(org.eclipse.jetty.server.UserIdentity)
	 */
	@Override
	public boolean validate(UserIdentity user) {
		if(user==null) return false;
		SessionLogin sessionLogin = (SessionLogin)user.getUserPrincipal();
		if(sessionLogin==null) return false;
		return sessionLogin.login();
	}


	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#logout(org.eclipse.jetty.server.UserIdentity)
	 */
	@Override
	public void logout(UserIdentity user) {
		if(user!=null) {
			SessionLogin sessionLogin = (SessionLogin)user.getUserPrincipal();
			if(sessionLogin!=null) {
				sessionLogin.logout();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#getIdentityService()
	 */
	@Override
	public IdentityService getIdentityService() {
		return assignedIdService!=null ? assignedIdService : defaultIdService;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.security.LoginService#setIdentityService(org.eclipse.jetty.security.IdentityService)
	 */
	@Override
	public void setIdentityService(IdentityService service) {
		assignedIdService = service;
	}


	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		File[] pkFiles = pkRepo.getPkFiles();
		
		
	}
	

}
