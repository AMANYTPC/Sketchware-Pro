package com.besome.sketch.editor.manage.image;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.besome.sketch.beans.ProjectResourceBean;
import com.besome.sketch.lib.base.BaseDialogActivity;
import com.besome.sketch.lib.ui.EasyDeleteEditText;
import com.google.android.gms.analytics.HitBuilders;
import com.sketchware.remod.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import a.a.a.By;
import a.a.a.HB;
import a.a.a.MA;
import a.a.a.Op;
import a.a.a.PB;
import a.a.a.bB;
import a.a.a.iB;
import a.a.a.mB;
import a.a.a.uq;
import a.a.a.wq;
import a.a.a.xB;
import a.a.a.yy;

public class AddImageCollectionActivity extends BaseDialogActivity implements View.OnClickListener {
    private TextView tv_collection;
    private TextView tv_add_photo;
    private ImageView preview;
    private PB imageNameValidator;
    private EditText ed_input_edittext;
    private EasyDeleteEditText ed_input;
    private TextView tv_desc;
    private CheckBox chk_collection;
    private String sc_id;
    private ArrayList<ProjectResourceBean> images;
    private LinearLayout layout_img_inform = null;
    private LinearLayout layout_img_modify = null;
    private TextView tv_imgcnt = null;
    private boolean z = false;
    private String imageFilePath = null;
    private int imageRotationDegrees = 0;
    private int imageExifOrientation = 0;
    private int imageScaleY = 1;
    private int imageScaleX = 1;
    private boolean editing = false;
    private ProjectResourceBean editTarget = null;

    private void flipImageHorizontally() {
        String imageFilePath = this.imageFilePath;
        if (imageFilePath == null || imageFilePath.length() <= 0) {
            return;
        }
        int imageRotationDegrees = this.imageRotationDegrees;
        if (imageRotationDegrees != 90 && imageRotationDegrees != 270) {
            this.imageScaleX *= -1;
        } else {
            this.imageScaleY *= -1;
        }
        refreshPreview();
    }

    private void flipImageVertically() {
        String imageFilePath = this.imageFilePath;
        if (imageFilePath == null || imageFilePath.length() <= 0) {
            return;
        }
        int imageRotationDegrees = this.imageRotationDegrees;
        if (imageRotationDegrees != 90 && imageRotationDegrees != 270) {
            this.imageScaleY *= -1;
        } else {
            this.imageScaleX *= -1;
        }
        refreshPreview();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView preview;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 215 && (preview = this.preview) != null) {
            preview.setEnabled(true);
            if (resultCode == RESULT_OK) {
                this.imageRotationDegrees = 0;
                this.imageScaleY = 1;
                this.imageScaleX = 1;
                this.z = true;
                setImageFromUri(data.getData());
                PB imageNameValidator = this.imageNameValidator;
                if (imageNameValidator != null) {
                    imageNameValidator.a(1);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mB.a()) {
            return;
        }
        int id = v.getId();
        if (id == R.id.cancel_button) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.common_dialog_cancel_button) {
            finish();
        } else if (id == R.id.common_dialog_ok_button) {
            save();
        } else if (id == R.id.img_horizontal) {
            flipImageHorizontally();
        } else if (id == R.id.img_rotate) {
            setImageRotation(this.imageRotationDegrees + 90);
        } else if (id == R.id.img_selected) {
            this.preview.setEnabled(false);
            if (this.editing) {
                return;
            }
            pickImage();
        } else if (id == R.id.img_vertical) {
            flipImageVertically();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        e(xB.b().a(this, R.string.design_manager_image_title_add_image));
        d(xB.b().a(getApplicationContext(), R.string.common_word_save));
        setContentView(R.layout.manage_image_add);
        Intent intent = getIntent();
        this.images = intent.getParcelableArrayListExtra("images");
        this.sc_id = intent.getStringExtra("sc_id");
        this.editTarget = (ProjectResourceBean) intent.getParcelableExtra("edit_target");
        if (this.editTarget != null) {
            this.editing = true;
        }
        this.layout_img_inform = (LinearLayout) findViewById(R.id.layout_img_inform);
        this.layout_img_modify = (LinearLayout) findViewById(R.id.layout_img_modify);
        this.chk_collection = (CheckBox) findViewById(R.id.chk_collection);
        this.chk_collection.setVisibility(View.GONE);
        this.tv_desc = (TextView) findViewById(R.id.tv_desc);
        this.tv_imgcnt = (TextView) findViewById(R.id.tv_imgcnt);
        this.tv_collection = (TextView) findViewById(R.id.tv_collection);
        this.tv_collection.setVisibility(View.GONE);
        this.tv_add_photo = (TextView) findViewById(R.id.tv_add_photo);
        this.preview = (ImageView) findViewById(R.id.img_selected);
        ImageView img_rotate = (ImageView) findViewById(R.id.img_rotate);
        ImageView img_vertical = (ImageView) findViewById(R.id.img_vertical);
        ImageView img_horizontal = (ImageView) findViewById(R.id.img_horizontal);
        this.ed_input = (EasyDeleteEditText) findViewById(R.id.ed_input);
        this.ed_input_edittext = this.ed_input.getEditText();
        this.ed_input_edittext.setPrivateImeOptions("defaultInputmode=english;");
        this.ed_input.setHint(xB.b().a(this, R.string.design_manager_image_hint_enter_image_name));
        this.imageNameValidator = new PB(this, this.ed_input.getTextInputLayout(), uq.b, getReservedImageNames());
        this.imageNameValidator.a(1);
        this.tv_add_photo.setText(xB.b().a(this, R.string.design_manager_image_title_add_image));
        this.preview.setOnClickListener(this);
        img_rotate.setOnClickListener(this);
        img_vertical.setOnClickListener(this);
        img_horizontal.setOnClickListener(this);
        this.r.setOnClickListener(this);
        this.s.setOnClickListener(this);
        this.z = false;
        this.imageRotationDegrees = 0;
        this.imageScaleY = 1;
        this.imageScaleX = 1;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.editing) {
            this.editTarget.isEdited = true;
            e(xB.b().a(this, R.string.design_manager_image_title_edit_image_name));
            this.imageNameValidator = new PB(this, this.ed_input.getTextInputLayout(), uq.b, getReservedImageNames(), this.editTarget.resName);
            this.imageNameValidator.a(1);
            this.ed_input_edittext.setText(this.editTarget.resName);
            this.chk_collection.setVisibility(View.GONE);
            this.tv_collection.setVisibility(View.GONE);
            this.tv_add_photo.setVisibility(View.GONE);
            setImageFromFile(a(this.editTarget));
            this.layout_img_modify.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.d.setScreenName(AddImageCollectionActivity.class.getSimpleName());
        this.d.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private ArrayList<String> getReservedImageNames() {
        ArrayList<String> names = new ArrayList<>();
        names.add("app_icon");
        Iterator<ProjectResourceBean> it = this.images.iterator();
        while (it.hasNext()) {
            names.add(it.next().resName);
        }
        return names;
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, xB.b().a(this, R.string.common_word_choose)), 215);
        } catch (ActivityNotFoundException unused) {
            bB.b(this, xB.b().a(this, R.string.common_error_activity_not_found), bB.TOAST_NORMAL).show();
        }
    }

    private void refreshPreview() {
        this.preview.setImageBitmap(iB.a(iB.a(iB.a(this.imageFilePath, 1024, 1024), this.imageExifOrientation), this.imageRotationDegrees, this.imageScaleX, this.imageScaleY));
    }

    private void save() {
        if (a(this.imageNameValidator)) {
            new Handler().postDelayed(() -> {
                k();
                new SaveAsyncTask(getApplicationContext()).execute();
            }, 500L);
        }
    }

    private void t() {
        TextView tv_desc = this.tv_desc;
        if (tv_desc != null) {
            tv_desc.setVisibility(View.INVISIBLE);
        }
        LinearLayout layout_img_inform = this.layout_img_inform;
        if (layout_img_inform == null || this.layout_img_modify == null || this.tv_imgcnt == null) {
            return;
        }
        layout_img_inform.setVisibility(View.GONE);
        this.layout_img_modify.setVisibility(View.VISIBLE);
        this.tv_imgcnt.setVisibility(View.GONE);
    }

    private boolean a(PB validator) {
        if (!validator.b()) {
            return false;
        }
        if (this.z || this.imageFilePath != null) {
            return true;
        }
        this.tv_desc.startAnimation(AnimationUtils.loadAnimation(this, R.anim.ani_1));
        return false;
    }

    private void setImageFromFile(String path) {
        this.imageFilePath = path;
        this.preview.setImageBitmap(iB.a(path, 1024, 1024));
        int indexOfFilenameExtension = path.lastIndexOf(".");
        if (path.endsWith(".9.png")) {
            indexOfFilenameExtension = path.lastIndexOf(".9.png");
        }
        EditText ed_input_edittext = this.ed_input_edittext;
        if (ed_input_edittext != null && (ed_input_edittext.getText() == null || this.ed_input_edittext.getText().length() <= 0)) {
            this.ed_input_edittext.setText(path.substring(path.lastIndexOf("/") + 1, indexOfFilenameExtension));
        }
        try {
            this.imageExifOrientation = iB.a(path);
            refreshPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
        t();
    }

    private void setImageRotation(int degrees) {
        String imageFilePath = this.imageFilePath;
        if (imageFilePath == null || imageFilePath.length() <= 0) {
            return;
        }
        this.imageRotationDegrees = degrees;
        if (this.imageRotationDegrees == 360) {
            this.imageRotationDegrees = 0;
        }
        refreshPreview();
    }

    private class SaveAsyncTask extends MA {
        public SaveAsyncTask(Context context) {
            super(context);
            AddImageCollectionActivity.this.a(this);
        }

        @Override
        public void a() {
            if (AddImageCollectionActivity.this.editing) {
                bB.a(AddImageCollectionActivity.this.getApplicationContext(), xB.b().a(AddImageCollectionActivity.this.getApplicationContext(), R.string.design_manager_message_edit_complete), bB.TOAST_NORMAL).show();
            } else {
                bB.a(AddImageCollectionActivity.this.getApplicationContext(), xB.b().a(AddImageCollectionActivity.this.getApplicationContext(), R.string.design_manager_message_add_complete), bB.TOAST_NORMAL).show();
            }
            AddImageCollectionActivity.this.h();
            AddImageCollectionActivity.this.finish();
        }

        /* JADX WARN: Removed duplicated region for block: B:31:0x00c4  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0105 A[Catch: all -> 0x0075, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0012, B:7:0x004b, B:12:0x005a, B:13:0x005b, B:18:0x0079, B:19:0x0085, B:21:0x0087, B:34:0x00ff, B:36:0x0105, B:38:0x010b, B:39:0x010f, B:41:0x0115, B:43:0x0121, B:45:0x0132, B:48:0x0142, B:49:0x015b, B:50:0x0160, B:51:0x00ca, B:52:0x00dc, B:53:0x00ee, B:54:0x00a1, B:57:0x00ab, B:60:0x00b5), top: B:2:0x0001, inners: #1, #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0115 A[Catch: all -> 0x0075, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0012, B:7:0x004b, B:12:0x005a, B:13:0x005b, B:18:0x0079, B:19:0x0085, B:21:0x0087, B:34:0x00ff, B:36:0x0105, B:38:0x010b, B:39:0x010f, B:41:0x0115, B:43:0x0121, B:45:0x0132, B:48:0x0142, B:49:0x015b, B:50:0x0160, B:51:0x00ca, B:52:0x00dc, B:53:0x00ee, B:54:0x00a1, B:57:0x00ab, B:60:0x00b5), top: B:2:0x0001, inners: #1, #3 }] */
        /* JADX WARN: Removed duplicated region for block: B:53:0x00ee A[Catch: all -> 0x0075, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0012, B:7:0x004b, B:12:0x005a, B:13:0x005b, B:18:0x0079, B:19:0x0085, B:21:0x0087, B:34:0x00ff, B:36:0x0105, B:38:0x010b, B:39:0x010f, B:41:0x0115, B:43:0x0121, B:45:0x0132, B:48:0x0142, B:49:0x015b, B:50:0x0160, B:51:0x00ca, B:52:0x00dc, B:53:0x00ee, B:54:0x00a1, B:57:0x00ab, B:60:0x00b5), top: B:2:0x0001, inners: #1, #3 }] */
        @Override
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void b() {
            char c;
            String str;
            String a;
            ArrayList<String> a2;
            Iterator<String> it;
            try {
                try {
                    publishProgress("Now processing..");
                    if (AddImageCollectionActivity.this.editing) {
                        Op.g().a(AddImageCollectionActivity.this.editTarget, AddImageCollectionActivity.this.ed_input_edittext.getText().toString(), true);
                        return;
                    }
                    ProjectResourceBean projectResourceBean = new ProjectResourceBean(ProjectResourceBean.PROJECT_RES_TYPE_FILE, AddImageCollectionActivity.this.ed_input_edittext.getText().toString().trim(), AddImageCollectionActivity.this.imageFilePath);
                    projectResourceBean.savedPos = 1;
                    projectResourceBean.isNew = true;
                    projectResourceBean.rotate = AddImageCollectionActivity.this.imageRotationDegrees;
                    projectResourceBean.flipVertical = AddImageCollectionActivity.this.imageScaleY;
                    projectResourceBean.flipHorizontal = AddImageCollectionActivity.this.imageScaleX;
                    try {
                        Op.g().a(AddImageCollectionActivity.this.sc_id, projectResourceBean);
                    } catch (yy e) {
                        throw e;
                    }
                } catch (yy e2) {
                    String message = e2.getMessage();
                    int hashCode = message.hashCode();
                    if (hashCode == -2111590760) {
                        if (message.equals("fail_to_copy")) {
                            c = 2;
                            str = "";
                            if (c != 0) {
                            }
                            a2 = e2.a();
                            if (a2 != null) {
                            }
                            throw new By(a);
                        }
                        c = 65535;
                        str = "";
                        if (c != 0) {
                        }
                        a2 = e2.a();
                        if (a2 != null) {
                        }
                        throw new By(a);
                    }
                    if (hashCode != -1587253668) {
                        if (hashCode == -105163457 && message.equals("duplicate_name")) {
                            c = 0;
                            str = "";
                            if (c != 0) {
                                a = xB.b().a(AddImageCollectionActivity.this.getApplicationContext(), R.string.collection_duplicated_name);
                            } else if (c != 1) {
                                a = c != 2 ? "" : xB.b().a(AddImageCollectionActivity.this.getApplicationContext(), R.string.collection_failed_to_copy);
                            } else {
                                a = xB.b().a(AddImageCollectionActivity.this.getApplicationContext(), R.string.collection_no_exist_file);
                            }
                            a2 = e2.a();
                            if (a2 != null && a2.size() > 0) {
                                it = a2.iterator();
                                while (it.hasNext()) {
                                    String next = it.next();
                                    if (str.length() > 0) {
                                        str = str + ", ";
                                    }
                                    str = str + next;
                                }
                                a = a + "[" + str + "]";
                            }
                            throw new By(a);
                        }
                        c = 65535;
                        str = "";
                        if (c != 0) {
                        }
                        a2 = e2.a();
                        if (a2 != null) {
                            it = a2.iterator();
                            while (it.hasNext()) {
                            }
                            a = a + "[" + str + "]";
                        }
                        throw new By(a);
                    }
                    if (message.equals("file_no_exist")) {
                        c = 1;
                        str = "";
                        if (c != 0) {
                        }
                        a2 = e2.a();
                        if (a2 != null) {
                        }
                        throw new By(a);
                    }
                    c = 65535;
                    str = "";
                    if (c != 0) {
                    }
                    a2 = e2.a();
                    if (a2 != null) {
                    }
                    throw new By(a);
                }
            } catch (Exception e3) {
                e3.printStackTrace();
                throw new By(e3.getMessage());
            }
        }

        @Override
        public void a(String str) {
            AddImageCollectionActivity.this.h();
        }
    }

    private void setImageFromUri(Uri uri) {
        String filePath;
        if (uri == null || (filePath = HB.a(this, uri)) == null) {
            return;
        }
        setImageFromFile(filePath);
    }

    private String a(ProjectResourceBean projectResourceBean) {
        return wq.a() + File.separator + "image" + File.separator + "data" + File.separator + projectResourceBean.resFullName;
    }
}