package com.laishidua.view;

import java.util.concurrent.Callable;

import com.laishidua.common.CallableTask;
import com.laishidua.common.TaskCallback;
import com.laishidua.common.Utils;
import com.laishidua.model.mediator.GhostMySelfieDataMediator;
import com.laishidua.model.mediator.webdata.User;
import com.laishidua.model.mediator.webdata.UserCredentialsStatus;
import com.laishidua.model.mediator.webdata.UserCredentialsStatus.UserCredentialsState;
import com.laishidua.utils.Constants;

import com.laishidua.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 
 * This application uses ButterKnife. AndroidStudio has better support for
 * ButterKnife than Eclipse, but Eclipse was used for consistency with the other
 * courses in the series. If you have trouble getting the login button to work,
 * please follow these directions to enable annotation processing for this
 * Eclipse project:
 * 
 * http://jakewharton.github.io/butterknife/ide-eclipse.html
 * 
 */
public class LoginScreenActivity extends Activity {

	@InjectView(R.id.userName)
	protected EditText userName_;

	@InjectView(R.id.password)
	protected EditText password_;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
		ButterKnife.inject(this);			
	}
	
	@OnClick(R.id.loginButton)
	public void login() {
		
		if(!Utils.checkConn(this)){
			Utils.showToast(this, "Please, check internet connection");
			return;
		}
		
		Constants.user = userName_.getText().toString();
		Constants.pass = password_.getText().toString();
		
		final GhostMySelfieDataMediator gdm = new GhostMySelfieDataMediator();

		CallableTask.invoke(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				Integer gms = gdm.isConnectionEstablished();
				return gms;
			}
		}, new TaskCallback<Integer>() {

			@Override
			public void success(Integer result) {
				// OAuth 2.0 grant was successful and web
				// can talk to the server, open up the selfie listing
				startActivity(new Intent(
						LoginScreenActivity.this,
						GhostMySelfieListActivity.class));
			}

			@Override
			public void error(Exception e) {
				Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);
				
				Utils.showToast(
						LoginScreenActivity.this,
						"Login failed, check your Internet connection and credentials.");
			}
		});
	}

	@OnClick(R.id.signUpButton)
	public void signUp() {
		
		if(!Utils.checkConn(this)){
			Utils.showToast(this, "Please, check internet connection");
			return;
		}
		
		//User created to help to sign up. With correct authorities this 
		//could be safe.
		Constants.user = "host@ghostmyselfie.com";
		Constants.pass = "gffgftfu";
		final String username = userName_.getText().toString();
		final String password = password_.getText().toString();

		if(!Utils.isValidEmailAddress(username)) {
			Utils.showToast(
					LoginScreenActivity.this,
					"Please try with a valid email address.");			
			return;
		}
		
		final GhostMySelfieDataMediator gdm = new GhostMySelfieDataMediator();

		CallableTask.invoke(new Callable<UserCredentialsStatus>() {
			@Override
			public UserCredentialsStatus call() throws Exception {
				User user = new User();
				user.setUsername(username);
				user.setEmail(username);
				UserCredentialsStatus ucs = 
						gdm.checkCredentialsState(user);
                return ucs;
			}
		}, new TaskCallback<UserCredentialsStatus>() {
			@Override
			public void success(UserCredentialsStatus result) {
				
				if (result.getState() == UserCredentialsState.NONE_AVAILABLE){
					Utils.showToast(
							LoginScreenActivity.this,
							"Email already registered. Try another one.");
					return;
				}				

				CallableTask.invoke(new Callable<User>() {
					@Override
					public User call() throws Exception {
							return gdm.addUser(
									User.create(username,
											password,
											username));
					}
				}, new TaskCallback<User>() {

					@Override
					public void success(User result) {
						// OAuth 2.0 grant was successful.
						Utils.showToast(
								LoginScreenActivity.this,
								"User successfully registered!");
					}

					@Override
					public void error(Exception e) {
						Log.e(LoginScreenActivity.class.getName(), "Error sign up in via OAuth.", e);
						
						Utils.showToast(
								LoginScreenActivity.this,
								"Sign Up of user failed, check internet connection or/and try with other credentials.");
					}
				});				
				
				
			}

			@Override
			public void error(Exception e) {
				Log.e(LoginScreenActivity.class.getName(), "Error sign up in via OAuth.", e);
				
				Toast.makeText(
						LoginScreenActivity.this,
						"Error in Sign Up, check internet connection.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}	
	
}
