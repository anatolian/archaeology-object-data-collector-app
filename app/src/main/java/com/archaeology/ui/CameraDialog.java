// Camera Dialog
// @author: msenol
package com.archaeology.ui;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import com.archaeology.R;
public class CameraDialog
{
    // interface that will be used by camera dialogs
    interface ApproveDialogCallback
    {
        /**
         * User pressed save
         */
        void onSaveButtonClicked();

        /**
         * User pressed cancel
         */
        void onCancelButtonClicked();
    }

    /**
     * Creating approval dialog to view and approve photos
     * @param AN_ACTIVITY - calling activity
     * @param CALLBACK - function needing photo permissions
     */
    public static AlertDialog createPhotoApprovalDialog(final Activity AN_ACTIVITY,
                                                        final ApproveDialogCallback CALLBACK)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AN_ACTIVITY);
        LayoutInflater inflater = AN_ACTIVITY.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.approve_photo_dialog,null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
            /**
             * User clicked save
             * @param dialog - the alert window
             * @param which - the selected picture
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CALLBACK.onSaveButtonClicked();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * User clicked cancel
             * @param dialog - alert window
             * @param which - selected item
             */
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                CALLBACK.onCancelButtonClicked();
            }
        });
        return builder.create();
    }
}