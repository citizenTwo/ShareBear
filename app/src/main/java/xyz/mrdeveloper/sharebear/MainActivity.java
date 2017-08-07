package xyz.mrdeveloper.sharebear;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static xyz.mrdeveloper.sharebear.VerticalPagerAdapter.photoPosition;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Uri URI;
    boolean endIsHere;
    int viewID;
    ProgressDialog dialog;
    VerticalViewPager verticalViewPager;
    private GraphResponse previousResponse;
    public String firstName, lastName, URL;
    public static boolean isVideoPlaying;
    public ArrayList<Post> postsList;
    public static int position;
    private ShareDialog shareDialog;
    public VerticalPagerAdapter verticalPagerAdapter;
    boolean doubleBackToExitPressedOnce = false;
    boolean justStarted;
    boolean isCancelled;
    DownloadManager downloadManager;

    public ArrayList<Post> funlist;

    //SearchBar
    Toolbar toolbar, searchtollbar;
    Menu search_menu;
    MenuItem item_search;

    RecyclerView searchListView;
    SearchListAdapter searchListAdapter;
//    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*Permissions*/

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            }
        }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*SearchBar*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#00000000"));

//        android.app.ActionBar actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        setSupportActionBar(toolbar);
        setSearchtollbar();

        //Funlist for searchBar Testing...
        funlist = new ArrayList<>();
        Post post = new Post("1","Hello","1","hola");
        funlist.add(post);
        post = new Post("2","Hello","1","hola");
        funlist.add(post);
        post = new Post("3","Hello","1","hola");
        funlist.add(post);
        post = new Post("12","Hello","1","hola");
        funlist.add(post);
        post = new Post("13","Hello","1","hola");
        funlist.add(post);
        post = new Post("123","Hello","1","hola");
        funlist.add(post);
        post = new Post("22","Hello","1","hola");
        funlist.add(post);
        post = new Post("121","Hello","1","hola");
        funlist.add(post);

//        searchView.setOnQueryTextListener(this);
//
        searchListView = (RecyclerView) findViewById(R.id.list_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        searchListView.setLayoutManager(mLayoutManager);
                //        searchView = (SearchView) findViewById(R.id.search_view);

        searchListAdapter = new SearchListAdapter(this, funlist);
        searchListView.setAdapter(searchListAdapter);

//        searchView.setOnQueryTextListener(this);


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        shareDialog = new ShareDialog(this);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        endIsHere = false;
        justStarted = true;
        isCancelled = false;
        isVideoPlaying = false;

        Bundle inBundle = getIntent().getExtras();
        firstName = inBundle.get("FirstName").toString();
        lastName = inBundle.get("LastName").toString();
        URL = inBundle.get("URLs").toString();

        Log.d("Check", firstName + "  " + lastName);

        TextView profileName = (TextView) findViewById(R.id.profile_name);
        profileName.setText("Hi, " + firstName + " " + lastName);

        ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

        postsList = new ArrayList<>();
        verticalViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        verticalPagerAdapter = new VerticalPagerAdapter(this, postsList, this);

        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int currentPage) {
                //currentPage is the position that is currently displayed.
                position = currentPage;
                isVideoPlaying = "video".equals(postsList.get(currentPage).type);

                if (currentPage == postsList.size() - 7) {
                    Log.d("Check", "User wanna read more!");

                    GraphRequest nextResultsRequests = previousResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                    if (nextResultsRequests != null) {
                        nextResultsRequests.setCallback(new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                //your code
                                ParseTheShitOut(response);
                                //save the last GraphResponse you received
                                previousResponse = response;
                                ;
                            }
                        });
                        nextResultsRequests.executeAsync();
                    } else {
                        Log.d("Check", "End is Here!");
                        endIsHere = true;
                    }
                }

                if (currentPage == postsList.size() - 1) {
                    Toast.makeText(getBaseContext(), "That's all from our side :)", Toast.LENGTH_LONG).show();
                }
            }
        });

        verticalViewPager.setAdapter(verticalPagerAdapter);

        Glide
                .with(this)
                .load(URL)
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

        dialog = ProgressDialog.show(this, "Updating", "Getting latest posts...", true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface arg0) {
                isCancelled = true;
                Toast.makeText(getBaseContext(), "Can't update feed :(", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                if (!isCancelled) {
                    Toast.makeText(getBaseContext(), "Feed Updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        GetPosts();
    }

    private void GetPosts() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "1400364650188123/posts?fields=message,type,full_picture,source,attachments{subattachments.limit(100){media{image{src}}}}&limit=20", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        previousResponse = response;
                        ParseTheShitOut(response);
                    }
                }
        ).executeAsync();
    }

    private void ParseTheShitOut(final GraphResponse response) {
        try {
            JSONObject JSONObjectGraphResponse = new JSONObject(String.valueOf(response.getJSONObject()));
            JSONArray JSONArrayGraphResponse = JSONObjectGraphResponse.getJSONArray("data");

            for (int i = 0; i < JSONArrayGraphResponse.length(); ++i) {
                JSONObject postData = JSONArrayGraphResponse.getJSONObject(i);
                String type = postData.getString("type");

                //get your values
                if ("photo".equals(type) && postData.has("message") && postData.has("full_picture") && postData.has("id")) {
                    String id = postData.getString("id");
                    String postId = id.substring(id.lastIndexOf('_') + 1);

                    Post post = new Post(postData.getString("message"), postData.getString("full_picture"), postId, type);
                    postsList.add(post);

                    if (postData.has("attachments")) {
                        JSONObject attachments = postData.getJSONObject("attachments");
                        JSONArray attachmentData = attachments.getJSONArray("data");
                        JSONObject subAttachments = attachmentData.getJSONObject(0);
                        JSONObject subAttachmentData = subAttachments.getJSONObject("subattachments");
                        JSONArray data = subAttachmentData.getJSONArray("data");

                        for (int j = 1; j < data.length(); ++j) {
                            JSONObject subattachments = data.getJSONObject(j);
                            JSONObject subattachmentMedia = subattachments.getJSONObject("media");
                            JSONObject subattachmentImage = subattachmentMedia.getJSONObject("image");

                            postsList.get(postsList.size() - 1).URLs.add(j, subattachmentImage.getString("src"));
                        }
                    }
                    verticalPagerAdapter.notifyDataSetChanged();
                } else if ("video".equals(type) && postData.has("source")) {
                    String message = "";

                    if (!postData.has("message")) {
                        message = "Video by UPES Campus Ambassadors";
                    }

                    Post post = new Post(message, postData.getString("source"), postData.getString("id"), type);
                    postsList.add(post);
                    verticalPagerAdapter.notifyDataSetChanged();
                }
            }

            if (justStarted) {
                justStarted = false;
                dialog.dismiss();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        Log.d("Check", "Position in MainActivity: " + position);
        Log.d("Check", "Photo Position: " + photoPosition);

        viewID = view.getId();

        switch (viewID) {

//            case R.id.share_facebook:
//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("http://www.facebook.com/1400364650188123/posts/" + postsList.get(position).id))
//                        .setShareHashtag(new ShareHashtag.Builder().setHashtag("#CampusAmbassadors").build())
//                        .build();
//                shareDialog.show(content);
//                break;


            default:
                dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
                dialog.setCanceledOnTouchOutside(true);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface arg0) {
                        isCancelled = true;
                        Toast.makeText(getBaseContext(), "Share Failed. Try again", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface arg0) {
                        if (!isCancelled) {
                            Toast.makeText(getBaseContext(), "Ready to share!", Toast.LENGTH_SHORT).show();
                            isCancelled = false;
                        }
                    }
                });

                setUri();
        }
    }

    public void handleOnClick() {
        switch (viewID) {

            case R.id.share_facebook:
                String message;
                message = postsList.get(position).caption;

                //You can read the image from external drive too
                Intent  intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.setType("text/plain");

                Log.d("Video", "" + URI);
                intent.putExtra(Intent.EXTRA_STREAM, URI);

                if ("photo".equals(postsList.get(position).type)) {
                    intent.setType("image/*");
                } else if ("video".equals(postsList.get(position).type)) {
                    intent.setType("video/*");
                }


                boolean FacebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager()
                        .queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.facebook")) {
                        intent.setPackage(info.activityInfo.packageName);
                        FacebookAppFound = true;
                        break;
                    }
                }

                if (FacebookAppFound) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "FaceBook App not Installed in your mobile", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.share_linkedin:
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                String msg = postsList.get(position).caption;
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_STREAM, URI);
                if ("photo".equals(postsList.get(position).type)) {
                    intent.setType("image/*");
                } else if ("video".equals(postsList.get(position).type)) {
                    intent.setType("video/*");
                }

                boolean LinkedInAppFound = false;
                matches = getPackageManager()
                        .queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.linkedin")) {
                        intent.setPackage(info.activityInfo.packageName);
                        LinkedInAppFound = true;
                        break;
                    }
                }

                if (LinkedInAppFound) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Linkedin app not Installed in your mobile", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_instagram:
                msg = postsList.get(position).caption;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("caption", msg);
                clipboard.setPrimaryClip(clip);

                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_STREAM, URI);
                if ("photo".equals(postsList.get(position).type)) {
                    intent.setType("image/*");
                } else if ("video".equals(postsList.get(position).type)) {
                    intent.setType("video/*");
                }

                boolean instagramAppFound = false;
                matches = getPackageManager()
                        .queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.instagram")) {
                        intent.setPackage(info.activityInfo.packageName);
                        instagramAppFound = true;
                        break;
                    }
                }

                if (instagramAppFound) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Instagram app not Installed in your mobile", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_twitter:

                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                msg = postsList.get(position).caption;
                intent.putExtra(Intent.EXTRA_TEXT, msg);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_STREAM, URI);
                if ("photo".equals(postsList.get(position).type)) {
                    intent.setType("image/*");
                } else if ("video".equals(postsList.get(position).type)) {
                    intent.setType("video/*");
                }

                boolean twitterAppFound = false;
                matches = getPackageManager()
                        .queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.twitter")) {
                        intent.setPackage(info.activityInfo.packageName);
                        twitterAppFound = true;
                        break;
                    }
                }

                if (twitterAppFound) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Twitter app not Installed in your mobile", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_whatsapp:
                String whatsAppMessage;
                whatsAppMessage = postsList.get(position).caption;

                //You can read the image from external drive too
                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage);
                intent.setType("text/plain");

                Log.d("Video", "" + URI);
                intent.putExtra(Intent.EXTRA_STREAM, URI);

//                intent.setType("image/*");
                if ("photo".equals(postsList.get(position).type)) {
                    intent.setType("image/*");
                } else if ("video".equals(postsList.get(position).type)) {
                    intent.setType("video/*");
                }


                boolean WhatsAppFound = false;
                matches = getPackageManager()
                        .queryIntentActivities(intent, 0);

                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.whatsapp")) {
                        intent.setPackage(info.activityInfo.packageName);
                        WhatsAppFound = true;
                        break;
                    }
                }

                if (WhatsAppFound) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "WhatsApp not Installed in your mobile", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    void setUri() {
        new AsyncTask<Void, Void, Void>() {
            Bitmap theBitmap;
            String path;

            @Override
            protected Void doInBackground(Void... params) {
//                Looper.prepare();
                try {

                    if ("photo".equals(postsList.get(position).type)) {

                        if (photoPosition == 0)
                            photoPosition = 1;

                        Log.d("Minion", "Position : " + (photoPosition - 1));
                        theBitmap = Glide.
                                with(getBaseContext()).
//                                      load(postsList.get(position).URLs.
        load(postsList.get(position).URLs.get(photoPosition - 1)).
                                        asBitmap().
                                        into(500, 500).
                                        get();

                    } else if ("video".equals(postsList.get(position).type)) {

                        int start = 0;
                        int end = 0;

                        String name = postsList.get(position).URLs.get(0);
                        for(int i = 0; name.charAt(i) != '\0'; i++){
                            if(name.charAt(i) == '/') start = i;
                            if(name.charAt(i) == '?') {
                                end = i;
                                break;
                            }
                        }

                        name = name.substring(start + 1, end);

                        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShareBear/" + name;
                        File folder = new File(path);
                        Log.d("Video", "Name of Video : " + name);

                        if(!folder.exists()) {
                            Uri uri = Uri.parse(postsList.get(position).URLs.get(0));
                            DownloadManager.Request request = new DownloadManager.Request(uri);

                            request.setDestinationInExternalPublicDir("/ShareBear", name);
                            request.setVisibleInDownloadsUi(true);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                            downloadManager.enqueue(request);
                        }

                    }

                } catch (final ExecutionException | InterruptedException e) {
                    Log.e("Check", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void dummy) {

                if ("photo".equals(postsList.get(position).type)) {
                    if (null != theBitmap) {
                        Log.d("Check", "Image loaded");
                        URI = getLocalBitmapUri(theBitmap);
                    }
                } else if ("video".equals(postsList.get(position).type)) {

                    File file = new File(path);
                    URI = FileProvider.getUriForFile(MainActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            file);

                    handleOnClick();
//
                    Log.d("Check", "URL : " + postsList.get(position).URLs.get(0));
                    Log.d("Check", "URI : " + URI);
                }

                handleOnClick();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        dialog.dismiss();
    }




    /*SearchBar
    *
    ******************************************************************************************************
    ******************************************************************************************************
    ******************************************************************************************************
    *
    * This sections briefly works for the search bar that is being implemented
    */

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        searchListAdapter.filter(newText);
//        return false;
//    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.action_status:
//                Toast.makeText(this, "Home Status Click", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.action_search:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.searchtoolbar, 1, true, true);
                else
                    searchtollbar.setVisibility(View.VISIBLE);
                    searchListView.setVisibility(View.VISIBLE);
                    item_search.expandActionView();

                return true;
//            case R.id.action_settings:
//                Toast.makeText(this, "Home Settings Click", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSearchtollbar() {
        searchtollbar = (Toolbar) findViewById(R.id.searchtoolbar);
        if (searchtollbar != null) {
            searchtollbar.inflateMenu(R.menu.menu_search);
            search_menu = searchtollbar.getMenu();

            searchtollbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar, 1, true, false);
                    else
                        searchtollbar.setVisibility(View.GONE);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar, 1, true, false);
                    } else
                        searchtollbar.setVisibility(View.GONE);
                        searchListView.setVisibility(View.GONE);
                        searchListAdapter.diaplayList.clear();

                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();


        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }

    public void initSearchView() {
        final SearchView searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);


        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.colorSecondary));


        // set the cursor

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                Log.d("Search", "Entered here");
                searchListAdapter.filter(query);
                Log.i("Search", "Query : " + query);
            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }
}

