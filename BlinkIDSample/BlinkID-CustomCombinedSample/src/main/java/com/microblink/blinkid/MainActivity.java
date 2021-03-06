package com.microblink.blinkid;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import com.microblink.BaseMenuActivity;
import com.microblink.MenuListItem;
import com.microblink.entities.recognizers.Recognizer;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkid.austria.AustriaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.croatia.CroatiaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.czechia.CzechiaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.germany.GermanyCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.jordan.JordanCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.mrtd.MrtdCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.paymentcard.PaymentCardCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.poland.PolandCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.serbia.SerbiaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.singapore.SingaporeCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.slovakia.SlovakiaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.slovenia.SloveniaCombinedRecognizer;
import com.microblink.entities.recognizers.blinkid.usdl.UsdlCombinedRecognizer;
import com.microblink.hardware.camera.CameraType;
import com.microblink.result.ResultActivity;
import com.microblink.util.ImageSettings;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseMenuActivity {

    public static final int MY_BLINKID_REQUEST_CODE = 0x101;
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if BlinkID is supported on the device
        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkID is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected List<MenuListItem> createMenuListItems() {
        List<MenuListItem> items = new ArrayList<>();

        items.add(createMenuListItem("MRTD combined", ImageSettings.enableAllImages(new MrtdCombinedRecognizer())));
        items.add(createMenuListItem("Austrian combined", ImageSettings.enableAllImages(new AustriaCombinedRecognizer())));
        items.add(createMenuListItem("Croatian ID combined", ImageSettings.enableAllImages(new CroatiaCombinedRecognizer())));
        items.add(createMenuListItem("Czech ID combined", ImageSettings.enableAllImages(new CzechiaCombinedRecognizer())));
        items.add(createMenuListItem("German combined", ImageSettings.enableAllImages(new GermanyCombinedRecognizer())));
        items.add(createMenuListItem("Jordan ID combined", ImageSettings.enableAllImages(new JordanCombinedRecognizer())));
        items.add(createMenuListItem("Payment Card combined", ImageSettings.enableAllImages(new PaymentCardCombinedRecognizer())));
        items.add(createMenuListItem("Polish ID combined", ImageSettings.enableAllImages(new PolandCombinedRecognizer())));
        items.add(createMenuListItem("Serbian ID combined", ImageSettings.enableAllImages(new SerbiaCombinedRecognizer())));
        items.add(createMenuListItem("Singapore ID combined", ImageSettings.enableAllImages(new SingaporeCombinedRecognizer())));
        items.add(createMenuListItem("Slovak ID combined", ImageSettings.enableAllImages(new SlovakiaCombinedRecognizer())));
        items.add(createMenuListItem("Slovenian ID combined", ImageSettings.enableAllImages(new SloveniaCombinedRecognizer())));
        items.add(createMenuListItem("US / Canada DL combined", ImageSettings.enableAllImages(new UsdlCombinedRecognizer())));

        return items;
    }

    private MenuListItem createMenuListItem(String title, final Recognizer recognizer) {
        return new MenuListItem(title, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, CustomVerificationFlowActivity.class);
                mRecognizerBundle = new RecognizerBundle(recognizer);
                mRecognizerBundle.saveToIntent(intent);
                intent.putExtra(CustomVerificationFlowActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
                intent.putExtra(CustomVerificationFlowActivity.EXTRAS_COMBINED_CAMERA_TYPE, (Parcelable) CameraType.CAMERA_BACKFACE);
                startActivityForResult(intent, MY_BLINKID_REQUEST_CODE);
            }
        });
    }

    @Override
    protected String getTitleText() {
        return getString(R.string.app_name);
    }

    /**
     * This method is invoked after returning from scan activity. You can obtain
     * scan results here
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != MY_BLINKID_REQUEST_CODE) {
            return;
        }

        // make sure BlinkID activity returned result
        if (resultCode == CustomVerificationFlowActivity.RESULT_OK && data != null) {
            // set intent's component to ResultActivity and pass its contents
            // to ResultActivity. ResultActivity will show how to extract data from result.
            data.setComponent(new ComponentName(this, ResultActivity.class));
            startActivity(data);
        } else {
            // if BlinkID activity did not return result, user has probably
            // pressed Back button and cancelled scanning
            Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

}
