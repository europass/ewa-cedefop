/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.tools.ga.steps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.Inject;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.errors.GAJsonResponseError;
import europass.ewa.tools.ga.exceptions.AbstractGAStepExecuteException;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.executor.GAStepsExecutor;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public class GAAuthenticationStep extends AbstractGAStatisticsStep implements GAStepsExecutor{

	private static final String PASS_PHRASE = "notasecret";
	private static final String SERVICE_ACCOUNT_SCOPE = "https://www.googleapis.com/auth/analytics.readonly";
	private static final Logger LOG = LoggerFactory.getLogger(GAAuthenticationStep.class);
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	
	@Inject
	public GAAuthenticationStep(GAExecuteMailSenderImpl mailSender){
		super(mailSender);
	}
	
	@Override
	public boolean execute() throws RuntimeException{
		
		try {
			GoogleCredential credentials = authorize(this.gaStatisticsinfo.getPrivateKeyFileName(), this.gaStatisticsinfo.getClientSecretFileName(), this.gaStatisticsinfo.getServiceAccountID());
			this.gaStatisticsinfo.setCredentials(credentials);
		} catch (Exception e) {
			LOG.error("Could not authorize Google Analytics Application: "+e.getMessage());
			onFailure(new InterruptExecutionException(e));
		}

		return onSuccess();
	}

	@Override
	public boolean onSuccess() {
		return true;
	}

	@Override
	public void onFailure(AbstractGAStepExecuteException e) {

		String causeClassName = e.getThrowableName(true);
		String causeMessage = e.getThrowableCauseMessage();
		
		GAJsonResponseError error = ErrorTypesRecommendations.getError("500", ErrorTypesRecommendations.uknownError, causeMessage);
		
		// Dispatch by class name and configure GAJsonResponseError object
		switch(causeClassName){
		
			case "FileNotFoundException":
				error = ErrorTypesRecommendations.getError("600", ErrorTypesRecommendations.fileNotFound, causeMessage);
				break;
	
			case "IOException":
				error = ErrorTypesRecommendations.getError("601", ErrorTypesRecommendations.endOfFile, causeMessage);
				break;
	
			case "EOFException":
				error = ErrorTypesRecommendations.getError("602", ErrorTypesRecommendations.IOError, causeMessage);
				break;
				
			case "UnrecoverableKeyException":
				error = ErrorTypesRecommendations.getError("700", ErrorTypesRecommendations.privateKeyErrors, causeMessage);
				break;
	
			case "NoSuchAlgorithmException":
				error = ErrorTypesRecommendations.getError("700", ErrorTypesRecommendations.privateKeyErrors, causeMessage);
				break;
	
			case "CertificateException":
				error = ErrorTypesRecommendations.getError("700", ErrorTypesRecommendations.privateKeyErrors, causeMessage);
				break;
	
			case "KeyStoreException":
				error = ErrorTypesRecommendations.getError("700", ErrorTypesRecommendations.privateKeyErrors, causeMessage);
				break;
	
			default:
				break;
		}

		Map<String, GAJsonResponseError> errorsMap = this.mailSender.getErrorsMap();
		
		if(!errorsMap.containsKey(causeClassName)){
			errorsMap.put(causeClassName, error);
		}

		LOG.error("Exception "+causeClassName+" thrown with message '"+causeMessage+"'");
		
		failedExecutionNotify();
	}
	
	@Override
	public void doStep(GAStatisticsInfo info) throws RuntimeException {
		
		super.setGaStatisticsInfo(info);
		this.execute();
		super.doStep(info);
	}
	
	private GoogleCredential authorize(String privateKeyFileName, String clientSecretFileName, String serviceAccountID) throws FileNotFoundException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, RuntimeException{

		List<String> serviceAccountScopes = new ArrayList<String>();
		serviceAccountScopes.add(SERVICE_ACCOUNT_SCOPE);

		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream("/"+privateKeyFileName), PASS_PHRASE.toCharArray());
		PrivateKey pk = (PrivateKey) keystore.getKey("privatekey", PASS_PHRASE.toCharArray());
		
		if(pk == null){
			LOG.error("Private Key not found: ", new IOException("Private Key not found: "+privateKeyFileName));
			throw new RuntimeException();
		}

		GoogleCredential credentials = new GoogleCredential.Builder()
			.setTransport(TRANSPORT)
			.setJsonFactory(JSON_FACTORY)
			.setServiceAccountId(serviceAccountID)
			.setServiceAccountScopes(serviceAccountScopes)
			.setServiceAccountPrivateKey(pk)
			.build();
		
		@SuppressWarnings("unused")
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load( JSON_FACTORY, new InputStreamReader(new FileInputStream("/"+clientSecretFileName)));
		
		return credentials;
	}

	private void failedExecutionNotify() {
		
		super.sendMail(this.gaStatisticsinfo.getYear(),this.gaStatisticsinfo.getMonth(),this.gaStatisticsinfo.getDay());
	}

	@Override
	public void setRetries(int retries) {}

}
