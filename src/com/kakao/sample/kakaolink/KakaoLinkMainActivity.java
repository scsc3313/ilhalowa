/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.sample.kakaolink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kakao.AppActionBuilder;
import com.kakao.AppActionInfoBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/**
 * 텍스트, 이미지, 링크, 버튼 타입으로 메시지를 구성하여 카카오톡으로 전송한다.
 */
public class KakaoLinkMainActivity extends Activity {
    private KakaoLink kakaoLink;
    private Spinner link, image, button;
    private EditText text;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private final String imageSrc = "http://cfile10.uf.tistory.com/image/246EBD3653452615145F8A";
    private final String weblink = "http://ilowajeju.tistory.com";
    private final String facebooklink = "https://www.facebook.com/ilowajeju?fref=ts";
    /**
     * 메시지를 구성할 텍스트, 이미지, 링크, 버튼을 위한 spinner를 구성한다.
     * 메시지 전송 버튼과 메시지 다시 구성하기 버튼을 만든다.
     * @param savedInstanceState activity 내려가지 전에 저장한 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            text = (EditText) findViewById(R.id.text);
            image = (Spinner) findViewById(R.id.image);
            link = (Spinner) findViewById(R.id.link);
            button = (Spinner) findViewById(R.id.button);

            addListenerOnSendButton();
            addListenerOnClearButton();
        } catch (KakaoParameterException e) {
            alert(e.getMessage());
        }
    }

//Send 버튼
    void addListenerOnSendButton() {
        Button sendButton = (Button) findViewById(R.id.send);

        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textType = String.valueOf(text.getText());
                final String linkType = String.valueOf(link.getSelectedItem());
                final String imageType = String.valueOf(image.getSelectedItem());
                final String buttonType = String.valueOf(button.getSelectedItem());

                final String message = "Text : " + textType +
                    "\nLink : " + linkType +
                    "\nImage : " + imageType +
                    "\nButton : " + buttonType;

                new AlertDialog.Builder(KakaoLinkMainActivity.this)
                    .setTitle(R.string.send_message)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendKakaoTalkLink(textType, linkType, imageType, buttonType);
                            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            }

        });
    }
//클리어 버튼
    private void addListenerOnClearButton() {
        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(KakaoLinkMainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.reset_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                            text.setText("일로와입니다.");
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            }
        });
    }

    @SuppressWarnings("null")
	private void sendKakaoTalkLink(String textType, String linkType, String imageType, String buttonType) {
        try {
        	String linkMessage = text.getText().toString();
//        	Toast toast = Toast.makeText(this, linkMessage, 1);
//        	toast.show();
        	if (linkMessage != null || linkMessage.length() != 0 || linkMessage.equals("") || !TextUtils.isEmpty(linkMessage))
                kakaoTalkLinkMessageBuilder.addText(linkMessage);

            if (imageType.equals(getString(R.string.use_image)))
                kakaoTalkLinkMessageBuilder.addImage(imageSrc, 100, 100);

            // 웹싸이트에 등록한 "http://ilowajeju.tistory.com"을 overwrite함. overwrite는 같은 도메인만 가능.
            if (linkType.equals(getString(R.string.use_weblink))) {
                kakaoTalkLinkMessageBuilder.addWebLink(getString(R.string.ilowalink_weblink), facebooklink);
            }
            // 웹싸이트에 등록한 "http://ilowajeju.tistory.com"으로 이동.
            if (buttonType.equals(getString(R.string.use_webbutton)))
                kakaoTalkLinkMessageBuilder.addWebButton(getString(R.string.ilowalink_webbutton), weblink);

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);
        } catch (KakaoParameterException e) {
            alert(e.getMessage());
        }
    }

    private void alert(String message) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

}
