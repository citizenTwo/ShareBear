package xyz.mrdeveloper.sharebear;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static xyz.mrdeveloper.sharebear.VerticalPagerAdapter.position;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Post> postsList;
    boolean doubleBackToExitPressedOnce = false;
    private ShareDialog shareDialog;
    Uri imageUri;
    GraphResponse previousResponse;
    int viewID;
    ProgressDialog dialog;
    public static String firstName, lastName, imageURL;
    public static VerticalPagerAdapter verticalPagerAdapter;

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
                .thumbnail(1.0f)
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

        postsList = new ArrayList<>();
        dialog = ProgressDialog.show(this, "Updating", "Getting latest posts...", true);

        GetPosts();
    }

    private void GetPosts() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "1400364650188123/posts?fields=message,type,full_picture&limit=20", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("Check", "Response : " + response);
                        previousResponse = response;
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

                    Post post = new Post(postData.getString("message"), postData.getString("full_picture"), postId);
                    postsList.add(post);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();

        VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        verticalPagerAdapter = new VerticalPagerAdapter(this, postsList, previousResponse);
        verticalViewPager.setAdapter(verticalPagerAdapter);
    }

    @Override
    public void onClick(View view) {

        viewID = view.getId();

        if (viewID == R.id.share_facebook) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://www.facebook.com/1400364650188123/posts/" + postsList.get(position - 1).id))
                    .setShareHashtag(new ShareHashtag.Builder().setHashtag("#CampusAmbassadors").build())
                    .build();
            shareDialog.show(content);
        } else if (viewID == R.id.share_linkedin) {
            Intent linkedinIntent = new Intent(Intent.ACTION_SEND);

            String msg = postsList.get(position - 1).caption;
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
            } else {
                Toast.makeText(MainActivity.this, "LinkedIn app not Installed in your mobile", Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

            Log.d("Check", "Previous URI : " + imageUri);
            setImageUri();
        }
    }

    public void handleOnClick() {
        Log.d("Check", "New URI : " + imageUri);
        switch (viewID) {

            case R.id.share_instagram:
                String msg = postsList.get(position - 1).caption;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("caption", msg);
                clipboard.setPrimaryClip(clip);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
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

                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/*");

                intent.setPackage("com.twitter.android");
                startActivity(intent);

                break;

            case R.id.share_whatsapp:
                String whatsAppMessage = "http://www.facebook.com/1400364650188123/posts/" + postsList.get(position - 1).id;
                whatsAppMessage = postsList.get(position - 1).caption;

                //You can read the image from external drive too
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/*");

                intent.setPackage("com.whatsapp");
                startActivity(intent);
                break;
        }
    }

    void setImageUri() {
        new AsyncTask<Void, Void, Void>() {
            Bitmap theBitmap;

            @Override
            protected Void doInBackground(Void... params) {
//                Looper.prepare();
                try {
                    theBitmap = Glide.
                            with(getBaseContext()).
                            load(postsList.get(position - 1).imageURL).
                            asBitmap().
                            into(500, 500).
                            get();
                } catch (final ExecutionException | InterruptedException e) {
                    Log.e("Check", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void dummy) {
                if (null != theBitmap) {
                    Log.d("Check", "Image loaded");
                    imageUri = getLocalBitmapUri(theBitmap);
                    Log.d("Check", "Transit URI : " + imageUri);
                    dialog.dismiss();
                    handleOnClick();
                }
            }
        }.execute();
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {

//        // Extract Bitmap from ImageView drawable
//        Drawable drawable = imageView.getDrawable();
//        Bitmap bmp = null;
//        if (drawable instanceof BitmapDrawable){
//            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        } else {
//            return null;
//        }
//        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void logout() {
        LoginManager.getInstance().logOut();
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
