package de.jakop.ngcalsync.oauth;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.CredentialStoreRefreshListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.base.Preconditions;

import de.jakop.ngcalsync.Constants;



/**
 * Implements OAuth authentication "native" flow recommended for installed clients in which the end
 * user must grant access in a web browser and then copy a code into the application.
 *
 * <p>
 * The client_secrets.json file contains your client application's client ID and client secret. They
 * can be found in the <a href="https://code.google.com/apis/console/">Google APIs Console</a>. If
 * this is your first time, click "Create project...". Then, activate the Google APIs your client
 * application uses and agree to the terms of service. Now, click on "API Access", and then on
 * "Create an OAuth 2.0 Client ID...". Enter a product name and click "Next". >Select "Installed
 * application" and click "Create client ID". Finally, enter the "Client ID" and "Client secret"
 * shown under "Client ID for installed applications" into
 * {@code src/main/resources/client_secrets.json}.
 * </p>
 *
 * <p>
 * Warning: the client ID and secret are not secured and are plainly visible to users of your
 * application. It is a hard problem to secure client credentials in installed applications.
 * </p>
 *
 * <p>
 * In this sample code, it attempts to open the browser using {@link Desktop#isDesktopSupported()}.
 * If that fails, on Windows it tries {@code rundll32}. If that fails, it opens the browser
 * specified in {@link #BROWSER}, though note that currently we've only tested this code with Google
 * Chrome (hence this is the default value).
 * </p>
 *
 */
public class GoogleOAuth2DAO {

	private static final String RESOURCE_LOCATION = "/client_secrets.json";

	/**
	 * Browser to open in case {@link Desktop#isDesktopSupported()} is {@code false} or {@code null}
	 * to prompt user to open the URL in their favorite browser.
	 */
	private static final String BROWSER = "google-chrome";

	private final Log log = LogFactory.getLog(getClass());

	private final HttpTransport transport;
	private final JsonFactory jsonFactory;
	private final VerificationCodeReceiver receiver;
	private final File userSecretsFile;

	/** Google client secrets or {@code null} before initialized in {@link #authorize}. */
	private GoogleClientSecrets clientSecrets = null;

	/**
	 * 
	 * @param transport HTTP transport
	 * @param jsonFactory JSON factory
	 * @param receiver verification code receiver
	 * @param userSecretsFile file to store user secrets into
	 */
	public GoogleOAuth2DAO(final HttpTransport transport, final JsonFactory jsonFactory, final VerificationCodeReceiver receiver, final File userSecretsFile) {
		this.transport = transport;
		this.jsonFactory = jsonFactory;
		this.receiver = receiver;
		this.userSecretsFile = userSecretsFile;
	}


	/**
	 * Authorizes the installed application to access user's protected data.
	 *
	 * @param scopes OAuth 2.0 scopes
	 * @throws IOException 
	 */
	private Credential authorizeViaWeb(final Iterable<String> scopes, final String user) throws IOException {
		try {
			// get client secrets
			final GoogleClientSecrets secrets = getClientSecrets(RESOURCE_LOCATION);

			// redirect to an authorization page
			final String redirectUri = receiver.getRedirectUri();
			final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, secrets, scopes).build();
			browse(flow.newAuthorizationUrl().setRedirectUri(redirectUri).build());
			// receive authorization code and exchange it for an access token
			final String code = receiver.waitForCode();
			final GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
			// store credential and return it
			return flow.createAndStoreCredential(response, user);
		} finally {
			receiver.stop();
		}
	}

	/** Open a browser at the given URL. */
	private void browse(final String url) {
		// TODO i18n
		log.info(String.format(Constants.MSG_TRY_TO_OPEN_BROWSER_FOR_URL, url));

		// first try the Java Desktop
		if (Desktop.isDesktopSupported()) {
			final Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.BROWSE)) {
				try {
					desktop.browse(URI.create(url));
					return;
				} catch (final IOException e) {
					// handled below
				}
			}
		}
		// Next try rundll32 (only works on Windows)
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			return;
		} catch (final IOException e) {
			// handled below
		}
		// Next try the requested browser (e.g. "google-chrome")
		if (BROWSER != null) {
			try {
				Runtime.getRuntime().exec(new String[] { BROWSER, url });
				return;
			} catch (final IOException e) {
				// handled below
			}
		}
		// Finally just ask user to open in their browser using copy-paste
		// TODO i18n
		log.info(Constants.MSG_FAILED_TO_OPEN_BROWSER);
		// TODO i18n
		log.info(String.format(Constants.MSG_OPEN_URL_IN_BROWSER, url));
	}

	/**
	 * 
	 * @param scopes
	 * @param user
	 * @return the user's credential containing the access token, if successfully authorized 
	 * @throws IOException
	 */
	public Credential authorize(final Iterable<String> scopes, final String user) throws IOException {
		final FileCredentialStore credentialStore = new FileCredentialStore(userSecretsFile);
		Credential credential = new GoogleCredential.Builder() //
				.setJsonFactory(jsonFactory) //
				.setTransport(transport) //
				.setClientSecrets(getClientSecrets(RESOURCE_LOCATION)) //
				.addRefreshListener(new CredentialStoreRefreshListener(user, credentialStore)) //
				.build();

		final boolean loaded = credentialStore.load(user, credential);
		if (!loaded) {
			credential = authorizeViaWeb(scopes, user);
			credentialStore.store(user, credential);
		}
		return credential;
	}


	class FileCredentialStore implements CredentialStore {

		private final File secretsFile;
		private Properties properties;

		public FileCredentialStore(final File secretsFile) {
			this.secretsFile = secretsFile;
		}

		@Override
		public void delete(final String userId, final Credential credential) {
			getProperties().remove(userId);
			save();
		}

		@Override
		public boolean load(final String userId, final Credential credential) {
			final String keyAccessToken = String.format("%s.accessToken", userId);
			final String keyRefreshToken = String.format("%s.refreshToken", userId);
			credential.setAccessToken((String) getProperties().get(keyAccessToken));
			credential.setRefreshToken((String) getProperties().get(keyRefreshToken));
			return StringUtils.isNotBlank(credential.getAccessToken()) && StringUtils.isNotBlank(credential.getRefreshToken());
		}

		@Override
		public void store(final String userId, final Credential credential) {
			final String keyAccessToken = String.format("%s.accessToken", userId);
			final String keyRefreshToken = String.format("%s.refreshToken", userId);
			getProperties().put(keyAccessToken, credential.getAccessToken());
			getProperties().put(keyRefreshToken, credential.getRefreshToken());
			save();
		}

		private void save() {
			try {
				final FileWriter writer = new FileWriter(secretsFile);
				getProperties().store(writer, null);
				writer.close();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}

		private Properties getProperties() {
			if (properties == null) {
				properties = new Properties();
				try {
					properties.load(new FileReader(secretsFile));
				} catch (final FileNotFoundException e) {
					// empty properties
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
			return properties;

		}
	}

	/**
	 * Loads the Google client secrets (if not already loaded).
	 */
	private GoogleClientSecrets getClientSecrets(final String location) throws IOException {
		if (clientSecrets == null) {
			final InputStream inputStream = getClass().getResourceAsStream(location);
			Preconditions.checkNotNull(inputStream, "missing resource %s", location);
			clientSecrets = GoogleClientSecrets.load(jsonFactory, inputStream);
			Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("[[") && !clientSecrets.getDetails().getClientSecret().startsWith("[["),
					Constants.MSG_ENTER_CLIENT_ID_AND_SECRET, location);
		}
		return clientSecrets;
	}

}
