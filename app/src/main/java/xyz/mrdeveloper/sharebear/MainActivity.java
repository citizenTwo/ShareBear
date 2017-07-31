package xyz.mrdeveloper.sharebear;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static xyz.mrdeveloper.sharebear.VerticalPagerAdapter.position;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Post> postsList;
    private ShareDialog shareDialog;
    public static String firstName, lastName, imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shareDialog = new ShareDialog(this);

        Bundle inBundle = getIntent().getExtras();
        firstName = inBundle.get("FirstName").toString();
        lastName = inBundle.get("LastName").toString();
        imageURL = inBundle.get("imageURL").toString();

        Log.d("Check", firstName + "  " + lastName);

        TextView profileName = (TextView) findViewById(R.id.profile_name);
        profileName.setText("Hi, " + firstName + " " + lastName);

        ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

        Log.d("Check", imageURL);

        Glide
                .with(this)
                .load(imageURL)
                .centerCrop()
                .crossFade()
                .thumbnail(0.1f)
                .into(profileImage);

        ImageButton shareFacebook = (ImageButton) findViewById((R.id.share_facebook));
        shareFacebook.setOnClickListener(this);
        ImageButton shareInstagram = (ImageButton) findViewById((R.id.share_instagram));
        shareInstagram.setOnClickListener(this);
        ImageButton shareTwitter = (ImageButton) findViewById((R.id.share_twitter));
        shareTwitter.setOnClickListener(this);
        ImageButton shareLinkedIn = (ImageButton) findViewById((R.id.share_linkedin));
        shareLinkedIn.setOnClickListener(this);
        ImageButton shareWhatsApp = (ImageButton) findViewById((R.id.share_whatsapp));
        shareWhatsApp.setOnClickListener(this);

//        shareFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("http://www.facebook.com/1400364650188123/posts/" + postsList.get(position).id))
//                        .setShareHashtag(new ShareHashtag.Builder().setHashtag("#sitepoint").build())
//                        .build();
//                shareDialog.show(content);
//            }
//        });

        postsList = new ArrayList<>();
        GetPosts();
    }

    private void GetPosts() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "1400364650188123/posts?fields=message,type,full_picture", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        ParseTheShitOut(response);
                    }
                }
        ).executeAsync();
    }

    private void ParseTheShitOut(GraphResponse response) {
        try {
            JSONObject JSONObjectGraphResponse = new JSONObject(String.valueOf(response.getJSONObject()));
            JSONArray JSONArrayGraphResponse = JSONObjectGraphResponse.getJSONArray("data");

            for (int i = 0; i < JSONArrayGraphResponse.length(); i++) {
                JSONObject postData = JSONArrayGraphResponse.getJSONObject(i);

                //get your values
                if ("photo".equals(postData.getString("type")) && postData.has("message") && postData.has("full_picture") && postData.has("id")) {
                    String id = postData.getString("id");
                    String postId = id.substring(id.lastIndexOf('_') + 1);

                    Post post = new Post(postData.getString("message"), postData.getString("full_picture"), postId, this);
                    postsList.add(post);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        verticalViewPager.setAdapter(new VerticalPagerAdapter(this, postsList));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_facebook:
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://www.facebook.com/1400364650188123/posts/" + postsList.get(position - 1).id))
                        .setShareHashtag(new ShareHashtag.Builder().setHashtag("#CampusAmbassadors").build())
                        .build();
                shareDialog.show(content);
                break;

            case R.id.share_instagram:
                String msg = postsList.get(position - 1).caption;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("caption", msg);
                clipboard.setPrimaryClip(clip);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                Uri uri = postsList.get(position - 1).imageUri;
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");

                intent.setPackage("com.instagram.android");
                startActivity(intent);

                break;

            case R.id.share_twitter:

                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                msg = postsList.get(position - 1).caption;
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                intent.setType("text/plain");

                uri = postsList.get(position - 1).imageUri;
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");

                intent.setPackage("com.twitter.android");
                startActivity(intent);

                break;

            case R.id.share_linkedin:
                Intent linkedinIntent = new Intent(Intent.ACTION_SEND);

                msg = postsList.get(position - 1).caption;
                String text = "http://www.facebook.com/1400364650188123/posts/" + postsList.get(position - 1).id;

                linkedinIntent.setType("text/plain");
                linkedinIntent.putExtra(Intent.EXTRA_TEXT, msg + " " + text);

//                uri = postsList.get(position - 1).imageUri;
//                linkedinIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                linkedinIntent.setType("image/*");

                boolean linkedinAppFound = false;
                List<ResolveInfo> matches2 = getPackageManager()
                        .queryIntentActivities(linkedinIntent, 0);

                for (ResolveInfo info : matches2) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.linkedin")) {
                        linkedinIntent.setPackage(info.activityInfo.packageName);
                        linkedinAppFound = true;
                        break;
                    }
                }

                if (linkedinAppFound) {
                    startActivity(linkedinIntent);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"LinkedIn app not Insatlled in your mobile", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.share_whatsapp:
                String whatsAppMessage = "http://www.facebook.com/1400364650188123/posts/" + postsList.get(position - 1).id;
                whatsAppMessage = postsList.get(position - 1).caption;

                //You can read the image from external drive too
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
                intent.setType("text/plain");

                uri = postsList.get(position - 1).imageUri;
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/*");

                intent.setPackage("com.whatsapp");
                startActivity(intent);
                break;

//                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Trip from Voyajo");
//                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("I've found a trip in Voyajo website that might be interested you, ht));
//                emailIntent.setType("text/plain");
//                startActivity(Intent.createChooser(emailIntent, "Send to friend"));
//                break;

        }
    }

//    private void share() {
//        ShareDialog shareDialog = new ShareDialog(this);
//
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("http://www.sitepoint.com"))
//                .setShareHashtag(new ShareHashtag.Builder().setHashtag("#sitepoint").build())
//                .build();
//
//        shareDialog.show(content);
//    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
