package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageCaptureActivity extends AppCompatActivity {

    private final String TAG = ImageCaptureActivity.class.getSimpleName();

    private ImageView imageView;
    private Button button;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);

        imageView = findViewById(R.id.image_capture_image);
        button = findViewById(R.id.image_capture_savebtn);

        String image_s = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        image_uri = Uri.parse(image_s);
        imageView.setImageURI(image_uri);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO belum save imagenya;
                Log.d(TAG,"Image Saved");
                Intent intent = new Intent(ImageCaptureActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}