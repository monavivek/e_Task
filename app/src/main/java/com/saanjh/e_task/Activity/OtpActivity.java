package com.saanjh.e_task.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.saanjh.e_task.R;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class OtpActivity extends AppCompatActivity {

	private static final String TAG = OtpActivity.class.getSimpleName();
	EditText otp;
	Button submit;
	private String mVerificationId;
	private FirebaseAuth mAuth;
	private String OTP, data, mobile;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp);
		mAuth = FirebaseAuth.getInstance();
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Verification");
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mobile = bundle.getString("mobile");
			System.out.println("OTP_Mobile"+mobile);
		}

		Intent intent = getIntent();
		String mobile = intent.getStringExtra("mobile");
		sendVerificationCode(mobile);

		//OtpReader.bind(this, "");
//		Bundle bundle = getIntent().getExtras();
//		if (bundle != null) {
//			data = bundle.getString("otp");
//			mobile = bundle.getString("mobile");
//		}

		otp = (EditText) findViewById(R.id.otp);
		submit = (Button) findViewById(R.id.otp_submit);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				OTP = otp.getText().toString();
//				if (OTP.isEmpty()) {
//					Toast.makeText(getApplicationContext(), "Please fill otp", Toast.LENGTH_LONG).show();
//				} else if (!OTP.equals(data)) {
//					Toast.makeText(getApplicationContext(), "Please fill valid otp", Toast.LENGTH_LONG).show();
//				} else {
//					Intent i = new Intent(getApplicationContext(), ResetPassword.class);
//				//	i.putExtra("mobile", mobile);
//					startActivity(i);
			//	}

				String code = otp.getText().toString().trim();
				if (code.isEmpty() || code.length() < 6) {
					otp.setError("Enter valid code");
					otp.requestFocus();
					return;
				}
				verifyVerificationCode(code);
			}
		});
	}

	private void sendVerificationCode(String mobile) {
		PhoneAuthProvider.getInstance().verifyPhoneNumber(
				"+91" + mobile,
				60,
				TimeUnit.SECONDS,
				TaskExecutors.MAIN_THREAD,mCallbacks);
	}

	private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
		@Override
		public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

			//Getting the code sent by SMS
			String code = phoneAuthCredential.getSmsCode();
			//sometime the code is not detected automatically
			//in this case the code will be null
			//so user has to manually enter the code
			if (code != null) {
				otp.setText(code);
				//verifying the code
				verifyVerificationCode(code);
			}
		}

		@Override
		public void onVerificationFailed(FirebaseException e) {
			Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
			super.onCodeSent(s, forceResendingToken);

			//storing the verification id that is sent to the user
			mVerificationId = s;
		}
	};
	private void verifyVerificationCode(String code) {
		//creating the credential
		PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
		//signing the user
		signInWithPhoneAuthCredential(credential);
	}
	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							//verification successful we will start the profile activity
							Intent intent = new Intent(OtpActivity.this, ResetPassword.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							intent.putExtra("mobile",mobile);
							startActivity(intent);

						} else {
							//verification unsuccessful.. display an error message
							String message = "Somthing is wrong, we will fix it soon...";
							if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
								message = "Invalid code entered...";
							}

//							Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
//							snackbar.setAction("Dismiss", new View.OnClickListener() {
//								@Override
//								public void onClick(View v) {
//
//								}
//							});
//							snackbar.show();
						}
					}
				});

//	@Override
//	public void otpReceived(String message) {
//		Log.e(TAG, message);
//		String code = parseCode(message);//Parse verification code
//		otp.setText(code);//set code in edit text
//	}
//
//	/**
//	 * Parse verification code
//	 *
//	 * @param message sms message
//	 * @return only four numbers from massage string
//	 */
//	private String parseCode(String message) {
//		Pattern p = Pattern.compile("\\b\\d{4}\\b");
//		Matcher m = p.matcher(message);
//		String code = "";
//		while (m.find()) {
//			code = m.group(0);
//		}
//		return code;
//	}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
