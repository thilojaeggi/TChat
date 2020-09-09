package com.thilojaeggi.tchat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.divyanshu.draw.activity.DrawingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class AttachmentBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    StorageReference mStorageRef;;
    private Uri filePath;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int DRAW_IMAGE_REQUEST = 70;
    SharedPreferences sharedPreferences;
    public static AttachmentBottomSheet newInstance() {
        return new AttachmentBottomSheet();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.attachment_bottomsheet, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        view.findViewById(R.id.draw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                    Intent draw = new Intent(getContext(), DrawingActivity.class);
                    startActivityForResult(draw, DRAW_IMAGE_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                }

            }
        });
        view.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadImage();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if(requestCode == DRAW_IMAGE_REQUEST){
            byte[] result = data.getByteArrayExtra("bitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            filePath = getImageUri(getContext(), bitmap);
            uploadImage();
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void uploadImage() {
    storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();
    if (filePath != null) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Hochladen...");
        progressDialog.show();

        final StorageReference ref = storageReference.child("images/" + System.currentTimeMillis());
        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Hochgeladen", Toast.LENGTH_SHORT).show();
                        MessageModel meMod = new MessageModel();
                        String deviceId = Settings.Secure.getString(getActivity().getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        meMod.setName(sharedPreferences.getString("name", null));
                        meMod.setMessage("");
                        meMod.setDeviceId(deviceId);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", sharedPreferences.getString("name", null));
                        hashMap.put("message", "");
                        hashMap.put("deviceId", deviceId);
                        hashMap.put("imageurl", downloadUrl.toString());
                        reference.child("" + System.currentTimeMillis()).setValue(hashMap);
                        dismiss();

                    }
                });
            }
        });
    }
}
}