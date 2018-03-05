/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.android.speech;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.google.cloud.android.speech.camera.GraphicOverlay;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    //test stuff
    private String mSpeechText = "";

    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face, boolean spoken, String speechText) {
        mFace = face;
        mSpeechText = speechText;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        if(face.getLandmarks().size() > 1) {
            //Log.d("LandMarks: ", "" + face.getLandmarks().size());
            Landmark leftMLandmark = null;
            Landmark rightMLandmark = null;
            Landmark bottomMLandmark = null;
            //boolean hasMouth = false;
            for(int i = 0;i < face.getLandmarks().size(); i++){
                if (face.getLandmarks().get(i).getType() == 5){
                    leftMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( leftMLandmark.getPosition().x ), translateY(leftMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    //hasMouth = true;
                }//left mouth
                if (face.getLandmarks().get(i).getType() == 11){
                    rightMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( rightMLandmark.getPosition().x ) , translateY(rightMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    //hasMouth = true;
                }//right mouth

                if (face.getLandmarks().get(i).getType() == 0){
                    bottomMLandmark = face.getLandmarks().get(i);
                    canvas.drawCircle(translateX( bottomMLandmark.getPosition().x ) , translateY(bottomMLandmark.getPosition().y) , FACE_POSITION_RADIUS, mFacePositionPaint);
                    canvas.drawText(mSpeechText,translateX( bottomMLandmark.getPosition().x ) , translateY(bottomMLandmark.getPosition().y) + ID_Y_OFFSET, mIdPaint);
                    //hasMouth = true;
                }//bottom mouth
            }
        }
        //canvas.drawText(mSpeechText, x + ID_X_OFFSET, y + ID_Y_OFFSET + (face.getHeight() * 3 / 4), mIdPaint);
        //Log.d("LandMarks: ", "" + face.getLandmarks());
        ;

        //canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        //canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
