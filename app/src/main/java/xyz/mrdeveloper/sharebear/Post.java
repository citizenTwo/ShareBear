package xyz.mrdeveloper.sharebear;

/**
 * Created by Mr. Developer on 01-05-2017.
 */

class Post {
    String caption;
    String imageURL;
    String id;

    Post(String caption, String imageURL, String id) {
        this.caption = caption;
        this.imageURL = imageURL;
        this.id = id;
        //setImageUri();
    }

   /* void setImageUri() {
        new AsyncTask<Void, Void, Void>() {
            Bitmap theBitmap;

            @Override
            protected Void doInBackground(Void... params) {
//                Looper.prepare();
                try {
                    theBitmap = Glide.
                            with(context).
                            load(imageURL).
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
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }*/
}
